package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class DynamicValueHierarchy extends Hierarchy {

	public void addCoreAttribute(Attribute attribute) {

		throw createNotDynamicValuesOpException();
	}

	public void enableDynamicAttributes(ConstraintsOption constraintsOption) {

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

	DynamicValueHierarchy(Model model, EntityId rootConceptId) {

		super(model, rootConceptId);
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

		return new RuntimeException("Illegal operation on dynamic-value hierachy: " + getLabel());
	}
}
