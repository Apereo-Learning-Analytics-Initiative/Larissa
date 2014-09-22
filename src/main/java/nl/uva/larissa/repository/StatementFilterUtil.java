package nl.uva.larissa.repository;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.core.MultivaluedMap;

import nl.uva.larissa.UUIDUtil;
import nl.uva.larissa.json.ISO8601VerboseDateFormat;
import nl.uva.larissa.json.ParseException;
import nl.uva.larissa.json.StatementParser;
import nl.uva.larissa.json.StatementParserImpl;
import nl.uva.larissa.json.StatementPrinter;
import nl.uva.larissa.json.StatementPrinterImpl;
import nl.uva.larissa.json.model.Agent;

import org.apache.abdera.i18n.iri.IRI;

public class StatementFilterUtil {

	static enum Parameter {
		VERB("verb") {
			@Override
			public void setValue(StatementFilter result, String value) {
				result.setVerb(new IRI(value));
			}

			@Override
			public String getValueAsString(StatementFilter filter) {
				IRI iri = filter.getVerb();
				return iri == null ? null : iri.toString();
			}
		},
		ACTIVITY("activity") {

			@Override
			public void setValue(StatementFilter result, String value) {
				result.setActivity(new IRI(value));

			}

			@Override
			public String getValueAsString(StatementFilter filter) {
				IRI iri = filter.getActivity();
				return iri == null ? null : iri.toString();
			}

		},
		AGENT("agent") {

			@Override
			public void setValue(StatementFilter result, String value) {
				try {
					result.setAgent(getParser().parse(Agent.class, value));
				} catch (ParseException e) {
					throw new IllegalArgumentException(e);
				}
			}

			@Override
			public String getValueAsString(StatementFilter filter) {
				Agent agent = filter.getAgent();
				try {
					return agent == null ? null : getPrinter().printCompact(
							agent);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

			}
		},
		SINCE("since") {
			@Override
			public void setValue(StatementFilter result, String value) {
				try {
					result.setSince(new ISO8601VerboseDateFormat().parse(value));
				} catch (java.text.ParseException e) {
					throw new IllegalArgumentException(e);
				}
			}

			@Override
			public String getValueAsString(StatementFilter filter) {
				Date since = filter.getSince();

				return since == null ? null : new ISO8601VerboseDateFormat()
						.format(since);
			}
		},
		UNTIL("until") {
			@Override
			public void setValue(StatementFilter result, String value) {
				try {
					result.setUntil(new ISO8601VerboseDateFormat().parse(value));
				} catch (java.text.ParseException e) {
					throw new IllegalArgumentException(e);
				}
			}

			@Override
			public String getValueAsString(StatementFilter filter) {
				Date until = filter.getUntil();

				return until == null ? null : new ISO8601VerboseDateFormat()
						.format(until);
			}
		},
		LIMIT("limit") {
			@Override
			public void setValue(StatementFilter result, String value) {
				result.setLimit(Integer.parseInt(value));
			}

			@Override
			public String getValueAsString(StatementFilter filter) {
				Integer limit = filter.getLimit();
				return limit == null ? null : limit.toString();
			}
		},
		FORMAT("format") {
			@Override
			public void setValue(StatementFilter result, String value) {
				result.setFormat(value);
			}

			@Override
			public String getValueAsString(StatementFilter filter) {
				return filter.getFormat();
			}
		},
		REGISTRATION("registration") {
			@Override
			public void setValue(StatementFilter result, String value) {
				if (value != null && !UUIDUtil.isUUID(value)) {
					throw new IllegalArgumentException("'" + value
							+ "' is not a valid UUID");
				}
				result.setRegistration(value);
			}

			@Override
			public String getValueAsString(StatementFilter filter) {
				return filter.getRegistration();
			}
		},
		RELATED_ACTIVITIES("related_activities") {
			@Override
			public void setValue(StatementFilter result, String value) {
				result.setRelatedActivities(Boolean.valueOf(value));
			}

			@Override
			public String getValueAsString(StatementFilter filter) {
				Boolean result = filter.getRelatedActivities();
				return result == null ? null : Boolean.toString(result);
			}
		},
		RELATED_AGENTS("related_agents") {
			@Override
			public void setValue(StatementFilter result, String value) {
				result.setRelatedAgents(Boolean.valueOf(value));
			}

			@Override
			public String getValueAsString(StatementFilter filter) {
				Boolean result = filter.getRelatedAgents();
				return result == null ? null : Boolean.toString(result);
			}
		},
		ASCENDING("ascending") {

			@Override
			public void setValue(StatementFilter result, String value) {
				result.setAscending(Boolean.valueOf(value));
			}

			@Override
			public String getValueAsString(StatementFilter filter) {
				Boolean result = filter.getAscending();
				return result == null ? null : Boolean.toString(result);
			}
			
		},
		STARTID("startId") {
			// FIXME validate

			@Override
			public void setValue(StatementFilter result, String value) {
				result.setStartId(value);
			}

			@Override
			public String getValueAsString(StatementFilter filter) {
				return filter.getStartId();
			}

		};

		private final String name;

		// FIXME should be injected but can't with non-injected constructor
		private StatementParser parser = new StatementParserImpl();
		private StatementPrinter printer = new StatementPrinterImpl();

		@Inject
		private Parameter(String name) {
			this.name = name;
		}

		public abstract void setValue(StatementFilter result, String value);

		public String getName() {
			return name;
		}

		public abstract String getValueAsString(StatementFilter filter);

		public StatementParser getParser() {
			return parser;
		}

		public StatementPrinter getPrinter() {
			return printer;
		}
	}

	private static Map<String, Parameter> PARAMETERS;
	static {
		PARAMETERS = new HashMap<>(Parameter.values().length);
		for (Parameter arg : Parameter.values()) {
			PARAMETERS.put(arg.getName(), arg);
		}
	}

	public static StatementFilter fromParameters(
			MultivaluedMap<String, String> map) throws ValidationException {
		StatementFilter result = new StatementFilter();
		for (Entry<String, List<String>> entry : map.entrySet()) {
			List<String> entryValues = entry.getValue();
			String entryKey = entry.getKey();
			if (entryValues.size() != 1) {
				throw new ValidationException(
						String.format(
								"Parameter '%s' must have exactly 1 value, but has %s values.",
								entryKey, entryValues.size()));
			}
			String entryValue = entryValues.get(0);
			Parameter parameter = PARAMETERS.get(entryKey);
			if (parameter == null) {
				throw new ValidationException(String.format(
						"Unsupported parameter '%s'; must be one of %s.",
						entryKey, PARAMETERS.keySet()));
			}
			parameter.setValue(result, entryValue);
		}
		return result;
	}

	public static StatementFilter fromMoreUrl(String more)
			throws IllegalArgumentException {
		try {
			more = URLDecoder.decode(more, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}

		Pattern patt = Pattern.compile("(?:([a-zA-Z_]+)=([^&]+))+");
		Matcher matcher = patt.matcher(more);
		StatementFilter result = new StatementFilter();

		while (matcher.find()) {
			String key = matcher.group(1);
			String value = matcher.group(2);
			Parameter arg = PARAMETERS.get(key);
			if (arg == null) {
				throw new IllegalArgumentException("unknown parameter: " + key);
			}
			arg.setValue(result, value);
		}
		return result;
	}

	public static String toMoreUrl(StatementFilter filter) {
		// FIXME hardcoded path
		StringBuilder result = new StringBuilder("/larissa/xAPI/statements?");
		for (Parameter arg : Parameter.values()) {
			String value = arg.getValueAsString(filter);
			if (value != null) {
				try {
					result.append(arg.getName()).append('=')
							.append(URLEncoder.encode(value, "UTF-8"))
							.append('&');
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
		}
		int lastIndex = result.length() - 1;
		if (result.charAt(lastIndex) == '&') {
			result.deleteCharAt(lastIndex);
		}
		return result.toString();

	}
}
