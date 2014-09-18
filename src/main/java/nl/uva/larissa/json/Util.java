package nl.uva.larissa.json;

import java.io.IOException;

import nl.uva.larissa.json.model.IFI;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;

public class Util {

	static void serialize(IFI ifi, JsonGenerator generator,
			SerializerProvider serializerProvider)
			throws JsonProcessingException, IOException {
		String[] keys = new String[] { "account", "mbox", "mbox_sha1sum",
				"openid" };
		Object[] values = new Object[] { ifi.getAccount(), ifi.getMbox(),
				ifi.getMbox_sha1sum(), ifi.getOpenID() };
		for (int i = 0; i < keys.length; i++) {
			if (values[i] != null) {
				serializerProvider.defaultSerializeField(keys[i], values[i],
						generator);
			}
		}
	}
}
