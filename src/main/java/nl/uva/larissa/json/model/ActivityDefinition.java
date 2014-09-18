package nl.uva.larissa.json.model;

import java.util.Map;

import javax.validation.Valid;

import nl.uva.larissa.json.model.validate.IsIRL;
import nl.uva.larissa.json.model.validate.LanguageMap;

import org.apache.abdera.i18n.iri.IRI;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ActivityDefinition {

	@LanguageMap
	private Map<String, String> name;
	@LanguageMap
	private Map<String, String> description;
	private IRI type;
	@IsIRL
	private IRI moreInfo;
	@JsonUnwrapped
	@Valid
	private Interaction interaction;
	private Map<IRI, Object> extensions;

	public Map<String, String> getName() {
		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;
	}

	public Map<String, String> getDescription() {
		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;
	}

	public IRI getType() {
		return type;
	}

	public void setType(IRI type) {
		this.type = type;
	}

	public Interaction getInteraction() {
		return interaction;
	}

	public void setInteraction(Interaction interaction) {
		this.interaction = interaction;
	}

	public IRI getMoreInfo() {
		return moreInfo;
	}

	public void setMoreInfo(IRI moreInfo) {
		this.moreInfo = moreInfo;
	}

	public Object getExtensions() {
		return extensions;
	}

	public void setExtensions(Map<IRI, Object> extensions) {
		this.extensions = extensions;
	}

}
