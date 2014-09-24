package nl.uva.larissa.repository.couchdb;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import nl.uva.larissa.CouchDbConnectorFactory;
import nl.uva.larissa.json.model.StatementResult;
import nl.uva.larissa.repository.DuplicateIdException;
import nl.uva.larissa.repository.StatementFilter;
import nl.uva.larissa.repository.StatementFilterUtil;
import nl.uva.larissa.repository.UnknownStatementException;
import nl.uva.larissa.repository.VoidingTargetException;
import nl.uva.larissa.repository.couchdb.CouchDbStatementRepository.QueryStrategy;

import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ITMapQuery {
	static CouchDbStatementRepository repository;
	static HttpClient httpClient;

	private static String SERVER_URL = "http://localhost:5984";
	private static String DB_ID = "mapquerytest";

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
	public void testOrder() throws DuplicateIdException,
			VoidingTargetException, UnknownStatementException {

		List<DbKey> dbKeys = Arrays.asList(new DbKey("Aap@boom.bos", "Klimt"),
				new DbKey("Aap@boom.bos", "Aapt"), new DbKey("Aap@boom.bos",
						"Slingert"), new DbKey("Aap@boom.bos", "Klimt"));

		populateDb(dbKeys);

		testMoreQuery(dbKeys, true);
		testMoreQuery(dbKeys, false);
		testMoreQueryLimit2(dbKeys, true);
		testMoreQueryLimit2(dbKeys, false);
		testMoreUntil(dbKeys, true);
		testMoreUntil(dbKeys, false);
		testMoreSince(dbKeys, true);
		testMoreSince(dbKeys, false);
	}

	private void testMoreQuery(List<DbKey> dbKeys, boolean ascending) {
		String orderStr = ascending ? "ascending" : "descending";

		MapQuery query = new VerbRegistrationQuery();
		StatementFilter filter = new StatementFilter();

		filter.setLimit(1);
		filter.setAscending(ascending);
		StatementResult result = getResult(query, filter);

		assertEquals(1, result.getStatements().size());

		assertEquals(orderStr + " order (0)",
				id(dbKeys.get(ascending ? 0 : 3)), firstStatementId(result));

		String firstMore = result.getMore();

		assertFalse("there should be more", "".equals(firstMore));

		StatementFilter moreFilter;
		for (int i = 1; i < 3; i++) {
			moreFilter = StatementFilterUtil.fromMoreUrl(result.getMore());

			result = getResult(query, moreFilter);

			assertEquals(1, result.getStatements().size());
			assertEquals(orderStr + " order (" + i + ")",
					id(dbKeys.get(ascending ? i : 3 - i)),
					firstStatementId(result));

			assertFalse("there should be more (" + i + ")",
					"".equals(result.getMore()));
		}
		moreFilter = StatementFilterUtil.fromMoreUrl(result.getMore());

		result = getResult(query, moreFilter);

		assertEquals(1, result.getStatements().size());
		assertEquals(orderStr + " order (3)",
				id(dbKeys.get(ascending ? 3 : 0)), firstStatementId(result));
		assertEquals("", result.getMore());
	}

	private void testMoreQueryLimit2(List<DbKey> dbKeys, boolean ascending) {
		String orderStr = ascending ? "ascending" : "descending";

		MapQuery query = new VerbRegistrationQuery();
		StatementFilter filter = new StatementFilter();

		filter.setLimit(2);
		filter.setAscending(ascending);
		StatementResult result = getResult(query, filter);

		assertEquals(2, result.getStatements().size());

		assertEquals(orderStr + " order (0)",
				id(dbKeys.get(ascending ? 0 : 3)), statementId(result, 0));
		assertEquals(orderStr + " order (1)",
				id(dbKeys.get(ascending ? 1 : 2)), statementId(result, 1));

		String more = result.getMore();
		assertNotEquals("", more);

		filter = StatementFilterUtil.fromMoreUrl(result.getMore());

		result = getResult(query, filter);

		assertEquals(2, result.getStatements().size());

		assertEquals(orderStr + " order (2)",
				id(dbKeys.get(ascending ? 2 : 1)), statementId(result, 0));
		assertEquals(orderStr + " order (3)",
				id(dbKeys.get(ascending ? 3 : 0)), statementId(result, 1));

		more = result.getMore();
		assertEquals("", more);
	}

	private void testMoreUntil(List<DbKey> dbKeys, boolean ascending) {
		String orderStr = ascending ? "ascending" : "descending";

		MapQuery query = new VerbRegistrationQuery();
		StatementFilter filter = new StatementFilter();

		filter.setLimit(1);
		filter.setAscending(ascending);
		filter.setUntil(dbKeys.get(1).getStatement().getStored());
		StatementResult result = getResult(query, filter);

		assertEquals(1, result.getStatements().size());

		assertEquals(orderStr + " order (0)",
				id(dbKeys.get(ascending ? 0 : 1)), firstStatementId(result));

		String more = result.getMore();

		assertNotEquals("there should be more", "", more);

		result = getResult(query, StatementFilterUtil.fromMoreUrl(more));

		assertEquals(1, result.getStatements().size());
		assertEquals(orderStr + " order (1)",
				id(dbKeys.get(ascending ? 1 : 0)), firstStatementId(result));

		assertEquals("", result.getMore());
	}

	private void testMoreSince(List<DbKey> dbKeys, boolean ascending) {
		String orderStr = ascending ? "ascending" : "descending";

		MapQuery query = new VerbRegistrationQuery();
		StatementFilter filter = new StatementFilter();

		filter.setLimit(1);
		filter.setAscending(ascending);
		filter.setSince(dbKeys.get(1).getStatement().getStored());
		StatementResult result = getResult(query, filter);

		assertEquals(1, result.getStatements().size());

		assertEquals(orderStr + " order (0)",
				id(dbKeys.get(ascending ? 1 : 3)), firstStatementId(result));

		String more = result.getMore();

		assertNotEquals("there should be more", "", more);

		result = getResult(query, StatementFilterUtil.fromMoreUrl(more));

		assertEquals(1, result.getStatements().size());
		assertEquals(orderStr + " order (1)", id(dbKeys.get(2)),
				firstStatementId(result));

		more = result.getMore();

		assertNotEquals("there should be more", "", more);

		result = getResult(query, StatementFilterUtil.fromMoreUrl(more));

		assertEquals(1, result.getStatements().size());
		assertEquals(orderStr + " order (2)",
				id(dbKeys.get(ascending ? 3 : 1)), firstStatementId(result));

		more = result.getMore();

		assertEquals("", more);

	}

	private String statementId(StatementResult result, int i) {
		return result.getStatements().get(i).getId();
	}

	private String firstStatementId(StatementResult result) {
		return statementId(result, 0);
	}

	private String id(DbKey dbKey) {
		return dbKey.getStatement().getId();
	}

	private StatementResult getResult(StatementResultQuery query,
			StatementFilter filter) {
		return query.getQueryResult(repository.getConnector(), filter,
				QueryStrategy.NORMAL);
	}

	private void populateDb(List<DbKey> asList) throws DuplicateIdException,
			VoidingTargetException, UnknownStatementException {
		for (DbKey dbKey : asList) {
			repository.storeStatement(dbKey.getStatement());
		}
	}
}
