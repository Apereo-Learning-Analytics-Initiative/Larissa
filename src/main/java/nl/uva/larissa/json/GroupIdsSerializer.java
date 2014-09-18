package nl.uva.larissa.json;

import java.io.IOException;

import nl.uva.larissa.json.model.Group;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class GroupIdsSerializer extends JsonSerializer<Group> {

	@Override
	public void serialize(Group group, JsonGenerator generator,
			SerializerProvider serializerProvider) throws IOException,
			JsonGenerationException {
		generator.writeStartObject();
		generator.writeFieldName("objectType");
		generator.writeString("Group");
		if (group.getMember() != null) {
			serializerProvider.defaultSerializeField("member",
					group.getMember(), generator);
		}
		Util.serialize(group.getIdentifier(), generator, serializerProvider);
		generator.writeEndObject();
	}

	@Override
	public void serializeWithType(Group group, JsonGenerator generator,
			SerializerProvider serializerProvider, TypeSerializer typeSerializer)
			throws IOException, JsonProcessingException {
		serialize(group, generator, serializerProvider);
	}
}
