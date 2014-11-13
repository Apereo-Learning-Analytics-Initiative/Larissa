package nl.uva.larissa.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.uva.larissa.json.model.About;

import org.apache.abdera.i18n.iri.IRI;

@Path("/xAPI/about")
public class AboutResource {

	@GET
	public Response getAbout() {
		About about = new About();
		about.setVersion(Arrays.asList("1.0.0", "1.0.1"));
		Map<IRI, Object> extensions = new HashMap<>(1);
		extensions.put(new IRI("http://id.tincanapi.com/extension/powered-by"),
				"Apereo's Larissa LRS");
		about.setExtensions(extensions);

		return Response.ok(about, MediaType.APPLICATION_JSON).build();
	}
}
