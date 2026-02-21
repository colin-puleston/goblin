package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public abstract class CoreAttributeConfig {

	private String label;

	private EntityId rootSourceConceptId;
	private EntityId rootTargetConceptId;

	public String toString() {

		return getLabel() + "(" + rootSourceConceptId + " --> " + rootTargetConceptId + ")";
	}

	public String getLabel() {

		return label;
	}

	public EntityId getRootSourceConceptId() {

		return rootSourceConceptId;
	}

	public EntityId getRootTargetConceptId() {

		return rootTargetConceptId;
	}

	CoreAttributeConfig(String label, EntityId rootSourceConceptId, EntityId rootTargetConceptId) {

		this.label = label;
		this.rootSourceConceptId = rootSourceConceptId;
		this.rootTargetConceptId = rootTargetConceptId;
	}

	abstract ConstraintsOption getConstraintsOption();

	abstract void accept(CoreAttributeConfigVisitor visitor);
}
