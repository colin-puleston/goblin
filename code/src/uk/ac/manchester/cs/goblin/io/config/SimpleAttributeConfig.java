package uk.ac.manchester.cs.goblin.io.config;

import uk.ac.manchester.cs.goblin.model.*;

import uk.ac.manchester.cs.goblin.io.attribute.*;

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

	EntityId getLinkingPropertyId() {

		return linkingPropertyId;
	}

	Attribute createAttribute(
				String label,
				Concept rootSourceConcept,
				Concept rootTargetConcept) {

		return new SimpleAttribute(
						label,
						linkingPropertyId,
						rootSourceConcept,
						rootTargetConcept,
						getConstraintsOption());
	}
}
