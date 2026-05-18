package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public abstract class PropertyAttributeConfig extends CoreAttributeConfig {

	private DataField<ConstraintsOption> constraintsOption;

	public void resetConstraintsOption(ConstraintsOption option) {

		checkValidConstraintsOption(option);

		constraintsOption.set(option);
	}

	public ConstraintsOption getConstraintsOption() {

		return constraintsOption.get();
	}

	PropertyAttributeConfig(
		String label,
		CoreHierarchyConfig sourceHierarchy,
		EntityId rootTargetConceptId,
		ConstraintsOption constraintsOption) {

		super(label, sourceHierarchy, rootTargetConceptId);

		checkValidConstraintsOption(constraintsOption);

		this.constraintsOption = new DataField<ConstraintsOption>(constraintsOption);
	}

	private void checkValidConstraintsOption(ConstraintsOption option) {

		if (option == ConstraintsOption.NONE) {

			throw new RuntimeException(
						"Cannot create attribute \"" + getLabel() + "\""
						+ " with constraints option: " + option);
		}
	}
}
