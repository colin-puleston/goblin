package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class NonRootCoreConcept extends Concept {

	public boolean coreConcept() {

		return true;
	}

	public boolean canResetId() {

		return false;
	}

	public boolean canMove() {

		return false;
	}

	NonRootCoreConcept(Hierarchy hierarchy, EntityId conceptId) {

		super(hierarchy, conceptId);
	}

	Concept createMovedReplacement(Concept newParent) {

		throw createInvalidOperationException();
	}
}
