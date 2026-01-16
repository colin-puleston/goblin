package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class EditableCoreHierarchy extends CoreHierarchy {

	private boolean dynamicConstraintsEnabled = true;

	private List<ConstraintType> coreConstraintTypes = new ArrayList<ConstraintType>();

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

	public void addCoreConstraintType(ConstraintType type) {

		coreConstraintTypes.add(type);

		type.getRootTargetConcept().getHierarchy().addInwardCoreConstraintType(type);

		type.getRootSourceConcept().addConstraint(type.createRootConstraint());
	}

	public boolean dynamicConstraintsEnabled() {

		return dynamicConstraintsEnabled;
	}

	public boolean hasCoreConstraintTypes() {

		return !coreConstraintTypes.isEmpty();
	}

	public List<ConstraintType> getAllConstraintTypes() {

		List<ConstraintType> types = getCoreConstraintTypes();

		types.addAll(getRootConcept().getDynamicConstraintTypesDownwards());

		return types;
	}

	public List<ConstraintType> getCoreConstraintTypes() {

		return new ArrayList<ConstraintType>(coreConstraintTypes);
	}

	EditableCoreHierarchy(Model model, EntityId rootConceptId) {

		super(model, rootConceptId);
	}

	void onAddedDynamicConstraintType(DynamicConstraintType type) {

		for (HierarchyListener listener : copyListeners()) {

			listener.onAddedDynamicConstraintType(type);
		}
	}

	void onRemovedDynamicConstraintType(DynamicConstraintType type) {

		for (HierarchyListener listener : copyListeners()) {

			listener.onRemovedDynamicConstraintType(type);
		}
	}

	private List<HierarchyListener> copyListeners() {

		return new ArrayList<HierarchyListener>(listeners);
	}
}
