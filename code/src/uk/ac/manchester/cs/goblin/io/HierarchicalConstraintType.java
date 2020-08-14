package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchicalConstraintType extends ConstraintType {

	private boolean singleSuperConcepts = false;

	public boolean definesValidValues() {

		return false;
	}

	public boolean definesImpliedValues() {

		return true;
	}

	public boolean singleImpliedValues() {

		return singleSuperConcepts;
	}

	HierarchicalConstraintType(
		String name,
		Concept rootSourceConcept,
		Concept rootTargetConcept) {

		super(name, rootSourceConcept, rootTargetConcept);
	}

	void setSingleSuperConcepts(boolean value) {

		singleSuperConcepts = value;
	}
}
