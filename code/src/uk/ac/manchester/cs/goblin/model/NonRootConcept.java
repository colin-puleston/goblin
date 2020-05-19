package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
abstract class NonRootConcept extends Concept {

	private ConceptTracker parent;

	public boolean isRoot() {

		return false;
	}

	public Concept getParent() {

		return parent.getEntity();
	}

	public Set<Concept> getParents() {

		return Collections.singleton(getParent());
	}

	public boolean descendantOf(Concept testAncestor) {

		return getParent().equals(testAncestor) || getParent().descendantOf(testAncestor);
	}

	public Constraint getClosestAncestorValidValuesConstraint(ConstraintType type) {

		return getParent().getClosestValidValuesConstraint(type);
	}

	NonRootConcept(EntityId conceptId, Concept parent) {

		super(parent.getHierarchy(), conceptId);

		this.parent = toConceptTracker(parent);
	}

	NonRootConcept(NonRootConcept replaced, Concept parent) {

		super(replaced);

		this.parent = toConceptTracker(parent);
	}

	NonRootConcept(NonRootConcept replaced, EntityId conceptId) {

		super(replaced, conceptId);

		parent = replaced.parent;
	}

	ConceptTracker setTemporaryParent(Concept tempParent) {

		ConceptTracker savedParent = parent;
		parent = toConceptTracker(tempParent);

		return savedParent;
	}

	void resetSavedParent(ConceptTracker savedParent) {

		parent = savedParent;
	}

	private ConceptTracker toConceptTracker(Concept concept) {

		return getModel().getConceptTracking().toTracker(concept);
	}
}
