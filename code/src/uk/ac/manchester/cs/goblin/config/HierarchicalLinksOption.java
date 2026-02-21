package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public enum HierarchicalLinksOption {

	SINGLE_SUPER_CLASSES(ConstraintsOption.SINGLE_IMPLIED_VALUES_ONLY),
	MULTI_SUPER_CLASSES(ConstraintsOption.MULTI_IMPLIED_VALUES_ONLY);

	private ConstraintsOption constraintsOption;

	ConstraintsOption toConstraintsOption() {

		return constraintsOption;
	}

	private HierarchicalLinksOption(ConstraintsOption constraintsOption) {

		this.constraintsOption = constraintsOption;
	}
}
