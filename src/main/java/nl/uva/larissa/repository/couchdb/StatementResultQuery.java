package nl.uva.larissa.repository.couchdb;

import org.ektorp.CouchDbConnector;

import nl.uva.larissa.json.model.StatementResult;
import nl.uva.larissa.repository.StatementFilter;
import nl.uva.larissa.repository.couchdb.CouchDbStatementRepository.QueryStrategy;

public interface StatementResultQuery {

	StatementResult getQueryResult(CouchDbConnector connector,
			StatementFilter filter, QueryStrategy strategy);
}
