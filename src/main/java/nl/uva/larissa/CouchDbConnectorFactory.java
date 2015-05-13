package nl.uva.larissa;

import java.net.MalformedURLException;

import nl.uva.larissa.json.IRIDeserializer;
import nl.uva.larissa.json.IRISerializer;
import nl.uva.larissa.json.ISO8601VerboseDateFormat;

import org.apache.abdera.i18n.iri.IRI;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.impl.StreamingJsonSerializer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class CouchDbConnectorFactory {

	private static void applyMapperConfiguration(ObjectMapper mapper) {
		// working around broken NON_NULL stuff in Ektorp/Jackson
		// TODO report and/or check for update
		mapper.setSerializationInclusion(Include.NON_NULL);

		mapper.setDateFormat(new ISO8601VerboseDateFormat());

		SimpleModule module = new SimpleModule();
		module.addSerializer(IRI.class, new IRISerializer());
		module.addDeserializer(IRI.class, new IRIDeserializer());
		mapper.registerModule(module);
	}

	private static final ObjectMapper mapper;
	static {
		mapper = new ObjectMapper();
		applyMapperConfiguration(mapper);
	}

	public static ObjectMapper objectMapper() {
		return mapper;
	}

	public CouchDbConnector createConnector(String couchUrl, String dbName,
			int maxConnections, String username, String password) {
		HttpClient httpClient;

		try {
			StdHttpClient.Builder builder = new StdHttpClient.Builder().url(
					couchUrl).maxConnections(maxConnections);
			if (username != null) {
				builder.username(username);
			}
			if (password != null) {
				builder.password(password);
			}
			httpClient = builder.build();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}

		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		StdCouchDbConnector connector = new StdCouchDbConnector(dbName,
				dbInstance) {
			final StreamingJsonSerializer customSerializer = new StreamingJsonSerializer(
					mapper);

			@Override
			protected String serializeToJson(Object o) {
				return customSerializer.toJson(o);
			}
		};

		return connector;
	}

	public CouchDbConnector createConnector(String couchUrl, String dbName,
			int maxConnections) {
		return createConnector(couchUrl, dbName, maxConnections, null, null);
	}

}
