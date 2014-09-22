package nl.uva.larissa.repository.couchdb;

import org.ektorp.ComplexKey;

import nl.uva.larissa.json.model.IFI;
import nl.uva.larissa.repository.StatementFilter;

/*
 * targets view-entries of form [authority,related, agent, verb, registration, stored]
 * 
 * where 'related' is 1 = unrelated, 2 = related_agents, 3 = related_activities, 4 = both related
 * 
 * the backing view does not contain data for queries where agent and verb both are null!
 */
public class AgentActivityQuery extends MapQuery {

	public AgentActivityQuery() {
		super("agentActivity");
	}

	@Override
	protected void copyStartKeyValuesToFilter(StatementFilter filter,
			StatementDocument nextDoc, boolean isAscending) {
		if (isAscending) { 
			filter.setSince(nextDoc.getStatement().getStored());
		} else {
			filter.setUntil(nextDoc.getStatement().getStored());
		}
		filter.setStartId(nextDoc.getId());
	}

	@Override
	protected Object getStartKey(StatementFilter filter) {
		String since = date2String(filter.getSince());
		IFI agentIFI = filter.getAgent() == null ? null : filter.getAgent()
				.getIdentifier();
		return ComplexKey.of("ALL", getRelatedNumber(filter), agentIFI,
				filter.getActivity(), filter.getVerb(),
				filter.getRegistration(), since);
	}

	@Override
	protected Object getEndKey(StatementFilter filter) {
		Object until = date2String(filter.getUntil());
		if (until == null) {
			until = ComplexKey.emptyObject();
		}
		IFI agentIFI = filter.getAgent() == null ? null : filter.getAgent()
				.getIdentifier();
		return ComplexKey.of("ALL", getRelatedNumber(filter), agentIFI,
				filter.getActivity(), filter.getVerb(),
				filter.getRegistration(), until);
	}

	private int getRelatedNumber(StatementFilter filter) {
		boolean relatedAgents = filter.getRelatedAgents() != null
				&& filter.getRelatedAgents().booleanValue();
		boolean relatedActivities = filter.getRelatedActivities() != null
				&& filter.getRelatedActivities().booleanValue();

		if (relatedAgents) {
			if (relatedActivities) {
				return 4;
			}
			return 2;
		}
		if (relatedActivities) {
			return 3;
		}
		return 1;
	}

}
