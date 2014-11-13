package nl.uva.larissa;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import nl.uva.larissa.ConfigReader.Key;
import nl.uva.larissa.json.StatementParser;
import nl.uva.larissa.json.StatementParserImpl;
import nl.uva.larissa.json.StatementPrinter;
import nl.uva.larissa.json.StatementPrinterImpl;
import nl.uva.larissa.repository.StatementRepository;
import nl.uva.larissa.repository.couchdb.CouchDbStatementRepository;
import nl.uva.larissa.repository.couchdb.IQueryResolver;
import nl.uva.larissa.repository.couchdb.QueryResolver;
import nl.uva.larissa.service.AboutResource;
import nl.uva.larissa.service.AgentStringReaderProvider;
import nl.uva.larissa.service.CORSResponseFilter;
import nl.uva.larissa.service.DateParamConverterProvider;
import nl.uva.larissa.service.IllegalArgumentExceptionMapper;
import nl.uva.larissa.service.MultiExceptionMapper;
import nl.uva.larissa.service.StatementsResource;
import nl.uva.larissa.service.welcome.WelcomeController;

import org.ektorp.CouchDbConnector;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class Application extends ResourceConfig {

	@Inject
	public Application(ServletContext context) {

		ConfigReader config = new ConfigReader(context);
		final CouchDbConnector connector = new CouchDbConnectorFactory()
				.createConnector(config.get(Key.COUCHDB_URL), config
						.get(Key.COUCHDB_DB_NAME), Integer.parseInt(config
						.get(Key.COUCHDB_MAX_CONNECTIONS)));

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		final Validator validator = factory.getValidator();

		register(new AbstractBinder() {

			@Override
			protected void configure() {
				bind(validator).to(Validator.class);
				bind(connector).to(CouchDbConnector.class);
				bind(CouchDbStatementRepository.class).to(
						StatementRepository.class).in(Singleton.class);
				bind(StatementParserImpl.class).to(StatementParser.class);
				bind(StatementPrinterImpl.class).to(StatementPrinter.class);
				bind(QueryResolver.class).to(IQueryResolver.class);

			}
		});
		register(CORSResponseFilter.class);
		register(JspMvcFeature.class);
		register(WelcomeController.class);
		register(AgentStringReaderProvider.class);
		register(MultiExceptionMapper.class);
		register(IllegalArgumentExceptionMapper.class);
		register(DateParamConverterProvider.class);
		register(StatementsResource.class);
		register(AboutResource.class);
		register(JacksonJsonProvider.class);
	}
}
