package org.aimas.consert.ide.model;

public class ContextEntityModel {
	private String name;
	private String comment;
	
	public ContextEntityModel() {
	}

	public ContextEntityModel(String name, String comment) {
		setName(name);
		setComment(comment);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String toString() {
		return "{\"name\":\"" + getName() + "\",\"comment\":\"" + getComment() + "\"}";
	}
}
