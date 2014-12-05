package nl.uva.larissa.service;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

public class CORSResponseFilter implements ContainerResponseFilter {

	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) throws IOException {

		MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods",
				"HEAD, POST, GET, OPTIONS, DELETE, PUT");
		headers.add(
				"Access-Control-Allow-Headers",
				"Content-Type,Content-Length,Authorization,If-Match,If-None-Match,X-Experience-API-Version, Accept-Language");

		/*
		 * 6.2 API Versioning
		 * The LRS MUST include the "X-Experience-API-Version" header in every
		 * response. The LRS MUST set this header to "1.0.1".
		 */
		headers.add("X-Experience-API-Version", "1.0.1");
	}

}