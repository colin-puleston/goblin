package uk.ac.manchester.cs.goblin.io;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class AnchoredAttribute extends PropertyAttribute {

	private EntityId anchorConceptId;
	private EntityId sourcePropertyId;

	AnchoredAttribute(
		String label,
		EntityId anchorConceptId,
		EntityId sourcePropertyId,
		EntityId targetPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept) {

		super(label, targetPropertyId, rootSourceConcept, rootTargetConcept);

		this.anchorConceptId = anchorConceptId;
		this.sourcePropertyId = sourcePropertyId;
	}

	EntityId getAnchorConceptId() {

		return anchorConceptId;
	}

	EntityId getSourcePropertyId() {

		return sourcePropertyId;
	}

	Collection<EntityId> getInvolvedPropertyIds() {

		return Arrays.asList(getTargetPropertyId(), sourcePropertyId);
	}
}
