package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class DynamicValueHierarchy extends Hierarchy {

	public boolean potentiallyHasInwardAttributes() {

		return true;
	}

	DynamicValueHierarchy(Model model, EntityId rootConceptId) {

		super(model, rootConceptId);
	}

	RootConcept createRootConcept(EntityId rootConceptId) {

		return new RootDynamicConcept(this, rootConceptId);
	}
}
