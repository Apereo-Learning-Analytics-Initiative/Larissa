package nl.uva.larissa.repository.couchdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jersey.repackaged.com.google.common.collect.Lists;
import nl.uva.larissa.CouchDbConnectorFactory;
import nl.uva.larissa.json.model.Activity;
import nl.uva.larissa.json.model.ActivityDefinition;
import nl.uva.larissa.json.model.Agent;
import nl.uva.larissa.json.model.IFI;
import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.json.model.StatementBuilder;
import nl.uva.larissa.json.model.StatementRef;
import nl.uva.larissa.json.model.StatementResult;
import nl.uva.larissa.json.model.Verb;
import nl.uva.larissa.repository.DuplicateIdException;
import nl.uva.larissa.repository.StatementFilter;
import nl.uva.larissa.repository.StatementFilterUtil;
import nl.uva.larissa.repository.UnknownStatementException;
import nl.uva.larissa.repository.VoidingTargetException;

import org.apache.abdera.i18n.iri.IRI;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 *  expects a running CouchDB installation at localhost:5984 and will delete any db there named larissaintegrationtest!! 
 */
public class ITCouchDBStatementRepository {

	static CouchDbStatementRepository repository;
	static HttpClient httpClient;

	private static String SERVER_URL = "http://localhost:5984";
	private static String DB_ID = "larissaintegrationtest";

	@SuppressWarnings("deprecation")
	@BeforeClass
	public static void beforeClass() throws MalformedURLException {

		httpClient = new StdHttpClient.Builder().url(SERVER_URL).build();

		repository = new CouchDbStatementRepository(
				new CouchDbConnectorFactory()
						.createConnector(SERVER_URL, DB_ID),
				new QueryResolver());
		repository.forceBlockingIndexing();
	}

	@AfterClass
	public static void afterClass() {
		repository.shutdown();
		httpClient.shutdown();
	}

	@Before
	public void createDb() {
		repository.create();
	}

	@After
	public void deleteDb() {
		httpClient.delete(SERVER_URL + "/" + DB_ID);
	}

	@Test
	public void testStore() throws DuplicateIdException,
			VoidingTargetException, UnknownStatementException {
		Statement testStatement = createStatement();
		String uuid = UUID.randomUUID().toString();
		testStatement.setId(uuid);
		String storedId = repository.storeStatement(testStatement);
		assertEquals(uuid, storedId);
		Statement storedStatement = repository.getStatement(uuid)
				.getStatements().get(0);
		assertEquals(testStatement.getId(), storedStatement.getId());
	}

	@Test
	public void testStoreMultiple() throws DuplicateIdException,
			VoidingTargetException {
		Statement stat1 = createStatement();
		Statement stat2 = createStatement();
		Statement stat3 = createStatement();
		String id1 = UUID.randomUUID().toString();
		String id2 = UUID.randomUUID().toString();
		String id3 = UUID.randomUUID().toString();
		stat1.setId(id1);
		stat2.setId(id2);
		stat3.setId(id3);

		List<String> storedIds = repository.storeStatements(Lists.newArrayList(
				stat1, stat2));

		assertEquals(Lists.newArrayList(id1, id2), storedIds);

		boolean exceptionThrown = false;

		try {
			repository.storeStatements(Lists.newArrayList(stat1, stat2));
		} catch (DuplicateIdException e) {
			exceptionThrown = true;
			assertEquals(id1, e.getId());
		}
		assertTrue(exceptionThrown);
		try {
			repository.storeStatements(Lists.newArrayList(stat1, stat2, stat3));
		} catch (DuplicateIdException e) {
			exceptionThrown = true;
			assertEquals(id1, e.getId());
		}
		assertTrue(exceptionThrown);
		StatementResult queryResult = repository.getStatement(id3);
		assertEquals("stat3 should not be inserted (1)", 0, queryResult
				.getStatements().size());

		try {
			repository.storeStatements(Lists.newArrayList(stat3, stat1, stat2));
		} catch (DuplicateIdException e) {
			exceptionThrown = true;
			assertEquals(id1, e.getId());
		}
		assertTrue(exceptionThrown);
		queryResult = repository.getStatement(id3);
		assertEquals("stat3 should not be inserted (2)", 0, queryResult
				.getStatements().size());

		stat1.setId(null);
		stat2.setId(null);
		List<String> ids = repository.storeStatements(Lists.newArrayList(stat1,
				stat2, stat3));
		assertNotNull(stat1.getId());
		assertNotNull(stat2.getId());
		queryResult = repository.getStatement(id3);
		assertEquals(1, queryResult.getStatements().size());
		assertEquals(id3, queryResult.getStatements().get(0).getId());
		assertEquals(
				Lists.newArrayList(stat1.getId(), stat2.getId(), stat3.getId()),
				ids);

	}

	@Test
	public void testGetLimitedStatements() throws DuplicateIdException,
			VoidingTargetException, UnknownStatementException {
		for (int i = 0; i < 10; i++) {
			Statement statement = createStatement();
			String uuid = UUID.randomUUID().toString();
			statement.setId(uuid);

			repository.storeStatement(statement);
		}
		StatementFilter filter = new StatementFilter();
		assertEquals(10, repository.getStatements(filter).getStatements()
				.size());

		filter.setLimit(Integer.valueOf(6));
		StatementResult result = repository.getStatements(filter);
		assertEquals(6, result.getStatements().size());

		assertNotNull("should have more URL", result.getMore());
		StatementFilter moreFilter = StatementFilterUtil.fromMoreUrl(result
				.getMore());
		result = repository.getStatements(moreFilter);
		assertEquals(moreFilter.getStartId(), result.getStatements().get(0)
				.getId());
		assertEquals(4, result.getStatements().size());
		assertEquals("should not have more URL", "", result.getMore());
	}

	@Test
	public void testGetSinceStatements() throws DuplicateIdException,
			VoidingTargetException, UnknownStatementException {
		long time = -1;
		for (int i = 0; i < 10; i++) {
			Statement statement = createStatement();
			String uuid = UUID.randomUUID().toString();
			statement.setId(uuid);
			if (i == 5) {
				time = System.currentTimeMillis();
			}
			repository.storeStatement(statement);
		}
		StatementFilter filter = new StatementFilter();
		assertEquals(10, repository.getStatements(filter).getStatements()
				.size());

		filter.setSince(new Date(time + 5));
		StatementResult result = repository.getStatements(filter);
		// since is exclusive (xAPI 1.0.1 7.2.3)
		assertEquals(4, result.getStatements().size());
		assertEquals("should not have more URL", "", result.getMore());

		filter.setLimit(2);
		result = repository.getStatements(filter);
		assertEquals(2, result.getStatements().size());
		assertNotNull("should have more URL", result.getMore());
		StatementFilter moreFilter = StatementFilterUtil.fromMoreUrl(result
				.getMore());
		result = repository.getStatements(moreFilter);

		assertEquals(2, result.getStatements().size());
		assertEquals("should not have more URL", "", result.getMore());
	}

	@Test
	public void testGetVerbId() throws DuplicateIdException,
			InterruptedException, VoidingTargetException,
			UnknownStatementException {
		long starttime = -1;
		long endtime = -1;
		for (int i = 0; i < 10; i++) {
			Statement statement = createStatement();
			String uuid = UUID.randomUUID().toString();
			statement.setId(uuid);
			if (i == 4) {
				starttime = System.currentTimeMillis();
			}
			if (i == 6) {
				statement.getVerb().setId(new IRI("http://gargamel.com/haat"));
			}
			Thread.sleep(1);
			repository.storeStatement(statement);
			Thread.sleep(1);
			if (i == 7) {
				endtime = System.currentTimeMillis();
			}
		}
		StatementFilter filter = new StatementFilter();
		assertEquals(10, repository.getStatements(filter).getStatements()
				.size());

		filter.setVerb(new IRI("http://smurf.com/smurft"));
		StatementResult result = repository.getStatements(filter);
		assertEquals(9, result.getStatements().size());

		filter.setSince(new Date(starttime));
		result = repository.getStatements(filter);
		assertEquals(5, result.getStatements().size());

		filter.setUntil(new Date(endtime));
		result = repository.getStatements(filter);
		assertEquals(3, result.getStatements().size());
	}

	@Test
	public void testGetAgent() throws DuplicateIdException,
			VoidingTargetException, UnknownStatementException {
		for (int i = 0; i < 10; i++) {
			Statement statement = createStatement();
			String uuid = UUID.randomUUID().toString();
			statement.setId(uuid);

			repository.storeStatement(statement);
		}
		StatementFilter filter = new StatementFilter();
		filter.setAgent(createAgent("grotesmurf@smurfendorp"));

		StatementResult result = repository.getStatements(filter);
		assertEquals(10, result.getStatements().size());

		filter.setLimit(6);
		result = repository.getStatements(filter);
		assertEquals(6, result.getStatements().size());
		String moreUrl = result.getMore();
		assertTrue("should have more URL", !"".equals(moreUrl));
		result = repository.getStatements(StatementFilterUtil
				.fromMoreUrl(moreUrl));
		assertEquals(4, result.getStatements().size());

	}

	@Test
	public void testVoid() throws DuplicateIdException, VoidingTargetException,
			UnknownStatementException {
		Statement statement = createStatement();
		String uuid = UUID.randomUUID().toString();
		statement.setId(uuid);

		repository.storeStatement(statement);
		StatementFilter filter = new StatementFilter();

		List<Statement> stats = repository.getStatements(filter)
				.getStatements();

		assertEquals(1, stats.size());
		assertEquals(uuid, stats.get(0).getId());

		Statement voidingStatement = createVoidingStatement(UUID.randomUUID()
				.toString());
		String voidingUuid = UUID.randomUUID().toString();
		voidingStatement.setId(voidingUuid);

		boolean exceptionThrown = false;
		try {
			repository.storeStatement(voidingStatement);
		} catch (VoidingTargetException e) {
			exceptionThrown = true;
		}
		assertTrue("unknown target", exceptionThrown);

		stats = repository.getStatements(filter).getStatements();

		assertEquals(1, stats.size());
		assertEquals(uuid, stats.get(0).getId());

		((StatementRef) voidingStatement.getStatementObject()).setId(uuid);

		repository.storeStatement(voidingStatement);

		stats = repository.getStatements(filter).getStatements();

		assertEquals(1, stats.size());
		assertEquals(voidingUuid, stats.get(0).getId());

		stats = repository.getStatement(uuid).getStatements();
		assertEquals(0, stats.size());

		stats = repository.getVoidedStatement(uuid).getStatements();
		assertEquals(1, stats.size());
		assertEquals(uuid, stats.get(0).getId());

		voidingStatement.setId(UUID.randomUUID().toString());

		exceptionThrown = false;
		try {
			repository.storeStatement(voidingStatement);
		} catch (VoidingTargetException e) {
			exceptionThrown = true;
		}
		assertTrue("already voided", exceptionThrown);

		voidingStatement.setId(UUID.randomUUID().toString());
		((StatementRef) voidingStatement.getStatementObject()).setId(uuid);

		exceptionThrown = false;
		try {
			repository.storeStatement(voidingStatement);
		} catch (VoidingTargetException e) {
			exceptionThrown = true;
		}
		assertTrue("can't void voiding", exceptionThrown);
	}

	@Test
	public void testVoidMultiple() throws DuplicateIdException,
			VoidingTargetException, UnknownStatementException {
		Statement statement1 = createStatement();

		String uuid1 = repository.storeStatement(statement1);

		StatementFilter filter = new StatementFilter();
		filter.setAscending(true);

		List<Statement> stats = repository.getStatements(filter)
				.getStatements();

		assertEquals(1, stats.size());
		assertEquals(uuid1, stats.get(0).getId());

		Statement statement2 = createStatement();
		String uuid2 = UUID.randomUUID().toString();
		statement2.setId(uuid2);

		Statement voidingStatement1 = createVoidingStatement(uuid1);

		List<String> ids = repository.storeStatements(Lists.newArrayList(
				statement2, voidingStatement1));

		assertEquals(2, ids.size());

		stats = repository.getStatements(filter).getStatements();

		assertEquals("voiding and uuid2 should remain", 2, stats.size());
		assertEquals(uuid2, stats.get(0).getId());

		stats = repository.getVoidedStatement(uuid1).getStatements();
		assertEquals(1, stats.size());
	}

	@Test
	public void testGetReferring() throws Exception {
		Statement statZ = StatementBuilder.statement().randomId()
				.actor("ben@uva.nl").verb("passed")
				.activity("explosivestraining")
				.contextWithRegistration(UUID.randomUUID().toString()).build();

		Statement statY = StatementBuilder.statement().randomId()
				.actor("andrew@uva.nl").verb("confirms")
				.statementRef(statZ.getId()).build();

		Statement statX = StatementBuilder.statement().randomId()
				.actor("tom@uva.nl").verb("mentioned")
				.statementRef(statY.getId()).build();

		repository.storeStatement(statZ);
		repository.storeStatement(statY);
		repository.storeStatement(statX);
		// repository.storeStatements(Arrays.asList(statZ, statY, statX));

		StatementFilter filter = new StatementFilter();

		assertEquals(3, repository.getStatements(filter).getStatements().size());

		filter.setVerb(new IRI("passed"));

		StatementResult result = repository.getStatements(filter);

		List<Statement> statements = result.getStatements();
		assertEquals(3, statements.size());
		assertEquals(
				Arrays.asList(statX.getId(), statY.getId(), statZ.getId()),
				Arrays.asList(statements.get(0).getId(), statements.get(1)
						.getId(), statements.get(2).getId()));

		filter.setVerb(null);

		filter.setRegistration(statZ.getContext().getRegistration());

		statements = getStatements(filter);
		assertEquals(3, statements.size());
		assertEquals(
				Arrays.asList(statX.getId(), statY.getId(), statZ.getId()),
				Arrays.asList(statements.get(0).getId(), statements.get(1)
						.getId(), statements.get(2).getId()));

		filter.setRegistration(null);

		filter.setActivity(new IRI("explosivestraining"));

		statements = getStatements(filter);
		assertEquals(3, statements.size());
		assertEquals(
				Arrays.asList(statX.getId(), statY.getId(), statZ.getId()),
				Arrays.asList(statements.get(0).getId(), statements.get(1)
						.getId(), statements.get(2).getId()));

		filter.setVerb(new IRI("confirms"));

		statements = getStatements(filter);
		assertEquals(2, statements.size());
		assertEquals(Arrays.asList(statX.getId(), statY.getId()),
				Arrays.asList(statements.get(0).getId(), statements.get(1)
						.getId()));

		filter.setVerb(new IRI("mentioned"));
		statements = getStatements(filter);
		assertEquals(1, statements.size());
		assertEquals(Arrays.asList(statX.getId()),
				Arrays.asList(statements.get(0).getId()));

		filter.setAgent(createAgent("ben@uva.nl"));
		statements = getStatements(filter);
		assertEquals(1, statements.size());
		assertEquals(Arrays.asList(statX.getId()),
				Arrays.asList(statements.get(0).getId()));

		filter.setAgent(null);
		filter.setActivity(null);
		filter.setRegistration(statZ.getContext().getRegistration());
		// verb is still 'mentioned'
		statements = getStatements(filter);
		assertEquals(1, statements.size());
		assertEquals(Arrays.asList(statX.getId()),
				Arrays.asList(statements.get(0).getId()));

		filter.setVerb(null);
		statements = getStatements(filter);
		assertEquals(3, statements.size());
		assertEquals(
				Arrays.asList(statX.getId(), statY.getId(), statZ.getId()),
				Arrays.asList(statements.get(0).getId(), statements.get(1)
						.getId(), statements.get(2).getId()));

		filter.setVerb(new IRI("confirms"));
		statements = getStatements(filter);
		assertEquals(2, statements.size());
		assertEquals(Arrays.asList(statX.getId(), statY.getId()),
				Arrays.asList(statements.get(0).getId(), statements.get(1)
						.getId()));

	}

	private List<Statement> getStatements(StatementFilter filter) {
		StatementResult result;
		List<Statement> statements;
		result = repository.getStatements(filter);
		statements = result.getStatements();
		return statements;
	}

	Agent createAgent(String emailAddress) {
		Agent agent = new Agent();

		IFI identifier = new IFI();
		identifier.setMbox(new IRI("mailto:" + emailAddress));
		agent.setIdentifier(identifier);

		return agent;
	}

	private Statement createStatement() {
		Statement statement = new Statement();

		statement.setActor(createAgent("grotesmurf@smurfendorp"));

		Verb verb = new Verb();
		verb.setId(new IRI("http://smurf.com/smurft"));
		statement.setVerb(verb);

		Activity activity = new Activity();
		activity.setId(new IRI("http://smurf.com/smurfbesplukken"));
		ActivityDefinition definition = new ActivityDefinition();
		Map<String, String> map = new HashMap<>();
		map.put("NL-nl", "smurfbessen smurfen");
		definition.setName(map);
		activity.setDefinition(definition);
		statement.setStatementObject(activity);

		return statement;
	}

	private Statement createVoidingStatement(String uuid) {
		Statement statement = new Statement();

		statement.setActor(createAgent("voider@thebigvoid"));
		Verb verb = new Verb();
		verb.setId(new IRI("http://adlnet.gov/expapi/verbs/voided"));
		statement.setVerb(verb);
		StatementRef ref = new StatementRef();
		ref.setId(uuid);
		statement.setStatementObject(ref);

		return statement;
	}
}
