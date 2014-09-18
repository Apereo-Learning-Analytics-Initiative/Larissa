package nl.uva.larissa.repository;

import java.util.List;

import nl.uva.larissa.json.model.Statement;
import nl.uva.larissa.json.model.StatementResult;

/**
 * The interface for the Repository for storing and retrieving Statement objects
 * 
 * @FIXME ugly side-effects on store-methods
 * 
 */
public interface StatementRepository {
	/**
	 * Stores a Statement
	 * 
	 * @param statement
	 *            A valid Statement according to the xAPI 1.0.1 specification.
	 *            Statement.id may be null to instruct the repository to
	 *            generate an id instead.
	 * @return The id which may be used to reference the stored Statement.
	 *         Should be the id of the original Statement if it was not null.
	 * 
	 * @throws DuplicateIdException
	 *             If Statement.id has a value that is already in use. <br>
	 * <br>
	 *             <b>side-effect</b> if Statement.id was null, Statement.id may
	 *             be set to the return-value.
	 */
	public String storeStatement(Statement statement)
			throws DuplicateIdException, VoidingTargetException;

	/**
	 * Retrieves a Statement
	 * 
	 * @param id
	 *            UUID of the Statement to get.
	 * @return A StatementResult with 1 Statement, or with an empty list if no
	 *         Statement with the given id exists or if the Statement is voided.
	 */
	public StatementResult getStatement(String id);

	/**
	 * Retrieves a voided Statement
	 * 
	 * @param id
	 *            UUID of the voided Statement to get.
	 * @return A StatementResult with 1 Statement, or with an empty list if no
	 *         voided Statement with the given id exists.
	 */
	public StatementResult getVoidedStatement(String id);

	/**
	 * Retrieves multiple Statements based on a filter
	 * 
	 * @param filter
	 *            The filter to use.
	 * @return A StatementResult with the (non-voided) Statements matching the
	 *         filter, or with an empty list if no (non-voided) Statements match
	 *         the filter criteria. If the result size exceeds
	 *         StatementFilter.limit or any other limit set on the result size,
	 *         StatementResult.more should contain a relative IRL as described
	 *         in 4.2 of the 1.0.1 xAPI specification where additional results
	 *         may be retrieved
	 */
	public StatementResult getStatements(StatementFilter filter);

	/**
	 * Stores multiple statements in a transaction; Either all statements are
	 * stored or none are when an exception occurs.
	 * 
	 * @param statements
	 *            The Statements to store. Each Statement should be valid
	 *            according to the xAPI 1.0.1 specification. Statement.id may be
	 *            null to instruct the repository to generate an id instead.
	 * @return A list of the ids which may be used to reference the stored
	 *         Statements, ordered identical to the input.
	 * 
	 * @throws DuplicateIdException
	 *             if one or more Statement.id have a value that is already in
	 *             use. No statements are stored. <br>
	 * <br>
	 *             <b>side-effect</b> if Statement.id was null, Statement.id may
	 *             be set to the return-value.
	 */
	public List<String> storeStatements(List<Statement> statements)
			throws DuplicateIdException, VoidingTargetException;

}
