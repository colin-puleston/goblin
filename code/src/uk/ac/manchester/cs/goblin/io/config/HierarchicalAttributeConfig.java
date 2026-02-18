package uk.ac.manchester.cs.goblin.io.config;

import uk.ac.manchester.cs.goblin.model.*;

import uk.ac.manchester.cs.goblin.io.attribute.*;

/**
 * @author Colin Puleston
 */
public class HierarchicalAttributeConfig extends AttributeConfig {

	private HierarchicalLinksOption linksOption;

	public HierarchicalAttributeConfig(
				String label,
				EntityId rootSourceConceptId,
				EntityId rootTargetConceptId,
				HierarchicalLinksOption linksOption) {

		super(label, rootSourceConceptId, rootTargetConceptId);

		this.linksOption = linksOption;

		if (rootSourceConceptId.equals(rootTargetConceptId)) {

			throw new RuntimeException(
						"Cannot create hierarchical attribute \"" + label + "\""
						+ " with identical source and target hierarchies");
		}
	}

	Attribute createAttribute(
				String label,
				Concept rootSourceConcept,
				Concept rootTargetConcept) {

		return new HierarchicalAttribute(label, rootSourceConcept, rootTargetConcept, linksOption);
	}
}
