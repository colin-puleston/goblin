package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ConceptTracking {

	private Map<Concept, ConceptTracker> trackers = new HashMap<Concept, ConceptTracker>();

	ConceptTracker toTracker(Concept concept) {

		ConceptTracker tracker = trackers.get(concept);

		if (tracker == null) {

			tracker = new ConceptTracker(concept);
			trackers.put(concept, tracker);
		}

		return tracker;
	}

	void updateForReplacement(Concept replaced, Concept replacement) {

		ConceptTracker tracker = trackers.remove(replaced);

		if (tracker != null) {

			tracker.replaceEntity(replacement);
			trackers.put(replacement, tracker);
		}
	}
}
