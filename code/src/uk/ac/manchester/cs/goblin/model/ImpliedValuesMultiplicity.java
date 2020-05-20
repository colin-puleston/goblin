package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public enum ImpliedValuesMultiplicity {

	SINGLE, MULTIPLE;

	public boolean singleValue() {

		return this == SINGLE;
	}

	public boolean multiValue() {

		return this == MULTIPLE;
	}
}
