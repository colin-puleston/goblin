package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class RootCoreConcept extends RootConcept {

	public boolean coreConcept() {

		return true;
	}

	public boolean canResetId() {

		return false;
	}

	RootCoreConcept(Hierarchy hierarchy, EntityId conceptId) {

		super(hierarchy, conceptId);
	}
}
