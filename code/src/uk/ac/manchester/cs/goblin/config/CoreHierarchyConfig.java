package uk.ac.manchester.cs.goblin.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class CoreHierarchyConfig extends LabelledConfigEntity {

	private EntityId rootConceptId;

	private boolean fixedStructure = false;
	private ConstraintsOption dynamicAttributeConstraintsOption = ConstraintsOption.NONE;

	private List<CoreAttributeConfig> coreAttributes = new ArrayList<CoreAttributeConfig>();

	public CoreHierarchyConfig(EntityId rootConceptId) {

		super(rootConceptId.getLabel());

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

	public void replaceCoreAttribute(
					CoreAttributeConfig oldAttribute,
					CoreAttributeConfig newAttribute) {

		int index = coreAttributes.indexOf(oldAttribute);

		if (index == -1) {

			throw new RuntimeException("Attribute not currently present");
		}

		coreAttributes.remove(oldAttribute);
		coreAttributes.add(index, newAttribute);
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

		CoreHierarchy hierarchy = new CoreHierarchy(model, rootConceptId, getLabel());

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
