package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class SimpleAttributeConfig extends PropertyAttributeConfig {

	private EntityId linkingPropertyId;

	public SimpleAttributeConfig(
				EntityId linkingPropertyId,
				EntityId rootSourceConceptId,
				EntityId rootTargetConceptId,
				ConstraintsOption constraintsOption) {

		super(
			linkingPropertyId.getLabel(),
			rootSourceConceptId,
			rootTargetConceptId,
			constraintsOption);

		this.linkingPropertyId = linkingPropertyId;
	}

	public void resetLinkingPropertyId(EntityId linkingPropertyId) {

		this.linkingPropertyId = linkingPropertyId;
	}

	public EntityId getLinkingPropertyId() {

		return linkingPropertyId;
	}

	void accept(CoreAttributeConfigVisitor visitor) {

		visitor.visit(this);
	}
}
