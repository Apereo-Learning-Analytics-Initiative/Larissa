package nl.uva.larissa.repository.couchdb;

import java.util.Date;

import nl.uva.larissa.json.model.Activity;
import nl.uva.larissa.json.model.Authority;
import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.json.model.StatementObject;

import org.apache.abdera.i18n.iri.IRI;

class Referrer {
	private String id;
	private Date stored;
	private Authority auth;
	private String registration;
	private IRI activity;
	private IRI verb;

	public String getId() {
		return id;
	}

	public Date getStored() {
		return stored;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setStored(Date stored) {
		this.stored = stored;
	}

	public Authority getAuth() {
		return auth;
	}

	public void setAuth(Authority auth) {
		this.auth = auth;
	}

	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}

	public IRI getActivity() {
		return activity;
	}

	public void setActivity(IRI activity) {
		this.activity = activity;
	}

	public IRI getVerb() {
		return verb;
	}

	public void setVerb(IRI verb) {
		this.verb = verb;
	}

	public static Referrer fromStatement(Statement referringStatement) {
		Referrer result = new Referrer();

		result.setId(referringStatement.getId());
		result.setStored(referringStatement.getStored());
		result.setAuth(referringStatement.getAuthority());
		result.setVerb(referringStatement.getVerb().getId());

		result.setRegistration(referringStatement.getContext() == null ? null
				: referringStatement.getContext().getRegistration());
		StatementObject statementObject = referringStatement
				.getStatementObject();
		if (statementObject instanceof Activity) {
			result.setActivity(((Activity) statementObject).getId());
		}

		return result;

	}
}
