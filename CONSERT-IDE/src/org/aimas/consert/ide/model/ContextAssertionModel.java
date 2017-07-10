package org.aimas.consert.ide.model;

import java.util.List;

public class ContextAssertionModel extends AbstractContextModel {
	private int arity;
	private List<ContextEntityModel> entities;

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
				+ "\",\"entities\":\"" + getEntities() + "\"}";
	}

}
