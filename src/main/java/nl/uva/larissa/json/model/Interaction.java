package nl.uva.larissa.json.model;

import java.util.List;

import javax.validation.Valid;

public class Interaction {

	public String interactionType;
	public List<String> correctResponsesPattern;
	@Valid
	public List<InteractionComponent> choices;
	@Valid
	public List<InteractionComponent> scale;
	@Valid
	public List<InteractionComponent> source;
	@Valid
	public List<InteractionComponent> target;
	@Valid
	public List<InteractionComponent> steps;

	public String getInteractionType() {
		return interactionType;
	}

	public void setInteractionType(String interactionType) {
		this.interactionType = interactionType;
	}

	public List<String> getCorrectResponsesPattern() {
		return correctResponsesPattern;
	}

	public void setCorrectResponsesPattern(List<String> correctResponsesPattern) {
		this.correctResponsesPattern = correctResponsesPattern;
	}

	public List<InteractionComponent> getChoices() {
		return choices;
	}

	public void setChoices(List<InteractionComponent> choices) {
		this.choices = choices;
	}

	public List<InteractionComponent> getScale() {
		return scale;
	}

	public void setScale(List<InteractionComponent> scale) {
		this.scale = scale;
	}

	public List<InteractionComponent> getSource() {
		return source;
	}

	public void setSource(List<InteractionComponent> source) {
		this.source = source;
	}

	public List<InteractionComponent> getTarget() {
		return target;
	}

	public void setTarget(List<InteractionComponent> target) {
		this.target = target;
	}

	public List<InteractionComponent> getSteps() {
		return steps;
	}

	public void setSteps(List<InteractionComponent> steps) {
		this.steps = steps;
	}

}
