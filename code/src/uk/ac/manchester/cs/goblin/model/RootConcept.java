package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
abstract class RootConcept extends Concept {

	public boolean addValidValuesConstraint(ConstraintType type, Collection<Concept> targetValues) {

		throw createInvalidOperationException();
	}

	public boolean addImpliedValueConstraint(ConstraintType type, Concept targetValue) {

		throw createInvalidOperationException();
	}

	public List<Concept> getParents() {

		return Collections.emptyList();
	}

	public boolean descendantOf(Concept testAncestor) {

		return false;
	}

	public boolean canMove() {

		return false;
	}

	RootConcept(Hierarchy hierarchy, EntityId conceptId) {

		super(hierarchy, conceptId);
	}

	Concept createMovedReplacement(Concept newParent) {

		throw createInvalidOperationException();
	}

	void doRemoveConstraint(Constraint constraint) {

		throw createInvalidOperationException();
	}
}
