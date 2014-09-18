package nl.uva.larissa.json;

import java.io.IOException;

import org.apache.abdera.i18n.iri.IRI;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public final class IRIDeserializer extends JsonDeserializer<IRI> {
	@Override
	public IRI deserialize(JsonParser jsonparser,
			DeserializationContext deserializationcontext)
			throws IOException, JsonProcessingException {
		String value = jsonparser.readValueAs(String.class);
		return new IRI(value);
	}
}