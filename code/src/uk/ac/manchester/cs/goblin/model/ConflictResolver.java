package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ConflictResolver {

	private Confirmations confirmations = new AutoConfirmations();

	private abstract class ConflictFinder {

		final Constraint subject;

		private ConstraintType type;
		private List<Constraint> conflicts = new ArrayList<Constraint>();

		ConflictFinder(Constraint subject) {

			this.subject = subject;

			type = subject.getType();
		}

		boolean any() {

			return !findAll().isEmpty();
		}

		List<Constraint> findAll() {

			findFromLinkedConcepts(subject.getSourceValue());

			return conflicts;
		}

		abstract Set<Concept> getLinkedConcepts(Concept current);

		abstract Constraint getAncestorConstraint(Constraint candidate);

		abstract Constraint getDescendantConstraint(Constraint candidate);

		private void findFromLinkedConcepts(Concept current) {

			for (Concept child : getLinkedConcepts(current)) {

				findFrom(child);
			}
		}

		private void findFrom(Concept current) {

			for (Constraint candidate : current.getConstraints(type)) {

				if (conflicts(candidate)) {

					conflicts.add(candidate);
				}
			}

			findFromLinkedConcepts(current);
		}

		private boolean conflicts(Constraint candidate) {

			Constraint anc = getAncestorConstraint(candidate);
			Constraint dec = getDescendantConstraint(candidate);

			if (anc.getSemantics().impliedValue()) {

				if (dec.getSemantics().impliedValue()) {

					if (type.singleValue()) {

						return !dec.getTargetValue().descendantOf(anc.getTargetValue());
					}

					return anc.getTargetValue().subsumedBy(dec.getTargetValue());
				}

				return false;
			}

			return !Concept.allSubsumed(anc.getTargetValues(), dec.getTargetValues());
		}
	}

	private class UpwardsConflictFinder extends ConflictFinder {

		UpwardsConflictFinder(Constraint constraint) {

			super(constraint);
		}

		Set<Concept> getLinkedConcepts(Concept current) {

			return current.getParents();
		}

		Constraint getAncestorConstraint(Constraint candidate) {

			return candidate;
		}

		Constraint getDescendantConstraint(Constraint candidate) {

			return subject;
		}
	}

	private class DownwardsConflictFinder extends ConflictFinder {

		DownwardsConflictFinder(Constraint constraint) {

			super(constraint);
		}

		Set<Concept> getLinkedConcepts(Concept current) {

			return current.getChildren();
		}

		Constraint getAncestorConstraint(Constraint candidate) {

			return subject;
		}

		Constraint getDescendantConstraint(Constraint candidate) {

			return candidate;
		}
	}

	private class ConceptMoveConflictsFinder {

		final List<Constraint> conflicts = new ArrayList<Constraint>();

		ConceptMoveConflictsFinder(Concept moved) {

			findDownwardsFrom(moved);
		}

		private void findDownwardsFrom(Concept moved) {

			findFor(moved.getConstraints(), true);
			findFor(moved.getInwardConstraints(), false);

			for (Concept child : moved.getChildren()) {

				findDownwardsFrom(child);
			}
		}

		private void findFor(Set<Constraint> constraints, boolean upOnly) {

			for (Constraint constraint : constraints) {

				if (anyUpwards(constraint) || (!upOnly && anyDownwards(constraint))) {

					conflicts.add(constraint);
				}
			}
		}

		private boolean anyUpwards(Constraint constraint) {

			return new UpwardsConflictFinder(constraint).any();
		}

		private boolean anyDownwards(Constraint constraint) {

			return new DownwardsConflictFinder(constraint).any();
		}
	}

	private abstract class ConstraintConflictsResolver {

		private List<Constraint> conflicts;

		void initialise(List<Constraint> conflicts) {

			this.conflicts = conflicts;
		}

		ConflictResolution check() {

			if (conflicts.isEmpty()) {

				return ConflictResolution.NO_CONFLICTS;
			}

			if (confirmConflictRemovals(conflicts)) {

				return new ConflictResolution(createConflictRemovalActions());
			}

			return ConflictResolution.NO_RESOLUTION;
		}

		abstract boolean confirmConflictRemovals(List<Constraint> conflicts);

		private List<EditAction> createConflictRemovalActions() {

			List<EditAction> actions = new ArrayList<EditAction>();

			for (Constraint conflict : conflicts) {

				actions.add(new RemoveAction(conflict));
			}

			return actions;
		}
	}

	private class ConstraintAdditionConflictsResolver extends ConstraintConflictsResolver {

		ConstraintAdditionConflictsResolver(Constraint constraint) {

			initialise(findConflicts(constraint));
		}

		boolean confirmConflictRemovals(List<Constraint> conflicts) {

			return confirmations.confirmConstraintAddition(conflicts);
		}

		private List<Constraint> findConflicts(Constraint constraint) {

			List<Constraint> conflicts = new ArrayList<Constraint>();

			Constraint localConflict = lookForLocalConflict(constraint);

			if (localConflict != null) {

				conflicts.add(localConflict);
			}

			conflicts.addAll(new UpwardsConflictFinder(constraint).findAll());
			conflicts.addAll(new DownwardsConflictFinder(constraint).findAll());

			return conflicts;
		}

		private Constraint lookForLocalConflict(Constraint constraint) {

			ConstraintType type = constraint.getType();

			if (type.singleValue()) {

				Concept source = constraint.getSourceValue();

				if (constraint.getSemantics().impliedValue()) {

					return source.lookForValidValuesConstraint(type);
				}

				return source.lookForImpliedValueConstraint(type);
			}

			return null;
		}
	}

	private class ConceptMoveConflictsResolver extends ConstraintConflictsResolver {

		ConceptMoveConflictsResolver(Concept moved) {

			initialise(new ConceptMoveConflictsFinder(moved).conflicts);
		}

		boolean confirmConflictRemovals(List<Constraint> conflicts) {

			return confirmations.confirmConceptMove(conflicts);
		}
	}

	void setConfirmations(Confirmations confirmations) {

		this.confirmations = confirmations;
	}

	ConflictResolution checkConstraintAddition(Constraint constraint) {

		return new ConstraintAdditionConflictsResolver(constraint).check();
	}

	ConflictResolution checkConceptMove(Concept moved) {

		return new ConceptMoveConflictsResolver(moved).check();
	}
}
