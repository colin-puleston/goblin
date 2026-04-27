package uk.ac.manchester.cs.goblin.config;

/**
 * @author Colin Puleston
 */
public abstract class LabelledConfigObject {

	private String label;

	public void resetLabel(String label) {

		this.label = label;
	}

	public String getLabel() {

		return label;
	}

	public String toString() {

		return label;
	}

	LabelledConfigObject(String label) {

		this.label = label;
	}
}
