package uk.ac.manchester.cs.goblin.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class CoreHierarchyConfig extends LabelledConfigObject<CoreHierarchyConfig> {

	private ModelConfig model;

	private DataField<EntityId> rootConceptId;
	private DataField<Boolean> fixedStructure = new DataField<Boolean>(false);
	private DataField<ConstraintsOption> dynamicAttributeConstraintsOption
						= new DataField<ConstraintsOption>(ConstraintsOption.NONE);

	private DataArray<CoreAttributeConfig> coreAttributes = new DataArray<CoreAttributeConfig>();

	public void resetRootConceptId(EntityId conceptId) {

		rootConceptId.set(conceptId);
	}

	public void setFixedStructure(boolean fixed) {

		fixedStructure.set(fixed);
	}

	public void setDynamicAttributeConstraints(ConstraintsOption option) {

		dynamicAttributeConstraintsOption.set(option);
	}

	public void addCoreAttribute(CoreAttributeConfig attribute) {

		coreAttributes.add(attribute);
	}

	public void removeCoreAttribute(CoreAttributeConfig attribute) {

		coreAttributes.remove(attribute);
	}

	public void reorderCoreAttributes(List<CoreAttributeConfig> reorderedAttributes) {

		coreAttributes.reorder(reorderedAttributes);
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

	CoreHierarchyConfig(ModelConfig model, EntityId rootConceptId) {

		super(rootConceptId.getLabel());

		this.model = model;
		this.rootConceptId = new DataField<EntityId>(rootConceptId);
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
}
