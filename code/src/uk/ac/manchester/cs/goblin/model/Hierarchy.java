package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class Hierarchy {

	private Model model;
	private String label;

	private RootConcept rootConcept;

	private List<Attribute> inwardCoreAttributes = new ArrayList<Attribute>();

	public void setLabel(String label) {

		this.label = label;
	}

	public abstract void setDynamicAttributeConstraints(ConstraintsOption constraintsOption);

	public abstract void addCoreAttribute(Attribute attribute);

	public abstract void addListener(HierarchyListener listener);

	public abstract void removeListener(HierarchyListener listener);

	public Model getModel() {

		return model;
	}

	public String getLabel() {

		return label;
	}

	public abstract boolean fixedStructure();

	public Concept getRootConcept() {

		return rootConcept;
	}

	public boolean hasRootConcept(EntityId conceptId) {

		return rootConcept.getConceptId().equals(conceptId);
	}

	public boolean containsConcept(EntityId conceptId) {

		return lookForConcept(conceptId) != null;
	}

	public Concept getConcept(EntityId conceptId) {

		Concept concept = lookForConcept(conceptId);

		if (concept == null) {

			throw new RuntimeException("Cannot find concept: " + conceptId);
		}

		return concept;
	}

	public Concept lookForConcept(EntityId conceptId) {

		return rootConcept.lookForConceptDownwards(conceptId);
	}

	public boolean potentiallyHasAttributes() {

		return hasCoreAttributes() || dynamicAttributesEnabled();
	}

	public abstract List<Attribute> getAllAttributes();

	public abstract boolean hasCoreAttributes();

	public abstract List<Attribute> getCoreAttributes();

	public abstract Attribute getCoreAttribute(String label);

	public boolean potentiallyHasInwardAttributes() {

		return hasInwardCoreAttributes();
	}

	public boolean hasInwardCoreAttributes() {

		return !inwardCoreAttributes.isEmpty();
	}

	public List<Attribute> getInwardCoreAttributes() {

		return new ArrayList<Attribute>(inwardCoreAttributes);
	}

	public boolean dynamicAttributesEnabled() {

		return false;
	}

	public boolean hasDynamicAttributes() {

		return false;
	}

	public List<DynamicAttribute> getDynamicAttributes() {

		return Collections.emptyList();
	}

	Hierarchy(Model model, EntityId rootConceptId, String label) {

		this.model = model;
		this.label = label;

		rootConcept = createRootConcept(rootConceptId);
	}

	abstract RootConcept createRootConcept(EntityId rootConceptId);

	void addInwardCoreAttribute(Attribute attribute) {

		inwardCoreAttributes.add(attribute);
	}

	abstract ConstraintsOption getDynamicAttributeConstraintsOption();

	abstract void onAddedDynamicAttribute(DynamicAttribute attribute);

	abstract void onRemovedDynamicAttribute(DynamicAttribute attribute);
}
