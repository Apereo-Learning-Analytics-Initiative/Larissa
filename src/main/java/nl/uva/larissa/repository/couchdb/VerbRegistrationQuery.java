package nl.uva.larissa.repository.couchdb;

import org.ektorp.ComplexKey;

import nl.uva.larissa.repository.StatementFilter;

/*
 * targets view-entries of form [authority, verb, registration, stored]
 * 
 */
public class VerbRegistrationQuery extends MapQuery {

	static final String VIEWNAME = "verbRegistration";

	public VerbRegistrationQuery() {
		super(VIEWNAME);
	}

	@Override
	protected void copyStartKeyValuesToFilter(StatementFilter filter,
			StatementDocument nextDoc, boolean isAscending) {
		filter.setStartId(nextDoc.getId());
		if (isAscending) {
			filter.setSince(nextDoc.getStatement().getStored());
		} else {
			filter.setUntil(nextDoc.getStatement().getStored());
		}
	}

	@Override
	protected Object getStartKey(StatementFilter filter) {
		String since = date2String(filter.getSince());
		return ComplexKey.of("ALL", filter.getVerb(), filter.getRegistration(),
				since);
	}

	@Override
	protected Object getEndKey(StatementFilter filter) {
		Object until = date2String(filter.getUntil());
		if (until == null) {
			until = ComplexKey.emptyObject();
		}
		return ComplexKey.of("ALL", filter.getVerb(), filter.getRegistration(),
				until);
	}

}
