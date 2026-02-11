package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class AnchoredAttribute extends CoreAttribute {

	private EntityId anchorConceptId;

	private EntityId sourcePropertyId;
	private EntityId targetPropertyId;

	AnchoredAttribute(
		String label,
		EntityId anchorConceptId,
		EntityId sourcePropertyId,
		EntityId targetPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept,
		ConstraintsOption constraintsOption) {

		super(label, rootSourceConcept, rootTargetConcept, constraintsOption);

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
