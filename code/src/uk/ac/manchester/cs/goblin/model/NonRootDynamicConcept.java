package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class NonRootDynamicConcept extends Concept {

	public boolean coreConcept() {

		return false;
	}

	public boolean canResetId() {

		return true;
	}

	public boolean canMove() {

		return true;
	}

	NonRootDynamicConcept(Hierarchy hierarchy, EntityId conceptId) {

		super(hierarchy, conceptId);
	}

	Concept createMovedReplacement(Concept newParent) {

		return new NonRootDynamicConcept(this, newParent);
	}

	private NonRootDynamicConcept(Concept replaced, Concept newParent) {

		super(replaced, newParent);
	}
}
