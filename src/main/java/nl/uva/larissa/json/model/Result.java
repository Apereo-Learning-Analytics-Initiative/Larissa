package nl.uva.larissa.json.model;

import java.util.Map;

import org.apache.abdera.i18n.iri.IRI;

public class Result {
	private Score score;
	private Boolean success;
	private Boolean completion;
	private String response;
	// TODO joda.time Period?
	private String duration;
	private Map<IRI, Object> extensions;

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Boolean getCompletion() {
		return completion;
	}

	public void setCompletion(Boolean completion) {
		this.completion = completion;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Map<IRI, Object> getExtensions() {
		return extensions;
	}

	public void setExtensions(Map<IRI, Object> extensions) {
		this.extensions = extensions;
	}

}
