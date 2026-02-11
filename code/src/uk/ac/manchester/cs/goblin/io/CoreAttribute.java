package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class CoreAttribute extends Attribute {

	private String label;
	private ConstraintsOption constraintsOption;

	public String getLabel() {

		return label;
	}

	public ConstraintsOption getConstraintsOption() {

		return constraintsOption;
	}

	CoreAttribute(
		String label,
		Concept rootSourceConcept,
		Concept rootTargetConcept,
		ConstraintsOption constraintsOption) {

		super(rootSourceConcept, rootTargetConcept);

		this.label = label;
		this.constraintsOption = constraintsOption;
	}
}
