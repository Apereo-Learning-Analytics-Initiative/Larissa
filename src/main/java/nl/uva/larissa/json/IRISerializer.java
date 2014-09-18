package nl.uva.larissa.json;

import java.io.IOException;

import org.apache.abdera.i18n.iri.IRI;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class IRISerializer extends JsonSerializer<IRI> {
	@Override
	public void serialize(IRI iri, JsonGenerator generator,
			SerializerProvider serializerProvider) throws IOException,
			JsonProcessingException {
		generator.writeString(iri.toString());
	}
}