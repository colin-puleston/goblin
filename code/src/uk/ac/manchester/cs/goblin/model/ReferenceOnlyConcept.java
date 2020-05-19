package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ReferenceOnlyConcept extends NonRootConcept {

	public boolean resetId(DynamicId newDynamicId) {

		throw createInvalidReferenceOnlyException();
	}

	public boolean move(Concept newParent) {

		throw createInvalidReferenceOnlyException();
	}

	public void remove() {

		throw createInvalidReferenceOnlyException();
	}

	public boolean addValidValuesConstraint(ConstraintType type, Collection<Concept> targetValues) {

		throw createInvalidReferenceOnlyException();
	}

	public boolean addImpliedValueConstraint(ConstraintType type, Concept targetValue) {

		throw createInvalidReferenceOnlyException();
	}

	ReferenceOnlyConcept(EntityId conceptId, Concept parent) {

		super(conceptId, parent);
	}

	void doRemoveConstraint(Constraint constraint) {

		throw createInvalidReferenceOnlyException();
	}

	private RuntimeException createInvalidReferenceOnlyException() {

		return new RuntimeException("Cannot perform operation on reference-only concept!");
	}
}
