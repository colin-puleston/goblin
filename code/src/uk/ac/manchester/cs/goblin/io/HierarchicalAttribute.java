package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchicalAttribute extends Attribute {

	private String label;
	private boolean singleSuperConcepts = false;

	public String getLabel() {

		return label;
	}

	public boolean definesValidValues() {

		return false;
	}

	public boolean definesImpliedValues() {

		return true;
	}

	public boolean singleImpliedValues() {

		return singleSuperConcepts;
	}

	HierarchicalAttribute(String label, Concept rootSourceConcept, Concept rootTargetConcept) {

		super(rootSourceConcept, rootTargetConcept);

		this.label = label;
	}

	void setSingleSuperConcepts(boolean value) {

		singleSuperConcepts = value;
	}
}
