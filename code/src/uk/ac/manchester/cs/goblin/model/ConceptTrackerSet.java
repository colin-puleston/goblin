package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ConceptTrackerSet extends EntityTrackerSet<Concept> {

	ConceptTrackerSet(Model model) {

		super(model.getConceptTracking());
	}

	ConceptTrackerSet(Model model, Collection<Concept> concepts) {

		super(model.getConceptTracking(), concepts);
	}

	ConceptTrackerSet copy() {

		return new ConceptTrackerSet(this);
	}

	private ConceptTrackerSet(ConceptTrackerSet template) {

		super(template);
	}
}
