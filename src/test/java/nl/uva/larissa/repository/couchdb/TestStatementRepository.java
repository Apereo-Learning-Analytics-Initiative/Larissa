package nl.uva.larissa.repository.couchdb;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;

import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.json.model.StatementResult;
import nl.uva.larissa.repository.StatementFilter;
import nl.uva.larissa.repository.StatementRepository;
import nl.uva.larissa.repository.couchdb.CouchDbStatementRepository.QueryStrategy;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.mockito.Matchers;

public class TestStatementRepository {
	@Test
	public void testGetStatements() {
		StatementResultQuery mockedQuery = mock(StatementResultQuery.class);

		IQueryResolver mockedResolver = mock(IQueryResolver.class);
		StatementFilter filter = new StatementFilter();
		CouchDbConnector mockedConnector = mock(CouchDbConnector.class);

		when(mockedResolver.resolve(filter)).thenReturn(mockedQuery);

		StatementResult expectedResult = new StatementResult(
				new ArrayList<Statement>(), new Date());

		when(
				mockedQuery.getQueryResult(
						Matchers.any(CouchDbConnector.class), eq(filter),
						Matchers.any(QueryStrategy.class))).thenReturn(
				expectedResult);

		StatementRepository repository = new CouchDbStatementRepository(
				mockedConnector, mockedResolver);

		assertEquals(expectedResult, repository.getStatements(filter));

	}
}
