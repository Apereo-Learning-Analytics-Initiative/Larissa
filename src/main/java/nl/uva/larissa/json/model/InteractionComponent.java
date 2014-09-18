package nl.uva.larissa.json.model;

import java.util.Map;

import nl.uva.larissa.json.model.validate.LanguageMap;

public class InteractionComponent {
	public String id;
	@LanguageMap
	public Map<String, String> description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;
	}
}
