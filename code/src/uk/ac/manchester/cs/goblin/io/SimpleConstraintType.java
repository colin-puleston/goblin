package uk.ac.manchester.cs.goblin.io;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class SimpleConstraintType extends CorePropertyConstraintType {

	SimpleConstraintType(
		String name,
		EntityId linkingPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept) {

		super(name, linkingPropertyId, rootSourceConcept, rootTargetConcept);
	}

	Collection<EntityId> getInvolvedPropertyIds() {

		return Collections.singleton(getTargetPropertyId());
	}
}
