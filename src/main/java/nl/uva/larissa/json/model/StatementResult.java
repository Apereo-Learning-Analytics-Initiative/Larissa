package nl.uva.larissa.json.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StatementResult {

	private List<Statement> statements;
	// xAPI 1.0.1 4.2 Empty string if there are no more results to fetch
	private String more = "";

	@JsonIgnore
	private Date consistentThrough;

	public StatementResult(List<Statement> statements, Date consistentThrough) {
		this.statements = statements;
		this.consistentThrough = consistentThrough;
	}

	public Date getConsistentThrough() {
		return consistentThrough;
	}

	public void setConsistentThrough(Date consistentThrough) {
		this.consistentThrough = consistentThrough;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	public String getMore() {
		return more;
	}

	public void setMore(String more) {
		this.more = more;
	}
}
