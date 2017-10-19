package org.aimas.consert.ide.model;

import java.util.ArrayList;
import java.util.List;

public class ContextAssertionModel {
	public static final int BINARY = 2;
	private String name;
	private String comment;

	protected ContextEntityModel subjectEntity;
	protected ContextEntityModel objectEntity;

	protected AcquisitionType acquisitionType = AcquisitionType.SENSED;
	protected int arity = BINARY;
	private List<ContextEntityModel> entities;

	public ContextAssertionModel() {
		entities = new ArrayList<ContextEntityModel>();
	}

	public ContextAssertionModel(String name, String comment, ContextEntityModel subjectEntity,
			ContextEntityModel objectEntity, AcquisitionType acquisitionType) {
		entities = new ArrayList<ContextEntityModel>();
		setName(name);
		setComment(comment);
		setSubjectEntity(subjectEntity);
		setObjectEntity(objectEntity);
		setAcquisitionType(acquisitionType);
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
		if (!entities.contains(subjectEntity)) {
			entities.remove(this.subjectEntity);
			entities.add(subjectEntity);
			this.subjectEntity = subjectEntity;
		}
	}

	public ContextEntityModel getObjectEntity() {
		return objectEntity;
	}

	public void setObjectEntity(ContextEntityModel objectEntity) {
		if (!entities.contains(objectEntity)) {
			entities.remove(this.objectEntity);
			entities.add(objectEntity);
			this.objectEntity = objectEntity;
		}
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

	public String toString() {
		return "{\"name\":\"" + getName() + "\",\"comment\":\"" + getComment() + "\",\"arity\":\"" + getArity()
				+ "\",\"entities\":" + getEntities() + "}";
	}
}
