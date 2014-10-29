package nl.uva.larissa.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import nl.uva.larissa.json.ISO8601VerboseDateFormat;
import nl.uva.larissa.json.ParseException;
import nl.uva.larissa.json.StatementParser;
import nl.uva.larissa.json.StatementPrinter;
import nl.uva.larissa.json.model.Account;
import nl.uva.larissa.json.model.Agent;
import nl.uva.larissa.json.model.IFI;
import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.json.model.StatementResult;
import nl.uva.larissa.repository.DuplicateIdException;
import nl.uva.larissa.repository.StatementFilter;
import nl.uva.larissa.repository.StatementRepository;
import nl.uva.larissa.repository.UnknownStatementException;
import nl.uva.larissa.repository.VoidingTargetException;

import org.apache.abdera.i18n.iri.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

@Path(value = "/xAPI/statements")
public class StatementsResource {

	@Inject
	StatementParser parser;

	@Inject
	StatementRepository repository;

	@Inject
	StatementPrinter printer;

	@Inject
	Validator validator;

	@Context
	SecurityContext securityContext;

	private static Logger LOGGER = LoggerFactory
			.getLogger(StatementsResource.class);

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response storeStatement(@QueryParam("statementId") String id,
			String json) {
		LOGGER.trace("storeStatement " + id);
		if (id == null) {
			return badRequest("missing required parameter 'statementId'");
		}
		try {
			Agent authority = getAuthority();
			Statement statement = parser.parseStatement(json);
			statement.setAuthority(authority);
			validate(statement);
			final String idField = statement.getId();
			if (idField != null && !idField.equals(id)) {
				return badRequest(String
						.format("The field statement.id exists and has a different value than parameter 'statementId' (%s != %s)",
								idField, id));
			}
			statement.setId(id);
			repository.storeStatement(statement);
		} catch (ParseException | ValidationException | VoidingTargetException
				| UnknownStatementException e) {
			return badRequest(e);
		} catch (DuplicateIdException e) {
			// TODO spec mentions checking if statement is the same (although
			// ADL throws 409 regardless!)
			return dupeResponse(e);
		}
		return Response.noContent().build();
	}

	private Agent getAuthority() {
		Agent result = new Agent();
		IFI ifi = new IFI();
		Account account = new Account();
		account.setHomePage(new IRI("http://lrs.uva.nl"));
		account.setName(securityContext.getUserPrincipal().getName());
		ifi.setAccount(account);
		result.setIdentifier(ifi);
		return result;
	}

	private Response badRequest(Exception e) {
		return badRequest(e.getMessage());
	}

	private Response badRequest(String message) {
		ResponseBuilder response = Response.status(Status.BAD_REQUEST);
		if (message != null) {
			response.type(MediaType.TEXT_PLAIN).entity(message);
		}
		return response.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response storeStatements(String json) {
		LOGGER.trace("storeStatements");
		String id;
		Statement statement;
		try {
			try {
				Agent authority = getAuthority();
				statement = parser.parseStatement(json);
				statement.setAuthority(authority);
				validate(statement);
				id = repository.storeStatement(statement);
			} catch (ParseException e) {
				List<Statement> statements = parser.parseStatementList(json);
				return validateAndStore(statements);
			}
		} catch (ParseException | ValidationException | VoidingTargetException
				| UnknownStatementException e) {
			return badRequest(e);
		} catch (DuplicateIdException e) {
			return dupeResponse(e);
		}
		return Response.ok(String.format("[\"%s\"]", id),
				MediaType.APPLICATION_JSON).build();
	}

	private Response dupeResponse(DuplicateIdException e) {
		return Response
				.status(Status.CONFLICT)
				.entity(String.format("a statement with id %s already exists",
						e.getId())).type(MediaType.TEXT_PLAIN).build();
	}

	private Response validateAndStore(List<Statement> statements)
			throws DuplicateIdException, ValidationException,
			VoidingTargetException {
		Agent authority = getAuthority();
		for (Statement statement : statements) {
			statement.setAuthority(authority);
			validate(statement);
		}
		List<String> ids = repository.storeStatements(statements);

		ArrayNode node = JsonNodeFactory.instance.arrayNode();
		for (String id : ids) {
			node.add(id);
		}
		return Response.ok(node.toString(), MediaType.APPLICATION_JSON).build();
	}

	@GET
	public Response getStatements(@Context UriInfo uriInfo) {
		MultivaluedMap<String, String> parameters = uriInfo
				.getQueryParameters();
		LOGGER.trace("getStatements " + parameters.keySet());
		return getStatementsUsingParameters(parameters);
	}

	private Response getFilteredStatements(StatementFilter statementFilter) {
		StatementResult result = repository.getStatements(statementFilter);
		Response response;
		try {
			response = Response
					.ok(printFormatted(result, statementFilter.getFormat()),
							MediaType.APPLICATION_JSON)
					.header(XapiHeader.CONSISTENT_THROUGH.key(),
							new ISO8601VerboseDateFormat().format(result
									.getConsistentThrough())).build();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			response = Response.serverError().build();
		}
		return response;
	}

	private String printFormatted(StatementResult result, String format)
			throws IOException {
		if (format == null || "exact".equals(format)) {
			return printer.print(result);
		}
		if ("ids".equals(format)) {
			return printer.printIds(result);
		}
		throw new IllegalArgumentException(
				"allowed values for parameter 'format' are {ids,exact}");
	}

	// xAPI 1.0.1 - 7.2.2
	// GET Statements MAY be called using POST and form fields if necessary as
	// query strings have limits
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response getStatementsWithPost(
			MultivaluedMap<String, String> parameters) {
		LOGGER.trace("getStatementsWithPost " + parameters.keySet());
		return getStatementsUsingParameters(parameters);
	}

	@OPTIONS
	public Response options() {
		return Response.ok().build();
	}

	private Response getStatement(String id) {
		StatementResult result = repository.getStatement(id);
		return getQueryByIdResponse(id, result);
	}

	private Response getVoidedStatement(String id) {
		StatementResult result = repository.getVoidedStatement(id);
		return getQueryByIdResponse(id, result);
	}

	private Response getStatementsUsingParameters(
			MultivaluedMap<String, String> parameters) {
		RequestTypeParser parser = new RequestTypeParser();
		RequestTypeParser.Result result = parser.parse(parameters);
		Response response;
		switch (result.getType()) {
		case SINGLE:
			response = getStatement(result.getStatementId());
			break;
		case VOIDED:
			response = getVoidedStatement(result.getStatementId());
			break;
		case FILTER:
			response = getFilteredStatements(result.getStatementFilter());
			break;
		case INVALID:
			response = badRequest(result.getMessage());
			break;
		default:
			response = Response.serverError().build();
		}
		return response;
	}

	private Response getQueryByIdResponse(String id, StatementResult result) {
		ResponseBuilder builder;
		List<Statement> statements = result.getStatements();
		switch (statements.size()) {
		case 0:
			builder = Response.status(Status.NOT_FOUND);
			break;
		case 1:
			try {
				builder = Response.ok(
						printer.printStatement(statements.get(0)),
						MediaType.APPLICATION_JSON);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
				return Response.serverError().build();
			}
			break;
		default:
			LOGGER.error(
					"received more than one statement (n=%s) when querying by id '%s'",
					statements.size(), id);
			return Response.serverError().build();
		}
		builder.header(XapiHeader.CONSISTENT_THROUGH.key(),
				new ISO8601VerboseDateFormat().format(result
						.getConsistentThrough()));
		return builder.build();
	}

	private void validate(Statement statement) throws ValidationException {
		Set<ConstraintViolation<Statement>> violations = validator
				.validate(statement);
		Iterator<ConstraintViolation<Statement>> itt = violations.iterator();
		if (itt.hasNext()) {
			ConstraintViolation<Statement> violation = itt.next();
			throw new ValidationException(violation.getMessage() + ": "
					+ violation.getPropertyPath());
		}
	}
}
