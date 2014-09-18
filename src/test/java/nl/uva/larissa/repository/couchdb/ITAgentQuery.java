package nl.uva.larissa.repository.couchdb;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import nl.uva.larissa.CouchDbConnectorFactory;
import nl.uva.larissa.json.model.Activity;
import nl.uva.larissa.json.model.Agent;
import nl.uva.larissa.json.model.IFI;
import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.json.model.StatementResult;
import nl.uva.larissa.json.model.Verb;
import nl.uva.larissa.repository.DuplicateIdException;
import nl.uva.larissa.repository.StatementFilter;
import nl.uva.larissa.repository.StatementFilterUtil;
import nl.uva.larissa.repository.VoidingTargetException;
import nl.uva.larissa.repository.couchdb.CouchDbStatementRepository.QueryStrategy;

import org.apache.abdera.i18n.iri.IRI;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ITAgentQuery {

	static CouchDbStatementRepository repository;
	static HttpClient httpClient;

	private static String SERVER_URL = "http://localhost:5984";
	private static String DB_ID = "agentquerytest";

	@BeforeClass
	public static void beforeClass() throws MalformedURLException {

		httpClient = new StdHttpClient.Builder().url(SERVER_URL).build();

		repository = new CouchDbStatementRepository(
				new CouchDbConnectorFactory()
						.createConnector(SERVER_URL, DB_ID),
				new QueryResolver());
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
	public void testQuery() throws DuplicateIdException, VoidingTargetException {
		// the order is important! determines stored-value ordering
		/*
		 * equivalent in CouchDB map [A,A,3] [A,K,1] [A,S,4] [A,K,5] [N,S,2]
		 */
		List<DbKey> dbKeys = Arrays.asList(new DbKey("Aap@boom.bos", "Klimt"),
				new DbKey("Noot@boom.bos", "Slingert"), new DbKey(
						"Aap@boom.bos", "Aapt"), new DbKey("Aap@boom.bos",
						"Slingert"), new DbKey("Aap@boom.bos", "Klimt"));

		populateDb(dbKeys);

		StatementResultQuery query = new AgentActivityQuery();
		StatementFilter filter = new StatementFilter();
		filter.setAgent(getAgent("Aap@boom.bos"));
		filter.setLimit(1);
		StatementResult result = query.getQueryResult(
				repository.getConnector(), filter, QueryStrategy.NORMAL);

		Assert.assertEquals(1, result.getStatements().size());
		Assert.assertNotNull(result.getMore());

		StatementFilter moreFilter;
		for (int i = 0; i < 2; i++) {
			moreFilter = StatementFilterUtil.fromMoreUrl(result.getMore());

			result = getResult(query, moreFilter);

			Assert.assertEquals(1, result.getStatements().size());
			Assert.assertNotNull(result.getMore());
		}
		moreFilter = StatementFilterUtil.fromMoreUrl(result.getMore());

		result = getResult(query, moreFilter);

		Assert.assertEquals(1, result.getStatements().size());
		Assert.assertEquals("", result.getMore());

		Date firstDbStatDate = repository
				.getStatement(dbKeys.get(2).getStatement().getId())
				.getStatements().get(0).getStored();

		filter.setSince(firstDbStatDate);
		filter.setLimit(null);
		result = getResult(query, filter);
		Assert.assertEquals(3, result.getStatements().size());
		Assert.assertEquals("", result.getMore());
	}

	private StatementResult getResult(StatementResultQuery query,
			StatementFilter filter) {
		return query.getQueryResult(repository.getConnector(), filter,
				QueryStrategy.NORMAL);
	}

	private void populateDb(List<DbKey> asList) throws DuplicateIdException,
			VoidingTargetException {
		for (DbKey dbKey : asList) {
			repository.storeStatement(dbKey.getStatement());
		}
	}

	private static Agent getAgent(String email) {
		Agent agent = new Agent();
		agent.setIdentifier(new IFI());
		agent.getIdentifier().setMbox(new IRI("mailto:" + email));
		return agent;
	}

	private static class DbKey {

		private Statement statement;

		public DbKey(String email, String verbName) {
			Agent agent = getAgent(email);
			Verb verb = new Verb();
			verb.setId(new IRI("http://www.uva.nl/verb/" + verbName));
			this.statement = new Statement();
			statement.setId(UUID.randomUUID().toString());
			statement.setActor(agent);
			statement.setVerb(verb);
			Activity activity = new Activity();
			activity.setId(new IRI("http://www.uva.nl/activity/other"));
			statement.setStatementObject(activity);
		}

		public Statement getStatement() {
			return statement;
		}
	}

}
