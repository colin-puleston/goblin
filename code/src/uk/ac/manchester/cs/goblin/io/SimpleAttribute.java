package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class SimpleAttribute extends CorePropertyAttribute {

	private EntityId linkingPropertyId;

	SimpleAttribute(
		String label,
		EntityId linkingPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept) {

		super(label, rootSourceConcept, rootTargetConcept);

		this.linkingPropertyId = linkingPropertyId;
	}

	EntityId getLinkingPropertyId() {

		return linkingPropertyId;
	}
}
