package nl.uva.larissa.repository.couchdb;

import java.net.MalformedURLException;

import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ITCouchDB {

	private static HttpClient httpClient;
	private StdCouchDbConnector db;

	@BeforeClass
	static public void beforeClass() {
		try {
			httpClient = new StdHttpClient.Builder().url(
					"http://localhost:5984").build();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	@AfterClass
	static public void afterClass() {
		httpClient.shutdown();
	}

	@Test
	public void doTest() {
		Item item = new Item();
		item.setItemField("test" + System.currentTimeMillis());

		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		db = new StdCouchDbConnector("mydatabase", dbInstance);
		db.createDatabaseIfNotExists();
		db.create(item);
		Assert.assertEquals(item.getItemField(), db.get(Item.class, item.getId()).getItemField());
	}

}
