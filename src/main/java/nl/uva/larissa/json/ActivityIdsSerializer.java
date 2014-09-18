package nl.uva.larissa.json;

import java.io.IOException;

import nl.uva.larissa.json.model.Activity;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

public class ActivityIdsSerializer extends JsonSerializer<Activity> {

	@Override
	public void serialize(Activity activity, JsonGenerator generator,
			SerializerProvider serializerProvider) throws IOException,
			JsonGenerationException {
		generator.writeStartObject();
		generator.writeFieldName("objectType");
		generator.writeString("Activity");
		serializerProvider.defaultSerializeField("id", activity.getId(),
				generator);
		generator.writeEndObject();
	}

	@Override
	public void serializeWithType(Activity activity, JsonGenerator generator,
			SerializerProvider serializerProvider, TypeSerializer typeSerializer)
			throws IOException, JsonProcessingException {
		serialize(activity, generator, serializerProvider);
	}
}
