package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class DynamicHierarchy extends Hierarchy {

	private List<ConstraintType> constraintTypes = new ArrayList<ConstraintType>();

	public void addConstraintType(ConstraintType type) {

		constraintTypes.add(type);
		type.getRootTargetConcept().getHierarchy().addInwardConstraintType(type);

		getRootConcept().addRootConstraint(type);
	}

	public boolean dynamicHierarchy() {

		return true;
	}

	public boolean hasConstraintTypes() {

		return !constraintTypes.isEmpty();
	}

	public List<ConstraintType> getConstraintTypes() {

		return new ArrayList<ConstraintType>(constraintTypes);
	}

	DynamicHierarchy(Model model, EntityId rootConceptId) {

		super(model, rootConceptId);
	}
}
