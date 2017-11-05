package org.aimas.consert.ide.model;

public class ContextEntityModel {
	private String name;
	private String comment;
	private String ID;
	
	public ContextEntityModel() {
	}

	public ContextEntityModel(String name, String comment) {
		setName(name);
		setComment(comment);
	}
	
	public ContextEntityModel(String ID, String name, String comment) {
		setName(name);
		setComment(comment);
		setID(ID);
	}
	
	public void setID(String ID){
		this.ID = ID;
	}
	
	public String getID(){
		return this.ID;
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
