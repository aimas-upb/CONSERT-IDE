package org.aimas.consert.ide.model;

import java.util.ArrayList;
import java.util.List;

public class ContextAssertionModel {
	private String name;
	private String comment;
	private int arity;
	private List<ContextEntityModel> entities;

	public ContextAssertionModel() {
		entities = new ArrayList<ContextEntityModel>();
	}

	public ContextAssertionModel(String name, String comment, int arity, List<ContextEntityModel> entities) {
		setName(name);
		setComment(comment);
		setArity(arity);
		setEntities(entities);
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

	public int getArity() {
		return arity;
	}

	public void setArity(int arity) {
		this.arity = arity;
	}

	public List<ContextEntityModel> getEntities() {
		return entities;
	}

	public void setEntities(List<ContextEntityModel> entities) {
		this.entities = entities;
	}

	public String toString() {
		return "{\"name\":\"" + getName() + "\",\"comment\":\"" + getComment() + "\",\"arity\":\"" + getArity()
				+ "\",\"entities\":" + getEntities() + "}";
	}
}
