package nl.uva.larissa.repository.couchdb;

import nl.uva.larissa.repository.StatementFilter;

public class QueryResolver implements IQueryResolver {

	static String DESIGN_ID = "_design/statements";

	@Override
	public StatementResultQuery resolve(StatementFilter filter) {

		if (filter.getAgent() != null || filter.getActivity() != null) {
			return new AgentActivityQuery();
		}
		return new VerbRegistrationQuery();

	}
}
