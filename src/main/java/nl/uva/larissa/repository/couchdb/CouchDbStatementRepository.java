package nl.uva.larissa.repository.couchdb;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import jersey.repackaged.com.google.common.collect.Lists;
import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.json.model.StatementRef;
import nl.uva.larissa.json.model.StatementResult;
import nl.uva.larissa.repository.DuplicateIdException;
import nl.uva.larissa.repository.StatementFilter;
import nl.uva.larissa.repository.StatementRepository;
import nl.uva.larissa.repository.Verbs;
import nl.uva.larissa.repository.VoidingTargetException;
import nl.uva.larissa.repository.couchdb.StatementDocument.Type;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.ektorp.DbAccessException;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.DocumentOperationResult;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.support.DesignDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
public class CouchDbStatementRepository implements StatementRepository {

	static enum QueryStrategy {
		NORMAL, STALE;
	}

	private static class VoidPair {
		private StatementDocument voiding;
		private StatementDocument voided;

		public VoidPair(StatementDocument voiding, StatementDocument voided) {
			this.voiding = voiding;
			this.voided = voided;
		}

		public StatementDocument voiding() {
			return voiding;
		}

		public StatementDocument voided() {
			return voided;
		}
	}

	private static final int INDEXING_THRESHOLD = 10;

	private static Logger LOGGER = LoggerFactory
			.getLogger(CouchDbStatementRepository.class);

	private final IQueryResolver queryResolver;

	private final CouchDbConnector connector;
	private final HttpClient httpClient;

	private final Object voidingLock = new Object();
	private final Object storeMultipleLock = new Object();

	private final AtomicBoolean isIndexing = new AtomicBoolean(false);
	private final AtomicReference<Date> lastIndexTime = new AtomicReference<>(
			new Date(0));

	private final ExecutorService indexingExecSvc;

	private int storeCounter;

	private final Runnable indexTrigger = new Runnable() {
		public void run() {
			doIndex();
		}
	};

	private boolean blockingIndexing = false;

	@Inject
	public CouchDbStatementRepository(CouchDbConnector connector,
			IQueryResolver queryResolver) {

		this.queryResolver = queryResolver;
		httpClient = connector.getConnection();

		this.connector = connector;
		indexingExecSvc = Executors.newSingleThreadExecutor();

		create();
		doIndex();
	}

	@Override
	public String storeStatement(Statement statement)
			throws DuplicateIdException, VoidingTargetException {
		if (isVoidingStatement(statement)) {
			return voidAndReturnDocs(statement).voiding().getId();
		}
		StatementDocument cDBStatement = storeStatementAndReturnDoc(statement);
		return cDBStatement.getId();
	}

	private String getVoidingTarget(Statement statement) {
		try {
			return ((StatementRef) statement.getStatementObject()).getId();
		} catch (ClassCastException e) {
			// FIX check during validation
			throw new IllegalArgumentException(
					String.format(
							"Object of voiding Statement %s should be of type StatementRef",
							statement.getId()));
		}
	}

	private boolean isVoidingStatement(Statement statement)
			throws VoidingTargetException {
		return Verbs.VOIDING.iri().equals(
				statement.getVerb().getId().toString());
	}

	private StatementDocument storeStatementAndReturnDoc(Statement statement)
			throws DuplicateIdException {
		StatementDocument cDBStatement = new StatementDocument();
		cDBStatement.setStatement(statement);
		String statementId = statement.getId();
		if (statementId == null) {
			// TODO sequential!
			statement.setId(UUID.randomUUID().toString());
		}
		cDBStatement.setId(statement.getId());
		try {
			statement.setStored(new Date());
			connector.create(cDBStatement);
			if (++storeCounter > INDEXING_THRESHOLD) {
				scheduleIndex();
				storeCounter = 0;
			}
		} catch (UpdateConflictException e) {
			statement.setStored(null);
			throw new DuplicateIdException(e, cDBStatement.getId());
		}
		return cDBStatement;
	}

	@Override
	public StatementResult getStatement(String statementId) {
		StatementDocument doc = getStatementDoc(statementId);
		StatementResult result = new StatementResult(doc == null
				|| doc.getType() == Type.VOIDED ? new ArrayList<Statement>(0)
				: Lists.newArrayList(doc.getStatement()), getLastIndexedDate());
		return result;
	}

	private Date getLastIndexedDate() {
		Date lastDocDate = Util.getStoredDateOfLastStatement(connector);
		Date lastIndexDate = lastIndexTime.get();
		return lastDocDate == null ? lastIndexDate : lastDocDate
				.after(lastIndexDate) ? lastDocDate : lastIndexDate;
	}

	private StatementDocument getStatementDoc(String docId) {
		StatementDocument result;
		try {
			result = connector.get(StatementDocument.class, docId);
			// fixing null due to ambiguous deserialization of id-field
			result.getStatement().setId(result.getId());
		} catch (DocumentNotFoundException e) {
			result = null;
		}
		return result;
	}

	public void shutdown() {
		httpClient.shutdown();
	}

	@Override
	public StatementResult getStatements(StatementFilter filter) {
		LOGGER.trace(filter.toString());
		StatementResultQuery query = queryResolver.resolve(filter);
		if (query == null) {
			throw new IllegalArgumentException("unsupported filter: " + filter);
		}
		Date consistentThrough;
		QueryStrategy strategy;
		if (isIndexing.get() && !blockingIndexing) {
			consistentThrough = getLastIndexedDate();
			strategy = QueryStrategy.STALE;
		} else {
			consistentThrough = new Date();
			strategy = QueryStrategy.NORMAL;
		}
		LOGGER.debug("using strategy " + strategy);
		StatementResult result = query.getQueryResult(connector, filter,
				strategy);

		result.setConsistentThrough(consistentThrough);
		return result;
	}

	public void create() {
		connector.createDatabaseIfNotExists();
		try {
			DesignDocument designDoc = getDesignDocument();
			try {
				connector.get(DesignDocument.class, QueryResolver.DESIGN_ID);
			} catch (DocumentNotFoundException e) {
				connector.create(designDoc);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private DesignDocument getDesignDocument() throws IOException {
		// FIXME reuse mapper from somewhere
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				"statements_designdoc.json");
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.reader(DesignDocument.class).readValue(is);
		} finally {
			is.close();
		}
	}

	CouchDbConnector getConnector() {
		return connector;
	}

	@Override
	/**
	 * xAPI 1.0.1 7.0 Data Transfer
	 * The LRS MUST reject a batch of statements if any statement within that batch is rejected.
	 */
	public List<String> storeStatements(List<Statement> statements)
			throws DuplicateIdException, VoidingTargetException {
		List<StatementDocument> docs = new ArrayList<>(statements.size());
		List<StatementDocument> voidedDocs = new ArrayList<>(statements.size());

		synchronized (storeMultipleLock) {
			try {
				for (Statement statement : statements) {
					if (isVoidingStatement(statement)) {
						VoidPair voidPair = voidAndReturnDocs(statement);
						docs.add(voidPair.voiding());
						voidedDocs.add(voidPair.voided());
					} else {
						docs.add(storeStatementAndReturnDoc(statement));
					}
				}
			} catch (DuplicateIdException | VoidingTargetException e) {
				rollback(docs, voidedDocs);
				throw e;
			}
		}
		List<String> ids = new ArrayList<>(docs.size());
		for (StatementDocument doc : docs) {
			ids.add(doc.getStatement().getId());
		}
		return ids;
	}

	private VoidPair voidAndReturnDocs(Statement statement)
			throws VoidingTargetException, DuplicateIdException {
		String idToVoid = getVoidingTarget(statement);
		List<StatementDocument> voidedDocs = new ArrayList<>(1);

		synchronized (voidingLock) {
			StatementDocument docToVoid = getStatementDoc(idToVoid);
			if (docToVoid == null) {
				throw new VoidingTargetException(String.format(
						"Statement %s cannot be voided as it does not exist.",
						idToVoid));
			}
			if (Type.VOIDED.equals(docToVoid.getType())) {
				throw new VoidingTargetException(
						String.format(
								"Statement %s cannot be voided as it is already voided.",
								idToVoid));
			}
			if (isVoidingStatement(docToVoid.getStatement())) {
				throw new VoidingTargetException(
						String.format(
								"Statement %s cannot be voided as it is a voiding statement.",
								idToVoid));
			}
			try {
				docToVoid.setType(Type.VOIDED);
				connector.update(docToVoid);
				voidedDocs.add(docToVoid);
				return new VoidPair(storeStatementAndReturnDoc(statement),
						docToVoid);
			} catch (DuplicateIdException e) {
				rollback(new ArrayList<StatementDocument>(0), voidedDocs);
				throw e;
			}
		}
	}

	private void rollback(List<StatementDocument> createdDocs,
			List<StatementDocument> voidedDocs) {
		List<Object> bulk = new ArrayList<>(createdDocs.size()
				+ voidedDocs.size());
		for (StatementDocument doc : createdDocs) {
			bulk.add(BulkDeleteDocument.of(doc));
		}
		for (StatementDocument voidedDoc : voidedDocs) {
			voidedDoc.setType(Type.PLAIN);
			bulk.add(voidedDoc);
		}
		if (!bulk.isEmpty()) {
			List<DocumentOperationResult> result = connector.executeBulk(bulk);
			for (DocumentOperationResult docResult : result) {
				if (docResult.isErroneous()) {
					throw new RuntimeException(String.format(
							"rollback failed: %s", result));

				}
			}
		}
	}

	@Override
	public StatementResult getVoidedStatement(String id) {
		StatementDocument doc = getStatementDoc(id);
		StatementResult result = new StatementResult(doc == null
				|| doc.getType() != Type.VOIDED ? new ArrayList<Statement>(0)
				: Lists.newArrayList(doc.getStatement()), getLastIndexedDate());
		return result;
	}

	void scheduleIndex() {
		if (isIndexing.compareAndSet(false, true)) {
			indexingExecSvc.execute(indexTrigger);
		}
	}

	private void doIndex() {
		LOGGER.info("index start");
		doIndexingQueryIgnoreTimeout();
		lastIndexTime.set(new Date());
		LOGGER.info("index stop");
		isIndexing.set(false);

	}

	private void doIndexingQueryIgnoreTimeout() throws DbAccessException {
		try {
			connector.queryView(new ViewQuery()
					.designDocId("_design/statements")
					.viewName(VerbRegistrationQuery.VIEWNAME).limit(1)
					.startKey("").descending(true));
		} catch (DbAccessException e) {
			// TODO review, seems dangerous
			if (e.getCause() instanceof SocketTimeoutException) {
				doIndexingQueryIgnoreTimeout();
			} else {
				throw e;
			}
		}
	}

	@PreDestroy
	void cleanup() {
		indexingExecSvc.shutdownNow();
	}

	@Deprecated
	void forceBlockingIndexing() {
		this.blockingIndexing = true;
	}
}
