package uk.ac.manchester.cs.goblin.io;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchicalAttribute extends CoreAttribute {

	HierarchicalAttribute(
		String label,
		Concept rootSourceConcept,
		Concept rootTargetConcept,
		HierarchicalLinksOption linksOption) {

		super(label, rootSourceConcept, rootTargetConcept, linksOption.toConstraintsOption());

		if (rootSourceConcept.equals(rootTargetConcept)) {

			throw new RuntimeException(
						"Cannot create hierarchical attribute \"" + label + "\""
						+ " with identical source and target hierarchies");
		}
	}
}
