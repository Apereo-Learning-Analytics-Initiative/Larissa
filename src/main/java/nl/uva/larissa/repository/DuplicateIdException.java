package nl.uva.larissa.repository;

import org.ektorp.UpdateConflictException;

public class DuplicateIdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3214741294034571338L;

	private final String id;

	public DuplicateIdException(UpdateConflictException e, String id) {
		super(e);
		this.id = id;
	}

	public DuplicateIdException(String id) {
		super("duplicate id " + id);
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
