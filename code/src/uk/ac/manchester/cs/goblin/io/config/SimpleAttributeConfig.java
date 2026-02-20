package uk.ac.manchester.cs.goblin.io.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class SimpleAttributeConfig extends PropertyAttributeConfig {

	private EntityId linkingPropertyId;

	public SimpleAttributeConfig(
				String label,
				EntityId linkingPropertyId,
				EntityId rootSourceConceptId,
				EntityId rootTargetConceptId,
				ConstraintsOption constraintsOption) {

		super(label, rootSourceConceptId, rootTargetConceptId, constraintsOption);

		this.linkingPropertyId = linkingPropertyId;
	}

	public EntityId getLinkingPropertyId() {

		return linkingPropertyId;
	}

	void accept(CoreAttributeConfigVisitor visitor) {

		visitor.visit(this);
	}
}
