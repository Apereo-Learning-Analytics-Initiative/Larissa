package nl.uva.larissa.repository.couchdb;

import org.ektorp.support.CouchDbDocument;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({ "id", "revision" })
public class Item extends CouchDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6248677743286663027L;
	
	private String itemField;

	public String getItemField() {
		return itemField;
	}

	public void setItemField(String string) {
		this.itemField = string;
	}

}