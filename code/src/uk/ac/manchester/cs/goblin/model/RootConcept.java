package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class RootConcept extends Concept {

	public boolean resetId(DynamicId newDynamicId) {

		throw createInvalidRootOperationException();
	}

	public boolean move(Concept newParent) {

		throw createInvalidRootOperationException();
	}

	public void remove() {

		throw createInvalidRootOperationException();
	}

	public boolean addValidValuesConstraint(ConstraintType type, Collection<Concept> targetValues) {

		throw createInvalidRootOperationException();
	}

	public boolean addImpliedValueConstraint(ConstraintType type, Concept targetValue) {

		throw createInvalidRootOperationException();
	}

	public boolean isRoot() {

		return true;
	}

	public Concept getParent() {

		throw createInvalidRootOperationException();
	}

	public Set<Concept> getParents() {

		return Collections.emptySet();
	}

	public boolean descendantOf(Concept testAncestor) {

		return false;
	}

	public Constraint getClosestAncestorValidValuesConstraint(ConstraintType type) {

		throw createInvalidRootOperationException();
	}

	RootConcept(Hierarchy hierarchy, EntityId rootConceptId) {

		super(hierarchy, rootConceptId);
	}

	void doRemoveConstraint(Constraint constraint) {

		throw createInvalidRootOperationException();
	}

	private RuntimeException createInvalidRootOperationException() {

		return new RuntimeException("Cannot perform operation on root concept!");
	}
}
