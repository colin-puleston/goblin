package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public interface Confirmations {

	public boolean confirmConceptMoveOrphanedConstraintRemovals(
						Concept concept,
						List<Constraint> removals);

	public boolean confirmConceptMoveConflictingConstraintRemovals(
						Concept concept,
						List<Constraint> removals);

	public boolean confirmConstraintAdditionConflictRemovals(List<Constraint> removals);
}
