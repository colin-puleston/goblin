package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class EditableCoreHierarchy extends CoreHierarchy {

	private boolean dynamicConstraintsEnabled = true;

	private List<Attribute> coreAttributes = new ArrayList<Attribute>();

	private List<HierarchyListener> listeners = new ArrayList<HierarchyListener>();

	public void addListener(HierarchyListener listener) {

		listeners.add(listener);
	}

	public void removeListener(HierarchyListener listener) {

		listeners.remove(listener);
	}

	public void setDynamicConstraintsEnabled(boolean enabled) {

		dynamicConstraintsEnabled = enabled;
	}

	public void addCoreAttribute(Attribute attribute) {

		coreAttributes.add(attribute);

		attribute.getRootTargetConcept().getHierarchy().addInwardCoreAttribute(attribute);

		attribute.getRootSourceConcept().addConstraint(attribute.createRootConstraint());
	}

	public boolean dynamicConstraintsEnabled() {

		return dynamicConstraintsEnabled;
	}

	public boolean hasCoreAttributes() {

		return !coreAttributes.isEmpty();
	}

	public List<Attribute> getAllAttributes() {

		List<Attribute> attributes = getCoreAttributes();

		attributes.addAll(getRootConcept().getDynamicAttributesDownwards());

		return attributes;
	}

	public List<Attribute> getCoreAttributes() {

		return new ArrayList<Attribute>(coreAttributes);
	}

	EditableCoreHierarchy(Model model, EntityId rootConceptId) {

		super(model, rootConceptId);
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
