package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ConceptTracking extends EntityTracking<Concept, ConceptTracker> {

	ConceptTracker createTracker(Concept concept) {

		return new ConceptTracker(concept);
	}
}
