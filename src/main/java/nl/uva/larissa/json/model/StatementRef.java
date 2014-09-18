package nl.uva.larissa.json.model;

import javax.validation.constraints.NotNull;

import nl.uva.larissa.json.model.validate.IsUUID;

public class StatementRef implements StatementObject {

	@NotNull
	@IsUUID
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
