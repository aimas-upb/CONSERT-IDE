package org.aimas.consert.ide.model;

import java.util.List;

public class ContextAssertionModel {
	private String name;
	private String comment;
	private int arity;
	private List<ContextEntityModel> entities;

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
		return "ContextAssertionModel [ name: " + name + ", comment: " + comment + ", arity: " + arity + ", entities: "
				+ entities + " ] ";
	}

}
