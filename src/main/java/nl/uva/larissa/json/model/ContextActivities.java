package nl.uva.larissa.json.model;

import java.util.ArrayList;

import javax.validation.Valid;

public class ContextActivities {
	@Valid
	private ArrayList<Activity> parent;
	@Valid
	private ArrayList<Activity> grouping;
	@Valid
	private ArrayList<Activity> category;
	@Valid
	private ArrayList<Activity> other;

	public ArrayList<Activity> getParent() {
		return parent;
	}
	public void setParent(ArrayList<Activity> parent) {
		this.parent = parent;
	}
	public ArrayList<Activity> getGrouping() {
		return grouping;
	}
	public void setGrouping(ArrayList<Activity> grouping) {
		this.grouping = grouping;
	}
	public ArrayList<Activity> getCategory() {
		return category;
	}
	public void setCategory(ArrayList<Activity> category) {
		this.category = category;
	}
	public ArrayList<Activity> getOther() {
		return other;
	}
	public void setOther(ArrayList<Activity> other) {
		this.other = other;
	}
	

}
