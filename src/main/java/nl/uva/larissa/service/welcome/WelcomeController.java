package nl.uva.larissa.service.welcome;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ektorp.CouchDbConnector;
import org.glassfish.jersey.server.mvc.Viewable;

@Path("/")
public class WelcomeController {

	@Inject
	private CouchDbConnector connector;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable get() {
		Map<String, String> model = new HashMap<String, String>();
		long docCount = connector.getDbInfo().getDocCount();
		// #statements = docCount - number of design-documents
		model.put("statementCount", Long.toString(docCount - 1));
		return new Viewable("/welcome.jsp", model);
	}
}
