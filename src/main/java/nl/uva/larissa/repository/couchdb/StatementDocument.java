package nl.uva.larissa.repository.couchdb;

import nl.uva.larissa.json.model.Statement;

import org.ektorp.support.CouchDbDocument;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class StatementDocument extends CouchDbDocument {

	public static enum Type {
		PLAIN, VOIDED
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1533055980737805979L;

	@JsonUnwrapped
	private Statement statement;

	private Type type = Type.PLAIN;

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
