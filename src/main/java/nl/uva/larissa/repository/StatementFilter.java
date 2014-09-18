package nl.uva.larissa.repository;

import java.util.Date;

import javax.ws.rs.QueryParam;

import org.apache.abdera.i18n.iri.IRI;

import nl.uva.larissa.json.model.Agent;

public class StatementFilter {

	private String statementId;
	private String voidedStatementId;
	// TODO or Identified Group!
	private Agent agent;
	private IRI verb;
	private IRI activity;
	// UUID
	private String registration;
	private Boolean relatedActivities;
	private Boolean relatedAgents;
	private Date since;
	private Date until;
	private Integer limit;
	// TODO enum?
	private String format;

	// for more-URL
	@QueryParam("startId")
	private String startId;

	public StatementFilter() {
	}

	public StatementFilter(StatementFilter filter) {
		statementId = filter.getStatementId();
		voidedStatementId = filter.getVoidedStatementid();
		agent = filter.getAgent();
		verb = filter.getVerb();
		activity = filter.getActivity();
		registration = filter.getRegistration();
		relatedActivities = filter.getRelatedActivities();
		relatedAgents = filter.getRelatedAgents();
		since = filter.getSince();
		until = filter.getUntil();
		limit = filter.getLimit();
		format = filter.getFormat();
		startId = filter.getStartId();
	}

	public String getStatementId() {
		return statementId;
	}

	public void setStatementId(String statementId) {
		this.statementId = statementId;
	}

	public String getVoidedStatementid() {
		return voidedStatementId;
	}

	public void setVoidedStatementid(String voidedStatementId) {
		this.voidedStatementId = voidedStatementId;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public IRI getVerb() {
		return verb;
	}

	public void setVerb(IRI verb) {
		this.verb = verb;
	}

	public IRI getActivity() {
		return activity;
	}

	public void setActivity(IRI activity) {
		this.activity = activity;
	}

	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}

	public Boolean getRelatedActivities() {
		return relatedActivities;
	}

	public void setRelatedActivities(Boolean relatedActivities) {
		this.relatedActivities = relatedActivities;
	}

	public Boolean getRelatedAgents() {
		return relatedAgents;
	}

	public void setRelatedAgents(Boolean relatedAgents) {
		this.relatedAgents = relatedAgents;
	}

	public String getVoidedStatementId() {
		return voidedStatementId;
	}

	@Override
	public String toString() {
		return "StatementFilter [statementId=" + statementId
				+ ", voidedStatementId=" + voidedStatementId + ", agent="
				+ agent + ", verb=" + verb + ", activity=" + activity
				+ ", registration=" + registration + ", relatedActivities="
				+ relatedActivities + ", relatedAgents=" + relatedAgents
				+ ", since=" + since + ", until=" + until + ", limit=" + limit
				+ ", format=" + format + ", startId=" + startId + "]";
	}

	public void setVoidedStatementId(String voidedStatementId) {
		this.voidedStatementId = voidedStatementId;
	}

	public Date getSince() {
		return since;
	}

	public void setSince(Date since) {
		this.since = since;
	}

	public Date getUntil() {
		return until;
	}

	public void setUntil(Date until) {
		this.until = until;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getStartId() {
		return startId;
	}

	public void setStartId(String startId) {
		this.startId = startId;
	}

}
