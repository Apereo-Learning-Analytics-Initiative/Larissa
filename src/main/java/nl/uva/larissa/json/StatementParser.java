package nl.uva.larissa.json;

import java.util.List;

import org.jvnet.hk2.annotations.Contract;

import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.repository.couchdb.StatementDocument;

@Contract
public interface StatementParser {
	public Statement parseStatement(String json) throws ParseException;

	public StatementDocument parseStatementDocument(String doc)
			throws ParseException;

	public <T> T parse(Class<T> type, String json) throws ParseException;

	public List<Statement> parseStatementList(String json)
			throws ParseException;;
}
