package nl.uva.larissa.service;

import java.io.IOException;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

public class VersionRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		/*
		 * 7.7 An LRS MUST NOT reject requests based on their version header as
		 * would otherwise be required by 6.2 API Versioning.
		 */
		if ("xAPI/about".equals(requestContext.getUriInfo().getPath())
				|| !HttpMethod.GET.equals(requestContext.getMethod())) {
			return;
		}

		/*
		 * 6.2 The LRS MUST accept requests with a version header of "1.0" as if
		 * the version header was "1.0.0". The LRS MUST reject requests with
		 * version header prior to "1.0.0" unless such requests are routed to a
		 * fully conformant implementation of the prior version specified in the
		 * header. The LRS MUST reject requests with a version header of "1.1.0"
		 * or greater. The LRS MUST make these rejects by responding with an
		 * HTTP 400 error including a short description of the problem.
		 */
		String version = requestContext
				.getHeaderString("X-Experience-API-Version");
		if (version == null) {
			throw new IllegalArgumentException(
					"missing header X-Experience-API-Version");
		}
		if (!version.startsWith("1.0")) {
			throw new IllegalArgumentException(
					"invalid value for X-Experience-API-Version; this LRS expects requests for version 1.0.<X>");
		}

	}
}
