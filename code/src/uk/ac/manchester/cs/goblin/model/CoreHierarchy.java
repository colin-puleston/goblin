package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
abstract class CoreHierarchy extends Hierarchy {

	CoreHierarchy(Model model, EntityId rootConceptId) {

		super(model, rootConceptId);
	}

	RootConcept createRootConcept(EntityId rootConceptId) {

		return new RootCoreConcept(this, rootConceptId);
	}
}
