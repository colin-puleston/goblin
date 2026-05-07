package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public abstract class CoreAttributeConfig extends LabelledConfigObject<CoreAttributeConfig> {

	private DataField<EntityId> rootSourceConceptId;
	private DataField<EntityId> rootTargetConceptId;

	public String toString() {

		return getLabel() + "(" + rootSourceConceptId.get() + " --> " + rootTargetConceptId.get() + ")";
	}

	public void resetRootTargetConceptId(EntityId conceptId) {

		rootTargetConceptId.set(conceptId);
	}

	public EntityId getRootSourceConceptId() {

		return rootSourceConceptId.get();
	}

	public EntityId getRootTargetConceptId() {

		return rootTargetConceptId.get();
	}

	CoreAttributeConfig(String label, EntityId rootSourceConceptId, EntityId rootTargetConceptId) {

		super(label);

		this.rootSourceConceptId = new DataField<EntityId>(rootSourceConceptId);
		this.rootTargetConceptId = new DataField<EntityId>(rootTargetConceptId);
	}

	abstract ConstraintsOption getConstraintsOption();

	abstract void accept(CoreAttributeConfigVisitor visitor);
}
