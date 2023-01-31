package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ReferenceOnlyConcept extends FixedConcept {

	public Concept addChild(EntityId id, boolean dynamicNamespace) {

		if (getModel().modelLoaded()) {

			throw createInvalidOperationException();
		}

		return super.addChild(id, dynamicNamespace);
	}

	ReferenceOnlyConcept(Hierarchy hierarchy, EntityId rootConceptId) {

		super(hierarchy, rootConceptId);
	}
}
