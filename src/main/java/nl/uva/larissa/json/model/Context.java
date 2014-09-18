package nl.uva.larissa.json.model;

import java.util.Map;

import javax.validation.Valid;

import org.apache.abdera.i18n.iri.IRI;

import nl.uva.larissa.json.model.validate.IsUUID;
import nl.uva.larissa.json.model.validate.LanguageMapKey;

public class Context {
	@IsUUID
	private String registration;
	@Valid
	private Instructor instructor;
	@Valid
	private Group team;
	@Valid
	private ContextActivities contextActivities;
	private String revision;
	private String platform;
	@LanguageMapKey
	private String language;
	@Valid
	private StatementRef statement;
	private Map<IRI, Object> extensions;
	public String getRegistration() {
		return registration;
	}
	public void setRegistration(String registration) {
		this.registration = registration;
	}
	public Instructor getInstructor() {
		return instructor;
	}
	public void setInstructor(Instructor instructor) {
		this.instructor = instructor;
	}
	public Group getTeam() {
		return team;
	}
	public void setTeam(Group team) {
		this.team = team;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public StatementRef getStatement() {
		return statement;
	}
	public void setStatement(StatementRef statement) {
		this.statement = statement;
	}
	public Map<IRI, Object> getExtensions() {
		return extensions;
	}
	public void setExtensions(Map<IRI, Object> extensions) {
		this.extensions = extensions;
	}
	public ContextActivities getContextActivities() {
		return contextActivities;
	}
	public void setContextActivities(ContextActivities contextActivities) {
		this.contextActivities = contextActivities;
	}
	

}
