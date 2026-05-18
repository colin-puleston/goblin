package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class SimpleAttributeConfig extends PropertyAttributeConfig {

	private DataField<EntityId> linkingPropertyId;

	public void resetLinkingPropertyId(EntityId linkingPropertyId) {

		this.linkingPropertyId.set(linkingPropertyId);
	}

	public EntityId getLinkingPropertyId() {

		return linkingPropertyId.get();
	}

	SimpleAttributeConfig(
		CoreHierarchyConfig sourceHierarchy,
		EntityId linkingPropertyId,
		EntityId rootTargetConceptId,
		ConstraintsOption constraintsOption) {

		super(
			linkingPropertyId.getLabel(),
			sourceHierarchy,
			rootTargetConceptId,
			constraintsOption);

		this.linkingPropertyId = new DataField<EntityId>(linkingPropertyId);
	}

	void accept(CoreAttributeConfigVisitor visitor) {

		visitor.visit(this);
	}
}
