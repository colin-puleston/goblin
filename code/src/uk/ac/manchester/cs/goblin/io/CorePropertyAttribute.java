package uk.ac.manchester.cs.goblin.io;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class CorePropertyAttribute extends CoreAttribute {

	private ConstraintsOption constraintsOption;

	public ConstraintsOption getConstraintsOption() {

		return constraintsOption;
	}

	CorePropertyAttribute(
		String label,
		Concept rootSourceConcept,
		Concept rootTargetConcept,
		ConstraintsOption constraintsOption) {

		super(label, rootSourceConcept, rootTargetConcept);

		this.constraintsOption = constraintsOption;
	}
}
