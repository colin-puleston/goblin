package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class AnchoredConstraintType extends PropertyConstraintType {

	private EntityId anchorConceptId;
	private EntityId sourcePropertyId;

	AnchoredConstraintType(
		String name,
		EntityId anchorConceptId,
		EntityId sourcePropertyId,
		EntityId targetPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept) {

		super(name, targetPropertyId, rootSourceConcept, rootTargetConcept);

		this.anchorConceptId = anchorConceptId;
		this.sourcePropertyId = sourcePropertyId;
	}

	EntityId getAnchorConceptId() {

		return anchorConceptId;
	}

	EntityId getSourcePropertyId() {

		return sourcePropertyId;
	}
}
