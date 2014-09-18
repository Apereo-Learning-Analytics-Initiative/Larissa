package nl.uva.larissa.json;

import java.io.IOException;
import java.util.List;

import org.apache.abdera.i18n.iri.IRI;

import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.repository.couchdb.StatementDocument;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class StatementParserImpl implements StatementParser {

	final private ObjectMapper mapper;

	public StatementParserImpl() {
		this.mapper = new ObjectMapper();
		// FIXME Jackson doesn't seem to give an error on a missing property on
		// the level of an ActivityDefinition. This is potentially risky as a
		// statement will
		// be accepted yet stored incomplete. bug in Jackson?
		// TODO make this configure less general? (meant for only
		// ContextActivities)
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
				true);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(IRI.class, new IRIDeserializer());
		mapper.registerModule(module);
	}

	@Override
	public Statement parseStatement(String json) throws ParseException {
		try {
			return mapper.readValue(json, Statement.class);
		} catch (IOException e) {
			throw new ParseException(e);
		}
	}

	@Override
	public StatementDocument parseStatementDocument(String json)
			throws ParseException {
		try {
			return mapper.readValue(json, StatementDocument.class);
		} catch (IOException e) {
			throw new ParseException(e);
		}
	}

	@Override
	public <T> T parse(Class<T> type, String json) throws ParseException {
		try {
			return mapper.readValue(json, type);
		} catch (IOException e) {
			throw new ParseException(e);
		}
	}

	// http://stackoverflow.com/questions/6062011/jackson-is-not-deserialising-a-generic-list-that-it-has-serialised
	@Override
	public List<Statement> parseStatementList(String json)
			throws ParseException {
		try {
			return mapper.readValue(json, new TypeReference<List<Statement>>() {
			});
		} catch (IOException e) {
			throw new ParseException(e);
		}
	}
}
