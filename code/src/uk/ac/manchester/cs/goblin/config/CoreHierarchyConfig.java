package uk.ac.manchester.cs.goblin.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.edit.*;
import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class CoreHierarchyConfig extends LabelledConfigObject<CoreHierarchyConfig> {

	private DataField<ModelSectionConfig> section;

	private DataField<EntityId> rootConceptId;
	private DataField<Boolean> fixedStructure;
	private DataField<ConstraintsOption> dynamicAttributeConstraintsOption;

	private DataArray<CoreAttributeConfig> coreAttributes = new DataArray<CoreAttributeConfig>();

	public void resetRootConceptId(EntityId conceptId) {

		rootConceptId.set(conceptId);
	}

	public void resetFixedStructure(boolean fixed) {

		fixedStructure.set(fixed);
	}

	public void resetDynamicAttributeConstraintsOption(ConstraintsOption option) {

		dynamicAttributeConstraintsOption.set(option);
	}

	public SimpleAttributeConfig addSimpleAttribute(
									EntityId linkingPropertyId,
									EntityId rootTargetConceptId,
									ConstraintsOption constraintsOption) {

		return addCoreAttribute(
					new SimpleAttributeConfig(
							this,
							linkingPropertyId,
							rootTargetConceptId,
							constraintsOption));
	}

	public AnchoredAttributeConfig addAnchoredAttribute(
										EntityId anchorConceptId,
										EntityId sourcePropertyId,
										EntityId targetPropertyId,
										EntityId rootTargetConceptId,
										ConstraintsOption constraintsOption) {

		return addCoreAttribute(
					new AnchoredAttributeConfig(
							this,
							anchorConceptId,
							sourcePropertyId,
							targetPropertyId,
							rootTargetConceptId,
							constraintsOption));
	}

	public HierarchicalAttributeConfig addHierarchicalAttribute(
											EntityId rootTargetConceptId,
											HierarchicalLinksOption linksOption) {

		return addCoreAttribute(
					new HierarchicalAttributeConfig(
							this,
							rootTargetConceptId,
							linksOption));
	}

	public void removeCoreAttribute(CoreAttributeConfig attribute) {

		coreAttributes.remove(attribute);
	}

	public void reorderCoreAttributes(List<CoreAttributeConfig> reorderedAttributes) {

		coreAttributes.reorder(reorderedAttributes);
	}

	public ModelSectionConfig getSection() {

		return section.get();
	}

	public EntityId getRootConceptId() {

		return rootConceptId.get();
	}

	public boolean fixedStructure() {

		return fixedStructure.get();
	}

	public ConstraintsOption getDynamicAttributeConstraintsOption() {

		return dynamicAttributeConstraintsOption.get();
	}

	public boolean hasCoreAttributes() {

		return !coreAttributes.isEmpty();
	}

	public boolean hasCoreAttribute(CoreAttributeConfig attribute) {

		return coreAttributes.contains(attribute);
	}

	public List<CoreAttributeConfig> getCoreAttributes() {

		return coreAttributes.copy();
	}

	CoreHierarchyConfig(
		ModelSectionConfig section,
		EntityId rootConceptId,
		boolean fixedStructure,
		ConstraintsOption dynamicAttributeConstraintsOption) {

		super(rootConceptId.getLabel());

		this.section = new DataField<ModelSectionConfig>(section);
		this.rootConceptId = new DataField<EntityId>(rootConceptId);
		this.fixedStructure = new DataField<Boolean>(false);
		this.dynamicAttributeConstraintsOption
				= new DataField<ConstraintsOption>(dynamicAttributeConstraintsOption);
	}

	CoreHierarchy createHierarchy(Model model) {

		CoreHierarchy hierarchy = new CoreHierarchy(model, rootConceptId.get(), getLabel());

		hierarchy.setFixedStructure(fixedStructure.get());
		hierarchy.setDynamicAttributeConstraints(dynamicAttributeConstraintsOption.get());

		return hierarchy;
	}

	void addCoreAttributes(Hierarchy createdHierarchy) {

		Model createdModel = createdHierarchy.getModel();

		for (CoreAttributeConfig attribute : coreAttributes.get()) {

			createdHierarchy.addCoreAttribute(new CoreAttribute(createdModel, attribute));
		}
	}

	void includeSectionSetAction(CompoundEditAction compoundAction, ModelSectionConfig newSection) {

		section.includeSetAction(compoundAction, newSection);
	}

	ConfigEditActions getEditActions() {

		return section.get().getEditActions();
	}

	private <A extends CoreAttributeConfig>A addCoreAttribute(A attribute) {

		coreAttributes.add(attribute);

		return attribute;
	}
}
