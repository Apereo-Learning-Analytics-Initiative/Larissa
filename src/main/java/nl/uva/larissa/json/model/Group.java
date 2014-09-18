package nl.uva.larissa.json.model;

import java.util.List;

import javax.validation.Valid;

import nl.uva.larissa.json.model.validate.ValidGroup;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

@ValidGroup
public class Group implements Actor {

	private String objectType;
	private String name;
	@Valid
	private List<Agent> member;

	@JsonUnwrapped
	@Valid
	private IFI identifier;

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Agent> getMember() {
		return member;
	}

	public void setMember(List<Agent> member) {
		this.member = member;
	}

	public IFI getIdentifier() {
		return identifier;
	}

	public void setIdentifier(IFI identifier) {
		this.identifier = identifier;
	}

}
