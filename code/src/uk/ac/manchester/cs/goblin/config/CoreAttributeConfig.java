package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public abstract class CoreAttributeConfig extends LabelledConfigObject<CoreAttributeConfig> {

	private CoreHierarchyConfig sourceHierarchy;
	private DataField<EntityId> rootTargetConceptId;

	public String toString() {

		return getLabel()
				+ "("
				+ getRootSourceConceptId()
				+ " --> "
				+ getRootTargetConceptId()
				+ ")";
	}

	public void resetRootTargetConceptId(EntityId conceptId) {

		rootTargetConceptId.set(conceptId);
	}

	public CoreHierarchyConfig getSourceHierarchy() {

		return sourceHierarchy;
	}

	public EntityId getRootSourceConceptId() {

		return sourceHierarchy.getRootConceptId();
	}

	public EntityId getRootTargetConceptId() {

		return rootTargetConceptId.get();
	}

	CoreAttributeConfig(
		String label,
		CoreHierarchyConfig sourceHierarchy,
		EntityId rootTargetConceptId) {

		super(label);

		this.sourceHierarchy = sourceHierarchy;
		this.rootTargetConceptId = new DataField<EntityId>(rootTargetConceptId);
	}

	ConfigEditActions getEditActions() {

		return sourceHierarchy.getEditActions();
	}

	ConfigEditLocation createEditLocation() {

		return new ConfigEditLocation(this);
	}

	abstract ConstraintsOption getConstraintsOption();

	abstract void accept(CoreAttributeConfigVisitor visitor);
}
