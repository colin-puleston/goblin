package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ConceptTrackerSet extends EntityTrackerSet<Concept, ConceptTracker> {

	ConceptTrackerSet() {
	}

	ConceptTrackerSet(Collection<Concept> concepts) {

		super(concepts);
	}

	ConceptTrackerSet copy() {

		return new ConceptTrackerSet(this);
	}

	ConceptTracker toTracker(Concept concept) {

		return concept.toTracker();
	}

	private ConceptTrackerSet(ConceptTrackerSet template) {

		super(template);
	}
}
