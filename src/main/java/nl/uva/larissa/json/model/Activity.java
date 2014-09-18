package nl.uva.larissa.json.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.abdera.i18n.iri.IRI;

public class Activity implements StatementObject {

	private String objectType;
	@NotNull
	private IRI id;
	@Valid
	private ActivityDefinition definition;

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public IRI getId() {
		return id;
	}

	public void setId(IRI id) {
		this.id = id;
	}

	public ActivityDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ActivityDefinition definition) {
		this.definition = definition;
	}

}
