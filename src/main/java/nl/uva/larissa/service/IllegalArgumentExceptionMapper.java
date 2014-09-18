package nl.uva.larissa.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class IllegalArgumentExceptionMapper implements
		ExceptionMapper<IllegalArgumentException> {

	@Override
	public Response toResponse(IllegalArgumentException arg0) {
		// FIX don't expose too much internal detail still
		return Response.status(400).type(MediaType.TEXT_PLAIN)
				.entity(arg0.getMessage()).build();
	}
}
