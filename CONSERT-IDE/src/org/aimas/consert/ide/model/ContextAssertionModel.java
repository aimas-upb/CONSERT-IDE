package org.aimas.consert.ide.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContextAssertionModel {
	public static final int BINARY = 2;
	private String name;
	private String comment;
	private String ID;

	protected ContextEntityModel subjectEntity;
	protected ContextEntityModel objectEntity;

	protected AcquisitionType acquisitionType = AcquisitionType.SENSED;
	protected int arity = BINARY;

	public ContextAssertionModel() {
	}

	public ContextAssertionModel(String name, String comment, ContextEntityModel subjectEntity,
			ContextEntityModel objectEntity, AcquisitionType acquisitionType) {
		setName(name);
		setComment(comment);
		setSubjectEntity(subjectEntity);
		setObjectEntity(objectEntity);
		setAcquisitionType(acquisitionType);
	}
	
	public ContextAssertionModel(String ID, String name, String comment, ContextEntityModel subjectEntity,
			ContextEntityModel objectEntity, AcquisitionType acquisitionType) {
		setID(ID);
		setName(name);
		setComment(comment);
		setSubjectEntity(subjectEntity);
		setObjectEntity(objectEntity);
		setAcquisitionType(acquisitionType);
	}
	
	public void setID(String ID){
		this.ID = ID;
	}
	
	public String getID(){
		return this.ID;
	}

	public AcquisitionType getAcquisitionType() {
		return acquisitionType;
	}

	public void setAcquisitionType(AcquisitionType acquisitionType) {
		this.acquisitionType = acquisitionType;
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

	public ContextEntityModel getSubjectEntity() {
		return subjectEntity;
	}

	public void setSubjectEntity(ContextEntityModel subjectEntity) {
		this.subjectEntity = subjectEntity;
	}

	public ContextEntityModel getObjectEntity() {
		return objectEntity;
	}

	public void setObjectEntity(ContextEntityModel objectEntity) {
		this.objectEntity = objectEntity;
	}

	public int getArity() {
		return arity;
	}

	public void setArity(int arity) {
		this.arity = arity;
	}

	public List<ContextEntityModel> getEntities() {
		return new ArrayList<ContextEntityModel>(Arrays.asList(subjectEntity, objectEntity));
	}

	public String toString() {
		return "{\"name\":\"" + getName() + "\",\"comment\":\"" + getComment() + "\",\"arity\":\"" + getArity()
				+ "\",\"entities\":" + getEntities() + "}";
	}
}
