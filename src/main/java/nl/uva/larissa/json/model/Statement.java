package nl.uva.larissa.json.model;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;

import nl.uva.larissa.json.model.validate.IsUUID;
import nl.uva.larissa.json.model.validate.ValidPlatformUsage;
import nl.uva.larissa.json.model.validate.ValidRevisionUsage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "actor", "verb", "object", "result", "context",
		"timestamp", "stored", "authority", "version" })
@ValidRevisionUsage
@ValidPlatformUsage
public class Statement {
	@JsonProperty(required = false)
	@IsUUID
	private String id;
	@NotNull
	@Valid
	private Actor actor;
	@NotNull
	@Valid
	private Verb verb;
	@NotNull
	@Valid
	@JsonProperty("object")
	private StatementObject statementObject;
	private Result result;
	@Valid
	private Context context;
	// xAPI 1.0.1 4.1.7 A timestamp SHOULD be the current or a past time when it
	// is outside of a Sub-Statement.
	@Past
	private Date timestamp;

	// should be set by the LRS, not the client
	@Null
	private Date stored;
	private Authority authority;
	private String version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getStored() {
		return stored;
	}

	public void setStored(Date stored) {
		this.stored = stored;
	}

	public Authority getAuthority() {
		return authority;
	}

	public void setAuthority(Authority authority) {
		this.authority = authority;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
