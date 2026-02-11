package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
enum HierarchicalLinksOption {

	SINGLE_SUPER_CLASSES(ConstraintsOption.SINGLE_IMPLIED_VALUES_ONLY),
	MULTI_SUPER_CLASSES(ConstraintsOption.MULTI_IMPLIED_VALUES_ONLY);

	ConstraintsOption toConstraintsOption() {

		return constraintsOption;
	}

	private ConstraintsOption constraintsOption;

	HierarchicalLinksOption(ConstraintsOption constraintsOption) {

		this.constraintsOption = constraintsOption;
	}
}
