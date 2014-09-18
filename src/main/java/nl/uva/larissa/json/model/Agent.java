package nl.uva.larissa.json.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

//explicit non-null on this object as it is used in AgentQuery for ComplexKey
@JsonInclude(Include.NON_NULL)
public class Agent implements Actor, Authority, Instructor, StatementObject {

	private String objectType;
	private String name;
	@JsonUnwrapped
	@Valid
	@NotNull
	private IFI identifier;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IFI getIdentifier() {
		return identifier;
	}

	public void setIdentifier(IFI identifier) {
		this.identifier = identifier;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
}
