package nl.uva.larissa.json;

import java.io.IOException;

import nl.uva.larissa.json.model.Agent;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class AgentIdsSerializer extends JsonSerializer<Agent> {

	@Override
	public void serialize(Agent agent, JsonGenerator generator,
			SerializerProvider serializerProvider) throws IOException,
			JsonGenerationException {
		generator.writeStartObject();
		generator.writeFieldName("objectType");
		generator.writeString("Agent");
		Util.serialize(agent.getIdentifier(), generator, serializerProvider);
		generator.writeEndObject();
	}

	@Override
	public void serializeWithType(Agent agent, JsonGenerator generator,
			SerializerProvider serializerProvider, TypeSerializer typeSerializer)
			throws IOException, JsonProcessingException {
		serialize(agent, generator, serializerProvider);
	}
}
