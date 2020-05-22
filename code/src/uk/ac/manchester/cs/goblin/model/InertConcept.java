package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class InertConcept extends Concept {

	public boolean resetId(DynamicId newDynamicId) {

		throw createInvalidOperationException();
	}

	public boolean move(Concept newParent) {

		throw createInvalidOperationException();
	}

	public void remove() {

		throw createInvalidOperationException();
	}

	public Concept addChild(EntityId id) {

		if (getModel().modelLoaded()) {

			throw createInvalidOperationException();
		}

		return super.addChild(id);
	}

	InertConcept(Hierarchy hierarchy, EntityId rootConceptId) {

		super(hierarchy, rootConceptId);
	}

	private RuntimeException createInvalidOperationException() {

		return new RuntimeException("Cannot perform operation on inert concept!");
	}
}
