package uk.ac.manchester.cs.goblin.config;

/**
 * @author Colin Puleston
 */
public abstract class LabelledConfigEntity {

	private String label;

	public void resetLabel(String label) {

		this.label = label;
	}

	public String getLabel() {

		return label;
	}

	LabelledConfigEntity(String label) {

		this.label = label;
	}
}
