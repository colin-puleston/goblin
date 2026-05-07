package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class AnchoredAttributeConfig extends PropertyAttributeConfig {

	private DataField<EntityId> anchorConceptId;
	private DataField<EntityId> sourcePropertyId;
	private DataField<EntityId> targetPropertyId;

	public AnchoredAttributeConfig(
				EntityId anchorConceptId,
				EntityId sourcePropertyId,
				EntityId targetPropertyId,
				EntityId rootSourceConceptId,
				EntityId rootTargetConceptId,
				ConstraintsOption constraintsOption) {

		super(
			targetPropertyId.getLabel(),
			rootSourceConceptId,
			rootTargetConceptId,
			constraintsOption);

		this.anchorConceptId = new DataField<EntityId>(anchorConceptId);
		this.sourcePropertyId = new DataField<EntityId>(sourcePropertyId);
		this.targetPropertyId = new DataField<EntityId>(targetPropertyId);
	}

	public void resetAnchorConceptId(EntityId conceptId) {

		anchorConceptId.set(conceptId);
	}

	public void resetSourcePropertyId(EntityId propertyId) {

		sourcePropertyId.set(propertyId);
	}

	public void resetTargetPropertyId(EntityId propertyId) {

		targetPropertyId.set(propertyId);
	}

	public EntityId getAnchorConceptId() {

		return anchorConceptId.get();
	}

	public EntityId getSourcePropertyId() {

		return sourcePropertyId.get();
	}

	public EntityId getTargetPropertyId() {

		return targetPropertyId.get();
	}

	void accept(CoreAttributeConfigVisitor visitor) {

		visitor.visit(this);
	}
}
