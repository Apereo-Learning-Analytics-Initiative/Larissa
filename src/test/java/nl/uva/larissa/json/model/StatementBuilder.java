package nl.uva.larissa.json.model;

import java.util.ArrayList;
import java.util.List;
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

	private String instructor;

	private List<String> parentContextActivities = new ArrayList<>(0);

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
		result.setAuthority(createAgentWithEmail("test@example.com"));
		result.setId(id);

		Verb verb = new Verb();
		verb.setId(new IRI(verbId));

		result.setVerb(verb);

		result.setActor(createAgentWithEmail(email));

		if (referenceId != null) {
			StatementRef ref = new StatementRef();
			ref.setId(referenceId);

			result.setStatementObject(ref);
		} else if (activityId != null) {
			result.setStatementObject(createActivityWithId(activityId));

		}
		Context context = null;
		if (registration != null) {
			context = new Context();
			context.setRegistration(registration);
		}
		if (instructor != null) {
			if (context == null) {
				context = new Context();
			}
			context.setInstructor(createAgentWithEmail(instructor));

		}
		if (!parentContextActivities.isEmpty()) {
			if (context == null) {
				context = new Context();
			}
			ContextActivities contextActivities = new ContextActivities();
			ArrayList<Activity> activities = new ArrayList<>(
					parentContextActivities.size());
			for (String activityId : parentContextActivities) {
				activities.add(createActivityWithId(activityId));
			}
			contextActivities.setParent(activities);
			context.setContextActivities(contextActivities);
		}
		if (context != null) {
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

	private Activity createActivityWithId(String id) {
		Activity result = new Activity();
		result.setId(new IRI(id));
		return result;
	}

	private Agent createAgentWithEmail(String email) {
		Agent result = new Agent();
		IFI ifi = new IFI();
		ifi.setMbox(new IRI("mailto:" + email));
		result.setIdentifier(ifi);
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

	public StatementBuilder instructor(String string) {
		this.instructor = string;
		return this;
	}

	public StatementBuilder parentContextActivity(String string) {
		parentContextActivities.add(string);
		return this;
	}
}
