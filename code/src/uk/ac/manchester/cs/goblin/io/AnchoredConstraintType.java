package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class AnchoredConstraintType extends ConstraintType {

	private EntityId anchorConceptId;

	private EntityId sourcePropertyId;
	private EntityId targetPropertyId;

	AnchoredConstraintType(
		String name,
		EntityId anchorConceptId,
		EntityId sourcePropertyId,
		EntityId targetPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept) {

		super(name, rootSourceConcept, rootTargetConcept);

		this.anchorConceptId = anchorConceptId;
		this.sourcePropertyId = sourcePropertyId;
		this.targetPropertyId = targetPropertyId;
	}

	EntityId getAnchorConceptId() {

		return anchorConceptId;
	}

	EntityId getSourcePropertyId() {

		return sourcePropertyId;
	}

	EntityId getTargetPropertyId() {

		return targetPropertyId;
	}
}
