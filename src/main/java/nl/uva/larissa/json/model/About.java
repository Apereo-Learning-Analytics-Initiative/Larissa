package nl.uva.larissa.json.model;

import java.util.List;
import java.util.Map;

import org.apache.abdera.i18n.iri.IRI;

public class About {
	List<String> version;
	private Map<IRI, Object> extensions;

	public List<String> getVersion() {
		return version;
	}

	public void setVersion(List<String> version) {
		this.version = version;
	}

	public Map<IRI, Object> getExtensions() {
		return extensions;
	}

	public void setExtensions(Map<IRI, Object> extensions) {
		this.extensions = extensions;
	}

}
