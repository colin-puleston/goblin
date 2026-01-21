package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class CoreAttribute extends Attribute {

	private String label;

	public String getLabel() {

		return label;
	}

	CoreAttribute(String label, Concept rootSourceConcept, Concept rootTargetConcept) {

		super(rootSourceConcept, rootTargetConcept);

		this.label = label;
	}
}
