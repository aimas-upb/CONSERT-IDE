package org.aimas.consert.ide.model;

public enum AnnotationType {
	TIMESTAMP, TRUST;

	public static AnnotationType toValue(String annotationType) {
		if (AnnotationType.TIMESTAMP.toString().equals(annotationType))
			return AnnotationType.TIMESTAMP;
		return AnnotationType.TRUST;
	}
}
