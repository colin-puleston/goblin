package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchicalAttribute extends CoreAttribute {

	public ConstraintsOption getConstraintsOption() {

		return ConstraintsOption.SINGLE_IMPLIED_VALUES_ONLY;
	}

	HierarchicalAttribute(String label, Concept rootSourceConcept, Concept rootTargetConcept) {

		super(label, rootSourceConcept, rootTargetConcept);
	}
}
