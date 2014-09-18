package nl.uva.larissa.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import jersey.repackaged.com.google.common.collect.Lists;
import nl.uva.larissa.json.ISO8601VerboseDateFormat;
import nl.uva.larissa.json.ParseException;
import nl.uva.larissa.json.StatementParser;
import nl.uva.larissa.json.StatementPrinter;
import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.json.model.StatementResult;
import nl.uva.larissa.repository.DuplicateIdException;
import nl.uva.larissa.repository.StatementFilter;
import nl.uva.larissa.repository.StatementRepository;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStatementsResource extends JerseyTest {

	private static final String TARGET_PATH = "/xAPI/statements";
	private static StatementRepository mockedRepository = mock(StatementRepository.class);
	private static StatementParser mockedParser = mock(StatementParser.class);
	private static StatementPrinter mockedPrinter = mock(StatementPrinter.class);
	private static Validator mockedValidator = mock(Validator.class);
	private static ContainerRequestFilter dummySecurityFilter;

	@BeforeClass
	public static void beforeClass() {
		when(mockedValidator.validate(any(Statement.class))).thenReturn(
				new HashSet<ConstraintViolation<Statement>>(0));
		dummySecurityFilter = new ContainerRequestFilter() {

			@Override
			public void filter(ContainerRequestContext containerrequestcontext)
					throws IOException {
				containerrequestcontext
						.setSecurityContext(new SecurityContext() {

							@Override
							public boolean isUserInRole(String s) {
								return false;
							}

							@Override
							public boolean isSecure() {
								return false;
							}

							@Override
							public Principal getUserPrincipal() {
								return new Principal() {

									@Override
									public String getName() {
										return "larissa";
									}
								};
							}

							@Override
							public String getAuthenticationScheme() {
								return "dummy auth";
							}
						});

			}
		};
	}

	@Override
	protected Application configure() {

		return new ResourceConfig() {
			{
				register(new AbstractBinder() {

					@Override
					protected void configure() {
						bind(mockedValidator).to(Validator.class);
						bind(mockedRepository).to(StatementRepository.class);
						bind(mockedParser).to(StatementParser.class);
						bind(mockedPrinter).to(StatementPrinter.class);

					}
				});
				register(dummySecurityFilter);
				register(IllegalArgumentExceptionMapper.class);
				register(MultiExceptionMapper.class);
				register(DateParamConverterProvider.class);
				register(StatementsResource.class);
			}
		};
	}

	@Before
	public void beforeTest() {
		reset(mockedRepository, mockedParser, mockedPrinter);
	}

	@Test
	public void testGet() throws IOException {
		testGet(false);
	}

	@Test
	public void testGetWithPost() throws IOException {
		testGet(true);
	}

	public void testGet(boolean withPost) throws IOException {
		String existingId = "existingId";
		String nonExistingId = "nonExistingId";

		Date consistencyDate = new Date();

		Statement statement = new Statement();
		statement.setId(existingId);
		StatementResult singleStatementResult = new StatementResult(
				Lists.newArrayList(statement), consistencyDate);

		StatementResult noStatementResult = new StatementResult(
				new ArrayList<Statement>(0), consistencyDate);

		when(mockedRepository.getStatement(existingId)).thenReturn(
				singleStatementResult);
		when(mockedPrinter.printStatement(statement)).thenReturn(
				"expectedSerialization");
		when(mockedRepository.getStatement(nonExistingId)).thenReturn(
				noStatementResult);

		Response response;
		Form form;

		WebTarget target = target(TARGET_PATH);

		if (withPost) {
			form = new Form("statementId", existingId);
			response = target.request().post(Entity.form(form));
		} else {
			response = target.queryParam("statementId", existingId).request()
					.get();
		}

		assertEquals(200, response.getStatus());
		assertEquals("application/json", response.getMediaType().toString());
		assertEquals("expectedSerialization", response.readEntity(String.class));
		assertNotNull(response.getHeaderString(XapiHeader.CONSISTENT_THROUGH
				.key()));

		if (withPost) {
			form = new Form("statementId", nonExistingId);
			response = target.request().post(Entity.form(form));
		} else {
			response = target.queryParam("statementId", nonExistingId)
					.request().get();
		}

		assertEquals(404, response.getStatus());
		assertNotNull(response.getHeaderString(XapiHeader.CONSISTENT_THROUGH
				.key()));

		if (withPost) {
			form = new Form("statementId", existingId);
			form.param("verb", "http://verbs.uva.nl/someverb");
			response = target.request().post(Entity.form(form));
		} else {
			response = target.queryParam("statementId", existingId)
					.queryParam("verb", "http://verbs.uva.nl/someverb")
					.request().get();
		}

		assertEquals(400, response.getStatus());
		assertEquals(MediaType.TEXT_PLAIN_TYPE, response.getMediaType());
		assertNull(response
				.getHeaderString(XapiHeader.CONSISTENT_THROUGH.key()));

		if (withPost) {
			form = new Form("statementId", existingId);
			form.param("statementId", nonExistingId);
			response = target.request().post(Entity.form(form));
		} else {
			response = target
					.queryParam("statementId", existingId, nonExistingId)
					.request().get();
		}

		assertEquals(400, response.getStatus());
		assertEquals(MediaType.TEXT_PLAIN_TYPE, response.getMediaType());
		assertNull(response
				.getHeaderString(XapiHeader.CONSISTENT_THROUGH.key()));

		StatementResult correctResult = new StatementResult(
				Lists.newArrayList(new Statement()), consistencyDate);
		when(mockedRepository.getStatements(any(StatementFilter.class)))
				.thenReturn(correctResult);
		when(mockedPrinter.print(correctResult)).thenReturn(
				"expectedResultSerialization");

		if (withPost) {
			form = new Form("limit", "42");
			response = target.request().post(Entity.form(form));
		} else {
			response = target.queryParam("limit", 42).request().get();
		}

		assertEquals(200, response.getStatus());
		assertEquals("application/json", response.getMediaType().toString());
		assertEquals("expectedResultSerialization",
				response.readEntity(String.class));
		assertNotNull(response.getHeaderString(XapiHeader.CONSISTENT_THROUGH
				.key()));

		String dateString = new ISO8601VerboseDateFormat().format(new Date());

		if (withPost) {
			form = new Form("since", dateString);
			response = target.request().post(Entity.form(form));
		} else {
			response = target
					.queryParam("since", URLEncoder.encode(dateString, "UTF-8"))
					.request().get();
		}

		assertEquals(200, response.getStatus());
		assertEquals("application/json", response.getMediaType().toString());
		assertEquals("expectedResultSerialization",
				response.readEntity(String.class));
		assertNotNull(response.getHeaderString(XapiHeader.CONSISTENT_THROUGH
				.key()));

		if (withPost) {
			form = new Form("since", "bla");
			response = target.request().post(Entity.form(form));
		} else {
			response = target.queryParam("since", "bla").request().get();
		}

		assertEquals(400, response.getStatus());
		assertEquals("text/plain", response.getMediaType().toString());
		assertNull(response
				.getHeaderString(XapiHeader.CONSISTENT_THROUGH.key()));
	}

	@Test
	public void testPost() throws Exception {
		Statement validNonExistingStatement = new Statement();
		Statement validExistingStatement = new Statement();

		String validExistingJson = "validExistingJson";
		String validNonExistingJson = "validNonExistingJson";

		when(mockedParser.parseStatement(validNonExistingJson)).thenReturn(
				validNonExistingStatement);
		when(mockedParser.parseStatement(validExistingJson)).thenReturn(
				validExistingStatement);
		when(mockedRepository.storeStatement(validNonExistingStatement))
				.thenReturn("newlyGeneratedId");
		when(mockedRepository.storeStatement(validExistingStatement))
				.thenThrow(new DuplicateIdException("dupeId"));

		Builder request = target(TARGET_PATH).request();

		// --- single ---

		Response response = request.post(Entity.json(validNonExistingJson));

		assertEquals(200, response.getStatus());
		assertEquals("application/json", response.getMediaType().toString());
		assertEquals("[\"newlyGeneratedId\"]",
				response.readEntity(String.class));

		response = request.post(Entity.json(validExistingJson));

		assertEquals(409, response.getStatus());

		// --- multiple ---

		List<String> ids = Arrays.asList("id1", "id2");
		List<Statement> statements = Lists.newArrayList(
				validNonExistingStatement, validNonExistingStatement);
		// yes, those two are the same. But as we're mocking the repository it
		// won't cause problems unless we want it to

		when(mockedParser.parseStatement(validNonExistingJson)).thenThrow(
				new ParseException("invalid"));
		when(mockedParser.parseStatementList(validNonExistingJson)).thenReturn(
				statements);
		when(mockedRepository.storeStatements(statements)).thenReturn(ids);

		response = request.post(Entity.json(validNonExistingJson));

		assertEquals(200, response.getStatus());
		assertEquals("application/json", response.getMediaType().toString());
		assertEquals("[\"id1\",\"id2\"]", response.readEntity(String.class));
	}

	@Test
	public void testPut() throws Exception {
		String uniqueId = "uniqueId";
		String validStatementJson = "validStatementJson";
		String invalidStatementJson = "invalidStatementJson";
		Statement uniqueStatement = new Statement();
		String duplicateId = "duplicateId";
		Statement conflictingStatement = new Statement();

		when(mockedParser.parseStatement(validStatementJson)).thenReturn(
				uniqueStatement);
		when(mockedParser.parseStatement(invalidStatementJson)).thenThrow(
				new ParseException("parse error"));

		when(mockedRepository.storeStatement(uniqueStatement)).thenReturn(
				"nonExistingId");

		Response response = target(TARGET_PATH)
				.queryParam("statementId", validStatementJson).request()
				.put(Entity.json(validStatementJson));

		assertEquals(204, response.getStatus());
		assertEquals(null, response.getMediaType());

		response = target(TARGET_PATH).queryParam("statementId", uniqueId)
				.request().put(Entity.json(invalidStatementJson));

		assertEquals(400, response.getStatus());
		assertEquals(MediaType.TEXT_PLAIN, response.getMediaType().toString());

		when(mockedParser.parseStatement(validStatementJson)).thenReturn(
				conflictingStatement);
		when(mockedRepository.storeStatement(conflictingStatement)).thenThrow(
				new DuplicateIdException("dupeId"));

		response = target(TARGET_PATH).queryParam("statementId", duplicateId)
				.request().put(Entity.json(validStatementJson));

		assertEquals(409, response.getStatus());
	}
}