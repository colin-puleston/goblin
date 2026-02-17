package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ReferenceOnlyCoreHierarchy extends CoreHierarchy {

	public boolean referenceOnly() {

		return true;
	}

	ReferenceOnlyCoreHierarchy(Model model, EntityId rootConceptId) {

		super(model, rootConceptId);
	}
}
