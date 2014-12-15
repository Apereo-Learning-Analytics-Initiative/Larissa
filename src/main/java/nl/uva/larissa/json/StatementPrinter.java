package nl.uva.larissa.json;

import java.io.IOException;

import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.json.model.StatementResult;

public interface StatementPrinter {
	public String printStatement(Statement statement) throws IOException;

	public String print(StatementResult result) throws IOException;

	String printCompact(Object object) throws IOException;

	/**
	 * print Statement as defined in xAPI spec 7.2.3 for parameter 'format' with
	 * value 'ids'; Only print minimum info in Agent, Activity and Group Objects
	 * necessary to identify them
	 **/
	String printIds(Statement statement) throws IOException;

	public String printIds(StatementResult result) throws IOException;
}