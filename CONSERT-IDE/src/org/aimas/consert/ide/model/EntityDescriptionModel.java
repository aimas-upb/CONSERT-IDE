package org.aimas.consert.ide.model;


public class EntityDescriptionModel {
	
	private String name;
	private String object;
	private String ID;

	protected ContextEntityModel subjectEntity;

	public EntityDescriptionModel() {
	}

	public EntityDescriptionModel(String name, String object, ContextEntityModel subjectEntity) {
		setName(name);
		setObject(object);
		setSubjectEntity(subjectEntity);
	}
	
	public EntityDescriptionModel(String ID, String name, String comment, ContextEntityModel subjectEntity,
			ContextEntityModel objectEntity, AcquisitionType acquisitionType) {
		setID(ID);
		setName(name);
		setObject(object);
		setSubjectEntity(subjectEntity);
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

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public ContextEntityModel getSubjectEntity() {
		return subjectEntity;
	}

	public void setSubjectEntity(ContextEntityModel subjectEntity) {
		this.subjectEntity = subjectEntity;
	}

	public String toString() {
		return "{\"name\":\"" + getName() + "\",\"object\":\"" + getObject() + "\",\"arity\":\"" + "\",\"subjectEntity\":" + getSubjectEntity() + "}";
	}
}
