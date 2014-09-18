package nl.uva.larissa;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

public class ConfigReader {

	private static final String LRS_CONFIG = "lrs.config";

	enum Key {
		COUCHDB_URL("couchdb.url"), COUCHDB_DB_NAME("couchdb.db.name");
		private String key;

		Key(String key) {
			this.key = key;
		}

		public String key() {
			return key;
		}
	}

	private final Properties props;

	public ConfigReader(ServletContext context) {
		props = new Properties();
		try {
			String configPath = context.getInitParameter(LRS_CONFIG);
			if (configPath == null) {
				throw new IllegalStateException("context parameter "
						+ LRS_CONFIG + " not found.");
			}
			try (InputStream is = context.getResourceAsStream(configPath)) {
				if (is == null) {
					throw new RuntimeException("configuration file '"
							+ configPath + "' as defined by " + LRS_CONFIG
							+ " not found.");
				}
				props.load(is);
			}
		} catch (IOException e) {
			throw new RuntimeException("error reading configuration", e);
		}
		for (Key key : Key.values()) {
			if (!props.containsKey(key.key())) {
				throw new IllegalStateException(
						"missing configuration property " + key.key());
			}
		}
	}

	public String get(Key key) {
		return props.getProperty(key.key());
	}
}