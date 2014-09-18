package nl.uva.larissa.service;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.jersey.server.ParamException;
import org.glassfish.jersey.server.ParamException.FormParamException;
import org.glassfish.jersey.server.ParamException.QueryParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class MultiExceptionMapper implements ExceptionMapper<MultiException> {

	private static Logger LOGGER = LoggerFactory
			.getLogger(MultiExceptionMapper.class);

	// FIXME doesn't seem to catch e.g. turning off CouchDB??
	@Override
	public Response toResponse(MultiException exc) {
		LOGGER.debug("mapping " + exc.getClass(), exc);
		List<Throwable> errors = exc.getErrors();
		if (errors.size() > 0) {
			Throwable cause = errors.get(0);
			if (cause instanceof QueryParamException
					|| cause instanceof FormParamException) {
				ParamException paramException = (ParamException) cause;
				return Response
						.status(400)
						.type(MediaType.TEXT_PLAIN)
						.entity("illegal value for parameter '"
								+ paramException.getParameterName() + "'")
						.build();
			}

		}
		return Response.serverError().build();
	}
}
