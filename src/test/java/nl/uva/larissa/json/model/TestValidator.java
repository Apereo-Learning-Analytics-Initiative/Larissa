package nl.uva.larissa.json.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import jersey.repackaged.com.google.common.collect.Lists;

import static org.junit.Assert.*;

import org.apache.abdera.i18n.iri.IRI;
import org.junit.Before;
import org.junit.Test;

public class TestValidator {

	private static final IRI ValidMbox = new IRI("mailto:aap@test.com");
	private Actor validActor;
	private Verb validVerb;
	private StatementObject validStatementObject;
	private Activity validActivity;
	private Validator validator;
	private Agent validAgent;

	@Before
	public void before() {
		validAgent = new Agent();
		IFI ifi = new IFI();
		ifi.setMbox(ValidMbox);
		validAgent.setIdentifier(ifi);
		validActor = validAgent;
		validVerb = new Verb();
		validVerb.setId(new IRI("http://uva.nl/verbs/making"));
		validActivity = new Activity();
		validActivity.setId(new IRI("http://uva.nl/activities/homework"));
		validStatementObject = validActivity;

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

		validator = factory.getValidator();

	}

	@Test
	public void testActorVerbObject() {

		Statement statement = new Statement();

		Set<ConstraintViolation<Statement>> violations = validator
				.validate(statement);

		assertEquals(3, violations.size());

		for (ConstraintViolation<Statement> violation : violations) {
			assertEquals("missing required property", violation.getMessage());
		}
		statement.setActor(validActor);
		statement.setVerb(validVerb);
		statement.setStatementObject(validStatementObject);

		violations = validator.validate(statement);

		assertEquals(0, violations.size());
	}

	@Test
	public void testStored() {
		Statement statement = new Statement();
		statement.setActor(validActor);
		statement.setVerb(validVerb);
		statement.setStatementObject(validStatementObject);

		statement.setStored(new Date());

		Set<ConstraintViolation<Statement>> violations = validator
				.validate(statement);

		assertEquals(1, violations.size());
		ConstraintViolation<Statement> violation = violations.iterator().next();

		assertEquals("invalid property", violation.getMessage());
		assertEquals("stored", violation.getPropertyPath().toString());
	}

	@Test
	public void testIFIAgent() {
		Statement statement = new Statement();
		statement.setVerb(validVerb);
		statement.setStatementObject(validStatementObject);

		Agent agent = new Agent();
		IFI ifi = new IFI();
		agent.setIdentifier(ifi);
		statement.setActor(agent);

		Set<ConstraintViolation<Statement>> violations = validator
				.validate(statement);

		assertEquals("agent must have IFI", 1, violations.size());

		ifi.setMbox(ValidMbox);
		violations = validator.validate(statement);

		assertEquals("agent has IFI", 0, violations.size());

		ifi.setOpenID("openid");

		violations = validator.validate(statement);

		assertEquals("IFI may not have multiple values", 1, violations.size());

	}

	@Test
	public void testIFIGroup() {
		Statement statement = new Statement();
		statement.setVerb(validVerb);
		statement.setStatementObject(validStatementObject);

		Group group = new Group();
		statement.setActor(group);

		Set<ConstraintViolation<Statement>> violations = validator
				.validate(statement);

		assertEquals("anonymous group must have members", 1, violations.size());

		group.setMember(Lists.newArrayList(validAgent));

		violations = validator.validate(statement);

		assertEquals("anonymous group", 0, violations.size());

		IFI ifi = new IFI();
		group.setIdentifier(ifi);

		violations = validator.validate(statement);
		assertEquals("IFI must have value", 1, violations.size());

		ifi.setMbox(ValidMbox);
		violations = validator.validate(statement);

		assertEquals("identified group has IFI", 0, violations.size());

		ifi.setOpenID("openid");

		violations = validator.validate(statement);

		assertEquals("IFI may not have multiple values", 1, violations.size());

	}

	@Test
	public void testSubStatement() {
		Statement statement = new Statement();
		statement.setActor(validActor);
		statement.setVerb(validVerb);
		SubStatement subStatement = new SubStatement();
		subStatement.setActor(validActor);
		subStatement.setVerb(validVerb);
		subStatement.setStatementObject(validStatementObject);

		statement.setStatementObject(subStatement);

		Set<ConstraintViolation<Statement>> violations = validator
				.validate(statement);

		assertEquals(0, violations.size());

		subStatement.setStatementObject(subStatement);

		violations = validator.validate(statement);

		assertEquals(1, violations.size());
	}

	@Test
	public void testRevisionPlatform() {
		Statement statement = new Statement();
		statement.setActor(validActor);
		statement.setVerb(validVerb);
		Context context = new Context();
		statement.setContext(context);
		statement.setStatementObject(validAgent);

		Set<ConstraintViolation<Statement>> violations = validator
				.validate(statement);
		assertEquals(0, violations.size());

		context.setRevision("revision");
		context.setPlatform("platform");

		violations = validator.validate(statement);
		assertEquals(2, violations.size());

		statement.setStatementObject(validActivity);

		violations = validator.validate(statement);
		assertEquals(0, violations.size());
	}

	@Test
	public void testLanguageMap() {
		Statement statement = new Statement();
		statement.setActor(validActor);
		statement.setVerb(validVerb);
		statement.setStatementObject(validAgent);

		Set<ConstraintViolation<Statement>> violations = validator
				.validate(statement);
		assertEquals(0, violations.size());

		Map<String, String> map = new HashMap<>();
		validVerb.setDisplay(map);

		map.put("en-US", "totally");

		violations = validator.validate(statement);
		assertEquals(0, violations.size());

		map.put("en US", "totally");

		violations = validator.validate(statement);
		assertEquals(1, violations.size());

	}

	@Test
	public void testLanguageMapKey() {
		Statement statement = new Statement();
		statement.setActor(validActor);
		statement.setVerb(validVerb);
		statement.setStatementObject(validAgent);

		Set<ConstraintViolation<Statement>> violations = validator
				.validate(statement);
		assertEquals(0, violations.size());

		Context context = new Context();
		context.setLanguage("en-US");
		statement.setContext(context);

		violations = validator.validate(statement);
		assertEquals(0, violations.size());

		context.setLanguage("en US");
		violations = validator.validate(statement);
		assertEquals(1, violations.size());
	}
}
