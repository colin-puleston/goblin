package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class DynamicValueHierarchy extends Hierarchy {

	public void addCoreAttribute(Attribute attribute) {

		throw createNotDynamicValuesOpException();
	}

	public void setDynamicAttributeConstraints(ConstraintsOption constraintsOption) {

		throw createNotDynamicValuesOpException();
	}

	public void addListener(HierarchyListener listener) {

		throw createNotDynamicValuesOpException();
	}

	public void removeListener(HierarchyListener listener) {

		throw createNotDynamicValuesOpException();
	}

	public boolean potentiallyHasInwardAttributes() {

		return true;
	}

	public boolean fixedStructure() {

		return false;
	}

	public List<Attribute> getAllAttributes() {

		return Collections.emptyList();
	}

	public boolean hasCoreAttributes() {

		return false;
	}

	public List<Attribute> getCoreAttributes() {

		return Collections.emptyList();
	}

	public Attribute getCoreAttribute(String label) {

		throw createNotDynamicValuesOpException();
	}

	DynamicValueHierarchy(Model model, EntityId rootConceptId) {

		super(model, rootConceptId, rootConceptId.getLabel());
	}

	RootConcept createRootConcept(EntityId rootConceptId) {

		return new RootDynamicConcept(this, rootConceptId);
	}

	ConstraintsOption getDynamicAttributeConstraintsOption() {

		throw createNotDynamicValuesOpException();
	}

	void onAddedDynamicAttribute(DynamicAttribute attribute) {

		throw createNotDynamicValuesOpException();
	}

	void onRemovedDynamicAttribute(DynamicAttribute attribute) {

		throw createNotDynamicValuesOpException();
	}

	private RuntimeException createNotDynamicValuesOpException() {

		return new RuntimeException("Illegal operation on dynamic-value hierarchy: " + getLabel());
	}
}
