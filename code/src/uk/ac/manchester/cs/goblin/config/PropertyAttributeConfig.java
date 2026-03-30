package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public abstract class PropertyAttributeConfig extends CoreAttributeConfig {

	private ConstraintsOption constraintsOption;

	public void resetConstraintsOption(ConstraintsOption constraintsOption) {

		setConstraintsOption(constraintsOption);
	}

	public ConstraintsOption getConstraintsOption() {

		return constraintsOption;
	}

	PropertyAttributeConfig(
		String label,
		EntityId rootSourceConceptId,
		EntityId rootTargetConceptId,
		ConstraintsOption constraintsOption) {

		super(label, rootSourceConceptId, rootTargetConceptId);

		setConstraintsOption(constraintsOption);
	}

	private void setConstraintsOption(ConstraintsOption constraintsOption) {

		this.constraintsOption = constraintsOption;

		if (constraintsOption == ConstraintsOption.NONE) {

			throw new RuntimeException(
						"Cannot create attribute \"" + getLabel() + "\""
						+ " with constraints option: " + constraintsOption);
		}
	}
}
