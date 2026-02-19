package uk.ac.manchester.cs.goblin.io.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class HierarchyConfig {

	private EntityId rootConceptId;
	private String label;

	private boolean fixedStructure = false;
	private ConstraintsOption dynamicAttributeConstraintsOption = ConstraintsOption.NONE;

	private List<AttributeConfig> coreAttributes = new ArrayList<AttributeConfig>();

	public HierarchyConfig(EntityId rootConceptId) {

		this.rootConceptId = rootConceptId;

		label = rootConceptId.getLabel();
	}

	public void setLabel(String label) {

		this.label = label;
	}

	public void setFixedStructure(boolean fixedStructure) {

		this.fixedStructure = fixedStructure;
	}

	public void setDynamicAttributeConstraints(ConstraintsOption option) {

		dynamicAttributeConstraintsOption = option;
	}

	public String getLabel() {

		return label;
	}

	public boolean fixedStructure() {

		return fixedStructure;
	}

	public void addCoreAttribute(AttributeConfig attribute) {

		coreAttributes.add(attribute);
	}

	public boolean hasCoreAttributes() {

		return !coreAttributes.isEmpty();
	}

	public EntityId getRootConceptId() {

		return rootConceptId;
	}

	public List<AttributeConfig> getCoreAttributes() {

		return new ArrayList<AttributeConfig>(coreAttributes);
	}

	CoreHierarchy createHierarchy(Model model) {

		CoreHierarchy hierarchy = new CoreHierarchy(model, rootConceptId, label);

		hierarchy.setFixedStructure(fixedStructure);
		hierarchy.setDynamicAttributeConstraints(dynamicAttributeConstraintsOption);

		return hierarchy;
	}

	void addCoreAttributes(Hierarchy createdHierarchy) {

		Model model = createdHierarchy.getModel();

		for (AttributeConfig attribute : coreAttributes) {

			createdHierarchy.addCoreAttribute(attribute.createAttribute(model));
		}
	}
}
