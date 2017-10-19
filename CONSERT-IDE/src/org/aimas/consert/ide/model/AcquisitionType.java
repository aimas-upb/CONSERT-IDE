package org.aimas.consert.ide.model;

public enum AcquisitionType {
	DERIVED, PROFILED, SENSED;

	public static AcquisitionType toValue(String acquisitionTypeName) {
		if (AcquisitionType.DERIVED.toString().equals(acquisitionTypeName))
			return AcquisitionType.DERIVED;
		else if (AcquisitionType.SENSED.toString().equals(acquisitionTypeName))
			return AcquisitionType.SENSED;
		return AcquisitionType.PROFILED;
	}
}
