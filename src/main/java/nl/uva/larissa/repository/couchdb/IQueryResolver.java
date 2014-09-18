package nl.uva.larissa.repository.couchdb;

import nl.uva.larissa.repository.StatementFilter;

public interface IQueryResolver {

	StatementResultQuery resolve(StatementFilter filter);

}
