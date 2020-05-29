package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ReferenceOnlyConcept extends FixedConcept {

	public Concept addChild(EntityId id) {

		if (getModel().modelLoaded()) {

			throw createInvalidOperationException();
		}

		return super.addChild(id);
	}

	ReferenceOnlyConcept(Hierarchy hierarchy, EntityId rootConceptId) {

		super(hierarchy, rootConceptId);
	}

	String getFixedConceptTypeDecriptor() {

		return "reference-only";
	}
}
