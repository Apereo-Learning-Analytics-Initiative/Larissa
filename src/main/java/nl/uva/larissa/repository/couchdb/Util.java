package nl.uva.larissa.repository.couchdb;

import java.util.Date;
import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;

public class Util {

	public static StatementDocument getLastIndexed(CouchDbConnector connector) {
		String nowDateStr = MapQuery.date2String(new Date());

		ViewQuery query = new ViewQuery()
				.viewName(VerbRegistrationQuery.VIEWNAME)
				.designDocId("_design/statements")
				. startKey(ComplexKey.of("ALL", null, null, nowDateStr))
				.limit(1).staleOk(true).descending(true).inclusiveEnd(true)
				.includeDocs(true);

		List<StatementDocument> qResult = connector.queryView(query,
				StatementDocument.class);
		return qResult.size() > 0 ? qResult.get(0) : null;
	}

	public static Date getStoredDateOfLastStatement(CouchDbConnector connector) {
		StatementDocument doc = getLastIndexed(connector);
		return doc == null ? null : doc.getStatement().getStored();
	}

}
