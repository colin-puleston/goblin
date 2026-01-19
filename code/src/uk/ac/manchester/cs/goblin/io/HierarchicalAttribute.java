package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchicalAttribute extends Attribute {

	private String name;
	private boolean singleSuperConcepts = false;

	public String getName() {

		return name;
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

	HierarchicalAttribute(String name, Concept rootSourceConcept, Concept rootTargetConcept) {

		super(rootSourceConcept, rootTargetConcept);

		this.name = name;
	}

	void setSingleSuperConcepts(boolean value) {

		singleSuperConcepts = value;
	}
}
