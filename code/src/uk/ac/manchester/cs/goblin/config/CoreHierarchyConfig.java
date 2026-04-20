package uk.ac.manchester.cs.goblin.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class CoreHierarchyConfig extends LabelledConfigEntity {

	private ModelConfig model;
	private EntityId rootConceptId;

	private boolean fixedStructure = false;
	private ConstraintsOption dynamicAttributeConstraintsOption = ConstraintsOption.NONE;

	private List<CoreAttributeConfig> coreAttributes = new ArrayList<CoreAttributeConfig>();

	public void resetLabel(String label) {

		super.resetLabel(label);

		model.onCoreHierarchyRelabelled(this);
	}

	public void resetRootConceptId(EntityId rootConceptId) {

		this.rootConceptId = rootConceptId;
	}

	public void setFixedStructure(boolean fixedStructure) {

		this.fixedStructure = fixedStructure;
	}

	public void setDynamicAttributeConstraints(ConstraintsOption option) {

		dynamicAttributeConstraintsOption = option;
	}

	public void addCoreAttribute(CoreAttributeConfig attribute) {

		coreAttributes.add(attribute);
	}

	public void removeCoreAttribute(CoreAttributeConfig attribute) {

		coreAttributes.remove(attribute);
	}

	public void reorderCoreAttributes(List<CoreAttributeConfig> newOrderedAttributes) {

		coreAttributes.clear();
		coreAttributes.addAll(newOrderedAttributes);
	}

	public EntityId getRootConceptId() {

		return rootConceptId;
	}

	public boolean fixedStructure() {

		return fixedStructure;
	}

	public ConstraintsOption getDynamicAttributeConstraintsOption() {

		return dynamicAttributeConstraintsOption;
	}

	public boolean hasCoreAttributes() {

		return !coreAttributes.isEmpty();
	}

	public List<CoreAttributeConfig> getCoreAttributes() {

		return new ArrayList<CoreAttributeConfig>(coreAttributes);
	}

	CoreHierarchyConfig(ModelConfig model, EntityId rootConceptId) {

		super(rootConceptId.getLabel());

		this.model = model;
		this.rootConceptId = rootConceptId;
	}

	CoreHierarchy createHierarchy(Model model) {

		CoreHierarchy hierarchy = new CoreHierarchy(model, rootConceptId, getLabel());

		hierarchy.setFixedStructure(fixedStructure);
		hierarchy.setDynamicAttributeConstraints(dynamicAttributeConstraintsOption);

		return hierarchy;
	}

	void addCoreAttributes(Hierarchy createdHierarchy) {

		Model createdModel = createdHierarchy.getModel();

		for (CoreAttributeConfig attribute : coreAttributes) {

			createdHierarchy.addCoreAttribute(new CoreAttribute(createdModel, attribute));
		}
	}
}
