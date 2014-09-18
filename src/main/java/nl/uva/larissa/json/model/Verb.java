package nl.uva.larissa.json.model;

import java.util.Map;

import javax.validation.constraints.NotNull;

import nl.uva.larissa.json.model.validate.LanguageMap;

import org.apache.abdera.i18n.iri.IRI;

public class Verb {
	@NotNull
	private IRI id;
	@LanguageMap
	private Map<String, String> display;

	public IRI getId() {
		return id;
	}

	public void setId(IRI id) {
		this.id = id;
	}

	public Map<String, String> getDisplay() {
		return display;
	}

	public void setDisplay(Map<String, String> display) {
		this.display = display;
	}
}
