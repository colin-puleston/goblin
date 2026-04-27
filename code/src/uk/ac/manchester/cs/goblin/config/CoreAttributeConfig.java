package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public abstract class CoreAttributeConfig extends LabelledConfigObject {

	private EntityId rootSourceConceptId;
	private EntityId rootTargetConceptId;

	public String toString() {

		return getLabel() + "(" + rootSourceConceptId + " --> " + rootTargetConceptId + ")";
	}

	public void resetRootTargetConceptId(EntityId rootTargetConceptId) {

		this.rootTargetConceptId = rootTargetConceptId;
	}

	public EntityId getRootSourceConceptId() {

		return rootSourceConceptId;
	}

	public EntityId getRootTargetConceptId() {

		return rootTargetConceptId;
	}

	CoreAttributeConfig(String label, EntityId rootSourceConceptId, EntityId rootTargetConceptId) {

		super(label);

		this.rootSourceConceptId = rootSourceConceptId;
		this.rootTargetConceptId = rootTargetConceptId;
	}

	abstract ConstraintsOption getConstraintsOption();

	abstract void accept(CoreAttributeConfigVisitor visitor);
}
