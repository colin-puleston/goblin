package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class RootDynamicConcept extends RootConcept {

	public boolean coreConcept() {

		return false;
	}

	public boolean canResetId() {

		return true;
	}

	RootDynamicConcept(Hierarchy hierarchy, EntityId conceptId) {

		super(hierarchy, conceptId);
	}
}
