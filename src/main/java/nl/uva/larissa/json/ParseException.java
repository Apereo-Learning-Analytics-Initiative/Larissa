package nl.uva.larissa.json;


public class ParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -786854572139522845L;

	public ParseException(String message) {
		super(message);
	}
	public ParseException(Exception e) {
		super(e);
	}

}
