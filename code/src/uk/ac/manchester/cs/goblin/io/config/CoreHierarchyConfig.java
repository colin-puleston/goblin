package uk.ac.manchester.cs.goblin.io.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class CoreHierarchyConfig {

	private String label;
	private EntityId rootConceptId;

	private boolean fixedStructure = false;
	private ConstraintsOption dynamicAttributeConstraintsOption = ConstraintsOption.NONE;

	private List<CoreAttributeConfig> coreAttributes = new ArrayList<CoreAttributeConfig>();

	public CoreHierarchyConfig(EntityId rootConceptId) {

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

	public void addCoreAttribute(CoreAttributeConfig attribute) {

		coreAttributes.add(attribute);
	}

	public String getLabel() {

		return label;
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

	CoreHierarchy createHierarchy(Model model) {

		CoreHierarchy hierarchy = new CoreHierarchy(model, rootConceptId, label);

		hierarchy.setFixedStructure(fixedStructure);
		hierarchy.setDynamicAttributeConstraints(dynamicAttributeConstraintsOption);

		return hierarchy;
	}

	void addCoreAttributes(Hierarchy createdHierarchy) {

		Model model = createdHierarchy.getModel();

		for (CoreAttributeConfig attribute : coreAttributes) {

			createdHierarchy.addCoreAttribute(new CoreAttribute(model, attribute));
		}
	}
}
