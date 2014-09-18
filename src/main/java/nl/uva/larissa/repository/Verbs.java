package nl.uva.larissa.repository;

public enum Verbs {
	VOIDING("http://adlnet.gov/expapi/verbs/voided");

	private final String iri;

	Verbs(String iri) {
		this.iri = iri;
	}

	public String iri() {
		return iri;
	}
}
