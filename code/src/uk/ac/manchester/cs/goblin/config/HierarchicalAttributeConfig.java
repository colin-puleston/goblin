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

	private DataField<HierarchicalLinksOption> linksOption;

	public HierarchicalAttributeConfig(
				EntityId rootSourceConceptId,
				EntityId rootTargetConceptId,
				HierarchicalLinksOption linksOption) {

		super(
			createDefaultLabel(rootTargetConceptId),
			rootSourceConceptId,
			rootTargetConceptId);

		checkValidRootConceptCombo();

		this.linksOption = new DataField<HierarchicalLinksOption>(linksOption);
	}

	public void resetRootTargetConceptId(EntityId rootTargetConceptId) {

		super.resetRootTargetConceptId(rootTargetConceptId);

		checkValidRootConceptCombo();
	}

	public void resetLinksOption(HierarchicalLinksOption option) {

		linksOption.set(option);
	}

	public HierarchicalLinksOption getLinksOption() {

		return linksOption.get();
	}

	ConstraintsOption getConstraintsOption() {

		return linksOption.get().toConstraintsOption();
	}

	void accept(CoreAttributeConfigVisitor visitor) {

		visitor.visit(this);
	}

	private void checkValidRootConceptCombo() {

		if (getRootSourceConceptId().equals(getRootTargetConceptId())) {

			throw new RuntimeException(
						"Cannot create hierarchical attribute \"" + getLabel() + "\""
						+ " with identical source and target hierarchies");
		}
	}
}
