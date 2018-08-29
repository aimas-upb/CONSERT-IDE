package org.aimas.consert.ide.model;

public enum AnnotationCategory {
	SIMPLE, STRUCTURED;

	public static AnnotationCategory toValue(String annotationCategory) {
		if (AnnotationCategory.SIMPLE.toString().equals(annotationCategory))
			return AnnotationCategory.SIMPLE;
		return AnnotationCategory.STRUCTURED;
	}
}
