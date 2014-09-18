package nl.uva.larissa.service;

import java.util.List;

import javax.validation.ValidationException;
import javax.ws.rs.core.MultivaluedMap;

import nl.uva.larissa.repository.StatementFilter;
import nl.uva.larissa.repository.StatementFilterUtil;
import nl.uva.larissa.service.RequestTypeParser.Result.Type;

public class RequestTypeParser {

	private static final String KEY_VOIDED_ID = "voidedStatementId";
	private static final String KEY_ID = "statementId";

	public static class Result {

		public enum Type {
			SINGLE, VOIDED, FILTER, INVALID

		};

		private final Type type;
		private final String message;
		private final String statementId;
		private final StatementFilter statementFilter;

		Result(Type type, String statementId, StatementFilter statementFilter,
				String message) {
			this.type = type;
			this.statementId = statementId;
			this.statementFilter = statementFilter;
			this.message = message;
		}

		public Type getType() {
			return type;
		}

		/**
		 * @return validation error-message if type=INVALID, otherwise null
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * @return (voided) statementId if type=SINGLE or type=VOIDED, otherwise
		 *         null
		 */
		public String getStatementId() {
			return statementId;
		}

		/**
		 * @return a StatementFilter if type=FILTER, otherwise null
		 */
		public StatementFilter getStatementFilter() {
			return statementFilter;
		}

		public static Result invalid(String message) {
			return new Result(Type.INVALID, null, null, message);
		}

		public static Result id(Type type, String id) {
			if (type != Type.SINGLE && type != Type.VOIDED) {
				throw new IllegalArgumentException(
						"type must be one of {SINGLE,VOIDED}");
			}
			return new Result(type, id, null, null);
		}

		public static Result filter(StatementFilter filter) {
			return new Result(Type.FILTER, null, filter, null);
		}

	}

	public Result parse(MultivaluedMap<String, String> parameters) {
		Result result;
		if (parameters.containsKey(KEY_ID)) {
			result = parseId(parameters, Type.SINGLE, KEY_ID);
		} else if (parameters.containsKey(KEY_VOIDED_ID)) {
			result = parseId(parameters, Type.VOIDED, KEY_VOIDED_ID);
		} else {
			try {
				result = Result.filter(StatementFilterUtil
						.fromParameters(parameters));
			} catch (ValidationException e) {
				result = Result.invalid(e.getMessage());
			}
		}
		return result;
	}

	private Result parseId(MultivaluedMap<String, String> parameters,
			Type type, String parameterId) {
		Result result;
		if (parameters.size() > 1) {
			result = Result.invalid(String.format(
					"Parameter '%s' cannot be combined with other parameters.",
					parameterId));
		} else {
			List<String> values = parameters.get(parameterId);
			if (values.size() != 1) {
				result = Result
						.invalid(String
								.format("Parameter '%s' must have exactly one value, but has %s values",
										parameterId, values.size()));
			} else {
				result = Result.id(type, values.get(0));
			}
		}
		return result;
	}
}
