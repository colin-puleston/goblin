package uk.ac.manchester.cs.goblin.io.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public abstract class PropertyAttributeConfig extends CoreAttributeConfig {

	private ConstraintsOption constraintsOption;

	public ConstraintsOption getConstraintsOption() {

		return constraintsOption;
	}

	PropertyAttributeConfig(
		String label,
		EntityId rootSourceConceptId,
		EntityId rootTargetConceptId,
		ConstraintsOption constraintsOption) {

		super(label, rootSourceConceptId, rootTargetConceptId);

		this.constraintsOption = constraintsOption;

		if (constraintsOption == ConstraintsOption.NONE) {

			throw new RuntimeException(
						"Cannot create attribute \"" + label + "\""
						+ " with constraints option: " + constraintsOption);
		}
	}
}
