package nl.uva.larissa.repository.couchdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.uva.larissa.CouchDbConnectorFactory;
import nl.uva.larissa.json.ISO8601VerboseDateFormat;
import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.json.model.StatementResult;
import nl.uva.larissa.repository.StatementFilter;
import nl.uva.larissa.repository.StatementFilterUtil;
import nl.uva.larissa.repository.couchdb.CouchDbStatementRepository.QueryStrategy;

/**
 * A CouchDB Query that only uses a map function to get the desired Statements
 * 
 */
public abstract class MapQuery implements StatementResultQuery {

	private String viewName;
	// TODO get from config or whatever;
	private static Integer MAX_LIMIT = 500;

	private final ObjectMapper mapper;

	public MapQuery(String viewName) {
		this.viewName = viewName;
		this.mapper = CouchDbConnectorFactory.objectMapper();
	}

	@Override
	public final StatementResult getQueryResult(CouchDbConnector connector,
			StatementFilter filter, QueryStrategy strategy) {

		int limit = resolveLimit(filter.getLimit());
		boolean isAscending = resolveAscending(filter.getAscending());

		ViewQuery viewQuery = getViewQuery(filter, limit, isAscending);
		viewQuery.staleOk(strategy == QueryStrategy.STALE);
		List<StatementDocument> docs = connector.queryView(viewQuery,
				StatementDocument.class);

		StatementDocument nextDoc = null;
		if (docs.size() > limit) {
			nextDoc = docs.remove(docs.size() - 1);
		}

		StatementResult result = new StatementResult(docsToStatement(docs),
				null);

		if (nextDoc != null) {
			StatementFilter moreFilter = new StatementFilter(filter);
			copyStartKeyValuesToFilter(moreFilter, nextDoc, isAscending);
			moreFilter.setStartId(nextDoc.getId());
			result.setMore(StatementFilterUtil.toMoreUrl(moreFilter));
		}
		return result;
	}

	static boolean resolveAscending(Boolean ascending) {
		return ascending == null ? false : ascending;
	}

	/**
	 * @param filter
	 * @param nextDoc
	 *            sets the values in filter required to form a query from
	 *            nextDoc onwards
	 * @param isAscending 
	 */
	protected abstract void copyStartKeyValuesToFilter(StatementFilter filter,
			StatementDocument nextDoc, boolean isAscending);

	private List<Statement> docsToStatement(List<StatementDocument> docs) {
		List<Statement> result = new ArrayList<>(docs.size());
		for (StatementDocument doc : docs) {
			result.add(doc.getStatement());
		}
		return result;
	}

	private ViewQuery getViewQuery(StatementFilter filter, int limit,
			boolean ascending) {
		ViewQuery viewQuery = new ViewQuery(mapper).designDocId(
				"_design/statements").viewName(viewName);
		Object startKey = ascending ? getStartKey(filter) : getEndKey(filter);
		if (startKey != null) {
			viewQuery.startKey(startKey);
		}
		Object endKey = ascending ? getEndKey(filter) : getStartKey(filter);
		if (endKey != null) {
			viewQuery.endKey(endKey);
		}
		if (!ascending) {
			viewQuery.descending(true);
		}
		// fetch one too many for paging-check
		viewQuery.limit(limit + 1);

		String startId = filter.getStartId();
		if (startId != null) {
			viewQuery.startDocId(startId);
		}
		viewQuery.includeDocs(true);

		viewQuery.staleOkUpdateAfter();

		return viewQuery;
	}

	static int resolveLimit(Integer limit) {
		if (limit == null || limit.intValue() == 0
				|| limit.intValue() > MAX_LIMIT) {
			return MAX_LIMIT;
		}
		return limit.intValue();

	}

	/**
	 * @param date
	 * @return ISO8601 formatted string or null if date was null
	 */
	final static String date2String(Date date) {
		if (date == null) {
			return null;
		}
		return new ISO8601VerboseDateFormat().format(date);
	}

	/**
	 * @param filter
	 * @return the startKey as to be used by org.ektorp.ViewQuery, or null if
	 *         none should be used
	 */
	protected abstract Object getStartKey(StatementFilter filter);

	/**
	 * @param filter
	 * @return the endKey as to be used by org.ektorp.ViewQuery, or null if none
	 *         should be used
	 */
	protected abstract Object getEndKey(StatementFilter filter);

}
