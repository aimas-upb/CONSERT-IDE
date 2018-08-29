package org.aimas.consert.ide.model;


public class ContextAnnotationModel {
	
	private String name;
	private String ID;

	protected AnnotationType annotationType = AnnotationType.TIMESTAMP;
	protected AnnotationCategory annotationCategory = AnnotationCategory.SIMPLE;

	public ContextAnnotationModel() {
	}

	public ContextAnnotationModel(String name, AnnotationType annotationType, AnnotationCategory annotationCategory) {
		setName(name);
		setAnnotationType(annotationType);
		setAnnotationCategory(annotationCategory);
	}
	
	public ContextAnnotationModel(String ID, String name, AnnotationType annotationType, AnnotationCategory annotationCategory) {
		setID(ID);
		setName(name);
		setAnnotationType(annotationType);
		setAnnotationCategory(annotationCategory);
	}
	
	public void setID(String ID){
		this.ID = ID;
	}
	
	public String getID(){
		return this.ID;
	}

	public AnnotationType getAnnotationType() {
		return annotationType;
	}

	public void setAnnotationType(AnnotationType annotationType) {
		this.annotationType = annotationType;
	}
	
	public AnnotationCategory getAnnotationCategory() {
		return annotationCategory;
	}

	public void setAnnotationCategory(AnnotationCategory annotationCategory) {
		this.annotationCategory = annotationCategory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public String toString() {
		return "{\"name\":\"" + getName() + "\",\"Annotation Type\":\"" + getAnnotationType() + "\",\"Annotation Category\":\"" + getAnnotationCategory() + "}";
	}
}
