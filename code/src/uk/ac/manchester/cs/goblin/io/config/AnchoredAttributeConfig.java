package uk.ac.manchester.cs.goblin.io.config;

import uk.ac.manchester.cs.goblin.model.*;

import uk.ac.manchester.cs.goblin.io.attribute.*;

/**
 * @author Colin Puleston
 */
public class AnchoredAttributeConfig extends PropertyAttributeConfig {

	private EntityId anchorConceptId;

	private EntityId sourcePropertyId;
	private EntityId targetPropertyId;

	public AnchoredAttributeConfig(
				String label,
				EntityId anchorConceptId,
				EntityId sourcePropertyId,
				EntityId targetPropertyId,
				EntityId rootSourceConceptId,
				EntityId rootTargetConceptId,
				ConstraintsOption constraintsOption) {

		super(label, rootSourceConceptId, rootTargetConceptId, constraintsOption);

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

	Attribute createAttribute(
				String label,
				Concept rootSourceConcept,
				Concept rootTargetConcept) {

		return new AnchoredAttribute(
						label,
						anchorConceptId,
						sourcePropertyId,
						targetPropertyId,
						rootSourceConcept,
						rootTargetConcept,
						getConstraintsOption());
	}
}
