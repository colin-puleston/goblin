package uk.ac.manchester.cs.goblin.io.attribute;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class AnchoredAttribute extends CoreAttribute {

	private EntityId anchorConceptId;

	private EntityId sourcePropertyId;
	private EntityId targetPropertyId;

	public AnchoredAttribute(
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

	public EntityId getAnchorConceptId() {

		return anchorConceptId;
	}

	public EntityId getSourcePropertyId() {

		return sourcePropertyId;
	}

	public EntityId getTargetPropertyId() {

		return targetPropertyId;
	}
}
