package nl.uva.larissa.json;

import java.io.IOException;

import nl.uva.larissa.json.model.Activity;
import nl.uva.larissa.json.model.Agent;
import nl.uva.larissa.json.model.Group;
import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.json.model.StatementResult;

import org.apache.abdera.i18n.iri.IRI;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class StatementPrinterImpl implements StatementPrinter {
	final ObjectMapper mapper;
	final ObjectMapper idsMapper;

	public StatementPrinterImpl() {
		mapper = new ObjectMapper();
		configureBaseMapper(mapper, false);
		idsMapper = new ObjectMapper();
		configureBaseMapper(idsMapper, true);
	}

	private static void configureBaseMapper(ObjectMapper mapper,
			boolean includeIdsMappers) {
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setDateFormat(new ISO8601VerboseDateFormat());
		SimpleModule module = new SimpleModule();

		JsonSerializer<IRI> iriSerializer = new IRISerializer();
		module.addSerializer(IRI.class, iriSerializer);
		if (includeIdsMappers) {
			module.addSerializer(Agent.class, new AgentIdsSerializer());
			module.addSerializer(Group.class, new GroupIdsSerializer());
			module.addSerializer(Activity.class, new ActivityIdsSerializer());
		}

		mapper.registerModule(module);
	}

	@Override
	public String printStatement(Statement statement) throws IOException {
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
				statement);
	}

	@Override
	public String print(StatementResult result) throws IOException {
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
				result);
	}

	@Override
	public String printCompact(Object object) throws JsonProcessingException {
		return mapper.writeValueAsString(object);
	}

	@Override
	public String printIds(Statement statement) throws IOException {
		return idsMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
				statement);
	}

	@Override
	public String printIds(StatementResult result) throws IOException {
		return idsMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
				result);
	}
}
