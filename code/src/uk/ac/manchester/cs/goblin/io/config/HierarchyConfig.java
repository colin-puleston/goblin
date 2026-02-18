package uk.ac.manchester.cs.goblin.io.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class HierarchyConfig {

	private String label;
	private EntityId rootConceptId;

	private boolean fixedStructure;

	private ConstraintsOption dynamicAttributesConstraintsOption = null;

	private List<AttributeConfig> coreAttributes = new ArrayList<AttributeConfig>();

	public HierarchyConfig(EntityId rootConceptId, boolean fixedStructure) {

		this.rootConceptId = rootConceptId;
		this.fixedStructure = fixedStructure;

		label = rootConceptId.getLabel();
	}

	public void setLabel(String label) {

		this.label = label;
	}

	public void enableDynamicAttributes(ConstraintsOption constraintsOption) {

		dynamicAttributesConstraintsOption = constraintsOption;
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

		CoreHierarchy hierarchy = new CoreHierarchy(model, rootConceptId, label, fixedStructure);

		if (dynamicAttributesConstraintsOption != null) {

			hierarchy.enableDynamicAttributes(dynamicAttributesConstraintsOption);
		}

		return hierarchy;
	}

	void addCoreAttributes(Hierarchy createdHierarchy) {

		Model model = createdHierarchy.getModel();

		for (AttributeConfig attribute : coreAttributes) {

			createdHierarchy.addCoreAttribute(attribute.createAttribute(model));
		}
	}
}
