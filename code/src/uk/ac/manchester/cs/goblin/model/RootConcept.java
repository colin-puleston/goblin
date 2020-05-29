package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class RootConcept extends FixedConcept {

	public boolean addValidValuesConstraint(ConstraintType type, Collection<Concept> targetValues) {

		throw createInvalidOperationException();
	}

	public boolean addImpliedValueConstraint(ConstraintType type, Concept targetValue) {

		throw createInvalidOperationException();
	}

	public Set<Concept> getParents() {

		return Collections.emptySet();
	}

	public boolean descendantOf(Concept testAncestor) {

		return false;
	}

	RootConcept(Hierarchy hierarchy, EntityId rootConceptId) {

		super(hierarchy, rootConceptId);
	}

	void doRemoveConstraint(Constraint constraint) {

		throw createInvalidOperationException();
	}

	String getFixedConceptTypeDecriptor() {

		return "root";
	}
}
