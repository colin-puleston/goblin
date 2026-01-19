package uk.ac.manchester.cs.goblin.io;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class SimpleAttribute extends PropertyAttribute {

	SimpleAttribute(
		String label,
		EntityId linkingPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept) {

		super(label, linkingPropertyId, rootSourceConcept, rootTargetConcept);
	}

	Collection<EntityId> getInvolvedPropertyIds() {

		return Collections.singleton(getTargetPropertyId());
	}
}
