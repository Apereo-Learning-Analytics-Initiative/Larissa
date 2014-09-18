package nl.uva.larissa.json.model;

import nl.uva.larissa.json.model.validate.IsIRL;

import org.apache.abdera.i18n.iri.IRI;

public class Account {
	@IsIRL
	IRI homePage;
	String name;

	public IRI getHomePage() {
		return homePage;
	}

	public void setHomePage(IRI homePage) {
		this.homePage = homePage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
