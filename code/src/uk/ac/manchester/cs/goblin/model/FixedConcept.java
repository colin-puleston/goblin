package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class FixedConcept extends Concept {

	public boolean resetId(DynamicId newDynamicId) {

		throw createInvalidOperationException();
	}

	public boolean move(Concept newParent) {

		throw createInvalidOperationException();
	}

	public void remove() {

		throw createInvalidOperationException();
	}

	public boolean isFixed() {

		return true;
	}

	FixedConcept(Hierarchy hierarchy, EntityId rootConceptId) {

		super(hierarchy, rootConceptId);
	}

	RuntimeException createInvalidOperationException() {

		return new RuntimeException("Cannot perform operation on fixed concept!");
	}
}
