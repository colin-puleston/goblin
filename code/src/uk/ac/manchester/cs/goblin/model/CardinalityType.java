package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public enum CardinalityType {

	SINGLE_VALUE, MULTI_VALUE;

	public boolean singleValue() {

		return this == SINGLE_VALUE;
	}

	public boolean multiValue() {

		return this == MULTI_VALUE;
	}
}
