package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class DynamicConcept extends NonRootConcept {

	public boolean resetId(DynamicId newDynamicId) {

		if (canResetId(newDynamicId)) {

			replace(new DynamicConcept(this, toEntityId(newDynamicId)));

			return true;
		}

		return false;
	}

	public boolean move(Concept newParent) {

		ConflictResolution conflictRes = checkMoveConflicts(newParent);

		if (conflictRes.resolvable()) {

			replace(new DynamicConcept(this, newParent), conflictRes);

			return true;
		}

		return false;
	}

	DynamicConcept(EntityId conceptId, Concept parent) {

		super(conceptId, parent);
	}

	private DynamicConcept(DynamicConcept replaced, Concept parent) {

		super(replaced, parent);
	}

	private DynamicConcept(DynamicConcept replaced, EntityId conceptId) {

		super(replaced, conceptId);
	}

	private boolean canResetId(DynamicId newDynamicId) {

		return getModel().canResetDynamicConceptId(this, newDynamicId);
	}

	private ConflictResolution checkMoveConflicts(Concept newParent) {

		ConceptTracker savedParent = setTemporaryParent(newParent);
		ConflictResolution conflicts = checkMovedConflicts();

		resetSavedParent(savedParent);

		return conflicts;
	}

	private ConflictResolution checkMovedConflicts() {

		return getModel().getConflictResolver().checkConceptMove(this);
	}

	private EntityId toEntityId(DynamicId newDynamicId) {

		return getModel().toEntityId(newDynamicId);
	}
}
