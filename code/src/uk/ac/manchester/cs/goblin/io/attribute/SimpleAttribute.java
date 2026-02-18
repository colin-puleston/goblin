package uk.ac.manchester.cs.goblin.io.attribute;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class SimpleAttribute extends CoreAttribute {

	private EntityId linkingPropertyId;

	public SimpleAttribute(
				String label,
				EntityId linkingPropertyId,
				Concept rootSourceConcept,
				Concept rootTargetConcept,
				ConstraintsOption constraintsOption) {

		super(label, rootSourceConcept, rootTargetConcept, constraintsOption);

		this.linkingPropertyId = linkingPropertyId;
	}

	public EntityId getLinkingPropertyId() {

		return linkingPropertyId;
	}
}
