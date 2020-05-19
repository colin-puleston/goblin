package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class ReferenceOnlyHierarchy extends Hierarchy {

	public void addConstraintType(ConstraintType type) {

		throw new RuntimeException("Cannot add constraint-types to reference-only hierarchy!");
	}

	public boolean dynamicHierarchy() {

		return false;
	}

	public boolean hasConstraintTypes() {

		return false;
	}

	public List<ConstraintType> getConstraintTypes() {

		return Collections.emptyList();
	}

	ReferenceOnlyHierarchy(Model model, EntityId rootConceptId) {

		super(model, rootConceptId);
	}
}
