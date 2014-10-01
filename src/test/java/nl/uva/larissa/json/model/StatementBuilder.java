package nl.uva.larissa.json.model;

import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.apache.abdera.i18n.iri.IRI;

public class StatementBuilder {

	public static StatementBuilder statement() {
		StatementBuilder result = new StatementBuilder();
		return result;
	}

	final Validator validator = Validation.buildDefaultValidatorFactory()
			.getValidator();

	private String id;
	private String email;
	private String verbId;
	private String referenceId;
	private String activityId;
	private String registration;

	private StatementBuilder() {
	}

	public StatementBuilder id(String id) {
		this.id = id;
		return this;
	}

	public StatementBuilder actor(String email) {
		this.email = email;
		return this;
	}

	public StatementBuilder verb(String id) {
		this.verbId = id;
		return this;
	}

	public StatementBuilder statementRef(String id) {
		this.referenceId = id;
		return this;
	}

	public Statement build() {
		Statement result = new Statement();
		Agent authority = new Agent();
		IFI authIfi = new IFI();
		authIfi.setMbox(new IRI("test@example.com"));
		authority.setIdentifier(authIfi);
		result.setAuthority(authority);
		result.setId(id);

		Verb verb = new Verb();
		verb.setId(new IRI(verbId));

		result.setVerb(verb);

		Agent agent = new Agent();

		IFI identifier = new IFI();
		identifier.setMbox(new IRI("mailto:" + email));
		agent.setIdentifier(identifier);

		result.setActor(agent);

		if (referenceId != null) {
			StatementRef ref = new StatementRef();
			ref.setId(referenceId);

			result.setStatementObject(ref);
		} else if (activityId != null) {
			Activity activity = new Activity();
			activity.setId(new IRI(activityId));

			result.setStatementObject(activity);

		}
		if (registration != null) {
			Context context = new Context();
			context.setRegistration(registration);

			result.setContext(context);
		}
		Set<ConstraintViolation<Statement>> violations = validator
				.validate(result);
		if (!violations.isEmpty()) {
			throw new ValidationException(violations.iterator().next()
					.getMessage());
		}
		return result;
	}

	public StatementBuilder randomId() {
		this.id = UUID.randomUUID().toString();
		return this;
	}

	public StatementBuilder activity(String string) {
		this.activityId = string;
		return this;
	}

	public StatementBuilder contextWithRegistration(String registration) {
		this.registration = registration;
		return this;
	}
}
