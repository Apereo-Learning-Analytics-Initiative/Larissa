package nl.uva.larissa.service;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import nl.uva.larissa.json.ParseException;
import nl.uva.larissa.json.StatementParser;
import nl.uva.larissa.json.StatementPrinter;
import nl.uva.larissa.json.model.Agent;

@Provider
public class AgentStringReaderProvider implements ParamConverterProvider {

	@Inject
	StatementParser parser;

	@Inject
	StatementPrinter printer;

	@SuppressWarnings("unchecked")
	@Override
	public <T> ParamConverter<T> getConverter(Class<T> arg0, Type arg1,
			Annotation[] arg2) {
		if (arg1.equals(Agent.class)) {
			return (ParamConverter<T>) new AgentParamConverter();
		}
		return null;
	}
	
	// TODO static class (but keep injection working?)

	class AgentParamConverter implements ParamConverter<Agent> {

		@Override
		public Agent fromString(String arg0) {
			try {
				return parser.parse(Agent.class, arg0);
			} catch (ParseException e) {
				throw new IllegalArgumentException(e);
			}
		}

		@Override
		public String toString(Agent arg0) {
			try {
				return printer.printCompact(arg0);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}
}