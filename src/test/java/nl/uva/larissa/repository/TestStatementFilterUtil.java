package nl.uva.larissa.repository;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.junit.Test;

public class TestStatementFilterUtil {

	@Test
	public void testFromMoreUrl() throws UnsupportedEncodingException,
			IllegalArgumentException, URISyntaxException {

		String path = "/larissa/xAPI/statements?";
		checkRoundTrip(path);
		checkRoundTrip(path + "limit=10");
		String since = URLEncoder.encode("2014-05-26T10:39:24.594Z", "UTF-8");
		checkRoundTrip(path + "since=" + since + "&limit=5");
		String agent = URLEncoder
				.encode("{\"objectType\":\"Agent\",\"mbox\":\"mailto:tincanphp-github@tincanapi.com\"}",
						"UTF-8");
		checkRoundTrip(path + "agent=" + agent + "&limit=4");
		checkRoundTrip(path + "agent=" + agent + "&related_activities=true");

	}

	private void checkRoundTrip(String moreUrl)
			throws IllegalArgumentException, URISyntaxException {
		StatementFilter filter = StatementFilterUtil.fromMoreUrl(moreUrl);
		assertEquals(moreUrl, StatementFilterUtil.toMoreUrl(filter));
	}
}
