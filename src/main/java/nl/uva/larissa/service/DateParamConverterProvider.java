package nl.uva.larissa.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import nl.uva.larissa.json.ISO8601VerboseDateFormat;

@Provider
public class DateParamConverterProvider implements ParamConverterProvider {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ParamConverter getConverter(Class arg0, Type arg1, Annotation[] arg2) {
		if (arg1.equals(Date.class)) {
			return new DateParamConverter();
		} else {
			return null;
		}
	}

	private static class DateParamConverter implements ParamConverter<Date> {

		// TODO is this threadsafe?
		final DateFormat format = new ISO8601VerboseDateFormat();

		@Override
		public Date fromString(String arg0) {

			try {
				return format.parse(arg0);
			} catch (ParseException e) {
				throw new IllegalArgumentException(e);
			}

		}

		@Override
		public String toString(Date arg0) {
			return format.format(arg0);
		}

	}
}
