package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class SimpleConstraintType extends PropertyConstraintType {

	SimpleConstraintType(
		String name,
		EntityId linkingPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept) {

		super(name, linkingPropertyId, rootSourceConcept, rootTargetConcept);
	}
}
