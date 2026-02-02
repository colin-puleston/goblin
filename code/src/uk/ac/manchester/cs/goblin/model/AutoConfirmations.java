package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class AutoConfirmations implements Confirmations {

	public boolean confirmConceptMoveOrphanedConstraintRemovals(
						Concept concept,
						List<Constraint> removals) {

		return true;
	}

	public boolean confirmConceptMoveConflictingConstraintRemovals(
						Concept concept,
						List<Constraint> removals) {

		return true;
	}

	public boolean confirmConstraintAdditionConflictRemovals(List<Constraint> removals) {

		return true;
	}
}
