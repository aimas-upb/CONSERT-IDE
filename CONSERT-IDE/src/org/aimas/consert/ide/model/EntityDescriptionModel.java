package org.aimas.consert.ide.model;


public class EntityDescriptionModel {
	
	private String name;
	private String ID;

	protected ContextEntityModel subjectEntity;
	private ContextEntityModel objectEntity;

	public EntityDescriptionModel() {
	}

	public EntityDescriptionModel(String name, String object, ContextEntityModel subjectEntity) {
		setName(name);
		setObjectEntity(objectEntity);
		setSubjectEntity(subjectEntity);
	}
	
	public EntityDescriptionModel(String ID, String name, String comment, ContextEntityModel subjectEntity,
			ContextEntityModel objectEntity, AcquisitionType acquisitionType) {
		setID(ID);
		setName(name);
		setObjectEntity(objectEntity);
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


	public ContextEntityModel getSubjectEntity() {
		return subjectEntity;
	}
	
	public ContextEntityModel getObjectEntity() {
		return objectEntity;
	}

	public void setSubjectEntity(ContextEntityModel subjectEntity) {
		this.subjectEntity = subjectEntity;
	}
	
	public void setObjectEntity(ContextEntityModel objectEntity) {
		this.objectEntity = objectEntity;
	}

	public String toString() {
		return "{\"name\":\"" + getName() + "\",\"objectEntity\":\"" + getObjectEntity() + "\",\"subjectEntity\":" + getSubjectEntity() + "}";
	}
}
