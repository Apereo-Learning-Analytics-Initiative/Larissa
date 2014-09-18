package nl.uva.larissa.json.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import nl.uva.larissa.json.model.validate.NotInstanceOf;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubStatement implements StatementObject {

	@NotNull
	@Valid
	private Actor actor;
	@NotNull
	@Valid
	private Verb verb;
	@NotNull
	@NotInstanceOf(message = "Sub-Statements may not be nested", value = SubStatement.class)
	@JsonProperty("object")
	@Valid
	private StatementObject statementObject;

	public Actor getActor() {
		return actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	public Verb getVerb() {
		return verb;
	}

	public void setVerb(Verb verb) {
		this.verb = verb;
	}

	public StatementObject getStatementObject() {
		return statementObject;
	}

	public void setStatementObject(StatementObject statementObject) {
		this.statementObject = statementObject;
	}
}
