package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class EditableCoreHierarchy extends CoreHierarchy {

	private ConstraintsOption dynamicAttributesConstraintsOption = null;
	private List<Attribute> coreAttributes = new ArrayList<Attribute>();

	private List<HierarchyListener> listeners = new ArrayList<HierarchyListener>();

	public void addListener(HierarchyListener listener) {

		listeners.add(listener);
	}

	public void removeListener(HierarchyListener listener) {

		listeners.remove(listener);
	}

	public void enableDynamicAttributes(ConstraintsOption constraintsOption) {

		dynamicAttributesConstraintsOption = constraintsOption;
	}

	public void addCoreAttribute(Attribute attribute) {

		coreAttributes.add(attribute);

		attribute.getRootTargetConcept().getHierarchy().addInwardCoreAttribute(attribute);

		attribute.getRootSourceConcept().addConstraint(attribute.getRootConstraint());
	}

	public List<Attribute> getAllAttributes() {

		List<Attribute> attributes = getCoreAttributes();

		attributes.addAll(getDynamicAttributes());

		return attributes;
	}

	public boolean hasCoreAttributes() {

		return !coreAttributes.isEmpty();
	}

	public List<Attribute> getCoreAttributes() {

		return new ArrayList<Attribute>(coreAttributes);
	}

	public boolean dynamicAttributesEnabled() {

		return dynamicAttributesConstraintsOption != null;
	}

	public boolean hasDynamicAttributes() {

		return !getDynamicAttributes().isEmpty();
	}

	public List<DynamicAttribute> getDynamicAttributes() {

		if (dynamicAttributesEnabled()) {

			return getRootConcept().getDynamicAttributesDownwards();
		}

		return Collections.emptyList();
	}

	EditableCoreHierarchy(Model model, EntityId rootConceptId) {

		super(model, rootConceptId);
	}

	ConstraintsOption getDynamicAttributeConstraintsOption() {

		if (dynamicAttributesEnabled()) {

			return dynamicAttributesConstraintsOption;
		}

		throw new Error("Unexpected method invocation!");
	}

	void onAddedDynamicAttribute(DynamicAttribute attribute) {

		for (HierarchyListener listener : copyListeners()) {

			listener.onAddedDynamicAttribute(attribute);
		}
	}

	void onRemovedDynamicAttribute(DynamicAttribute attribute) {

		for (HierarchyListener listener : copyListeners()) {

			listener.onRemovedDynamicAttribute(attribute);
		}
	}

	private List<HierarchyListener> copyListeners() {

		return new ArrayList<HierarchyListener>(listeners);
	}
}
