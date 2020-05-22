package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ConceptTracker extends EntityTracker<Concept> {

	ConceptTracker(Concept concept) {

		super(concept);
	}

	ConceptTracker(ConceptTracker template) {

		super(template);
	}

	ConceptTracker copy() {

		return new ConceptTracker(this);
	}
}
