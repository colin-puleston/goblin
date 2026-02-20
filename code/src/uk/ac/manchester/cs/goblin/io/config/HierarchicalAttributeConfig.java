package uk.ac.manchester.cs.goblin.io.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class HierarchicalAttributeConfig extends CoreAttributeConfig {

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

	public HierarchicalLinksOption getLinksOption() {

		return linksOption;
	}

	ConstraintsOption getConstraintsOption() {

		return linksOption.toConstraintsOption();
	}

	void accept(CoreAttributeConfigVisitor visitor) {

		visitor.visit(this);
	}
}
