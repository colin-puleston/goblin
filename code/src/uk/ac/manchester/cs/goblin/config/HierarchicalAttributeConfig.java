package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class HierarchicalAttributeConfig extends CoreAttributeConfig {

	private static final String DEFAULT_LABEL_PREFIX = "type-of ";

	static private String createDefaultLabel(EntityId rootTargetConceptId) {

		return DEFAULT_LABEL_PREFIX + rootTargetConceptId.getLabel();
	}

	private HierarchicalLinksOption linksOption;

	public HierarchicalAttributeConfig(
				EntityId rootSourceConceptId,
				EntityId rootTargetConceptId,
				HierarchicalLinksOption linksOption) {

		super(
			createDefaultLabel(rootTargetConceptId),
			rootSourceConceptId,
			rootTargetConceptId);

		this.linksOption = linksOption;

		if (rootSourceConceptId.equals(rootTargetConceptId)) {

			throw new RuntimeException(
						"Cannot create hierarchical attribute \"" + getLabel() + "\""
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
