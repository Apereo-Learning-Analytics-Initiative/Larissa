package nl.uva.larissa.repository.couchdb;

import java.util.UUID;

import nl.uva.larissa.json.model.Activity;
import nl.uva.larissa.json.model.Agent;
import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.json.model.Verb;

import org.apache.abdera.i18n.iri.IRI;

class DbKey {

	private Statement statement;

	public DbKey(String email, String verbName) {
		Agent agent = ITAgentQuery.getAgent(email);
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