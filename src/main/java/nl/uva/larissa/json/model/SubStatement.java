package nl.uva.larissa.json.model;

import java.util.Date;

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
	@Valid
	private Context context;
	private Result result;
	private Date timestamp;

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

	public Context getContext() {
		return context;
	}

	public Result getResult() {
		return result;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
