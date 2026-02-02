package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ConflictResolver {

	private Confirmations confirmations = new AutoConfirmations();

	private abstract class ConstraintConflictFinder {

		final Constraint subject;

		private Attribute attribute;
		private List<Constraint> conflicts = new ArrayList<Constraint>();

		ConstraintConflictFinder(Constraint subject) {

			this.subject = subject;

			attribute = subject.getAttribute();
		}

		boolean anyFrom(Concept start) {

			findFrom(start);

			return !conflicts.isEmpty();
		}

		List<Constraint> findAllFromLinkedConcepts(Concept start) {

			findFromLinkedConcepts(start);

			return conflicts;
		}

		abstract List<Concept> getLinkedConcepts(Concept current);

		abstract Constraint getAncestorConstraint(Constraint candidate);

		abstract Constraint getDescendantConstraint(Constraint candidate);

		private void findFromLinkedConcepts(Concept current) {

			for (Concept child : getLinkedConcepts(current)) {

				findFrom(child);
			}
		}

		private void findFrom(Concept current) {

			for (Constraint candidate : current.getConstraints(attribute)) {

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

					if (attribute.getConstraintsOption().singleImpliedValues()) {

						return !dec.getTargetValue().descendantOf(anc.getTargetValue());
					}

					return anc.getTargetValue().subsumedBy(dec.getTargetValue());
				}

				return false;
			}

			return !Concept.allSubsumed(anc.getTargetValues(), dec.getTargetValues());
		}
	}

	private class UpwardsConstraintConflictFinder extends ConstraintConflictFinder {

		UpwardsConstraintConflictFinder(Constraint constraint) {

			super(constraint);
		}

		List<Concept> getLinkedConcepts(Concept current) {

			return current.getParents();
		}

		Constraint getAncestorConstraint(Constraint candidate) {

			return candidate;
		}

		Constraint getDescendantConstraint(Constraint candidate) {

			return subject;
		}
	}

	private class DownwardsConstraintConflictFinder extends ConstraintConflictFinder {

		DownwardsConstraintConflictFinder(Constraint constraint) {

			super(constraint);
		}

		List<Concept> getLinkedConcepts(Concept current) {

			return current.getChildren();
		}

		Constraint getAncestorConstraint(Constraint candidate) {

			return subject;
		}

		Constraint getDescendantConstraint(Constraint candidate) {

			return candidate;
		}
	}

	private abstract class ConstraintConflictsResolver {

		private List<Constraint> removals;

		void initialise(List<Constraint> removals) {

			this.removals = removals;
		}

		ConflictResolution check() {

			if (removals.isEmpty()) {

				return ConflictResolution.NO_CONFLICTS;
			}

			if (confirmConstraintRemovals(removals)) {

				return new ConflictResolution(createConflictRemovalActions());
			}

			return ConflictResolution.NO_RESOLUTION;
		}

		abstract boolean confirmConstraintRemovals(List<Constraint> removals);

		private List<EditAction> createConflictRemovalActions() {

			List<EditAction> actions = new ArrayList<EditAction>();

			for (Constraint conflict : removals) {

				actions.add(new RemoveAction(conflict));
			}

			return actions;
		}
	}

	private class ConceptMoveOrphanedConstraintsResolver extends ConstraintConflictsResolver {

		private Concept concept;

		ConceptMoveOrphanedConstraintsResolver(Concept concept, Concept newParent) {

			this.concept = concept;

			initialise(findOrphanedConstraints(newParent));
		}

		boolean confirmConstraintRemovals(List<Constraint> removals) {

			return confirmations.confirmConceptMoveOrphanedConstraintRemovals(concept, removals);
		}

		private List<Constraint> findOrphanedConstraints(Concept newParent) {

			List<Constraint> orphaneds = new ArrayList<Constraint>();

			for (DynamicAttribute attr : getPotentialOrphanParentAttributes(newParent)) {

				orphaneds.addAll(concept.getConstraintsDownwards(attr));
			}

			return orphaneds;
		}

		private List<DynamicAttribute> getPotentialOrphanParentAttributes(Concept newParent) {

			List<DynamicAttribute> attrs = new ArrayList<DynamicAttribute>();

			attrs.addAll(concept.getParent().getDynamicAttributesUpwards());
			attrs.removeAll(newParent.getDynamicAttributesUpwards());

			return attrs;
		}
	}

	private class ConceptMoveConflictingConstraintsResolver extends ConstraintConflictsResolver {

		private Concept concept;
		private Concept newParent;

		private class ConflictsFinder {

			final List<Constraint> conflicts = new ArrayList<Constraint>();

			ConflictsFinder() {

				findDownwardsFrom(concept);
			}

			private void findDownwardsFrom(Concept current) {

				findFor(current.getConstraints());
				findFor(current.getInwardConstraints());

				for (Concept child : current.getChildren()) {

					findDownwardsFrom(child);
				}
			}

			private void findFor(List<Constraint> constraints) {

				for (Constraint constraint : constraints) {

					if (anyUpwardConflicts(constraint)) {

						conflicts.add(constraint);
					}
				}
			}

			private boolean anyUpwardConflicts(Constraint constraint) {

				return new UpwardsConstraintConflictFinder(constraint).anyFrom(newParent);
			}
		}

		ConceptMoveConflictingConstraintsResolver(Concept concept, Concept newParent) {

			this.concept = concept;
			this.newParent = newParent;

			initialise(new ConflictsFinder().conflicts);
		}

		boolean confirmConstraintRemovals(List<Constraint> removals) {

			return confirmations.confirmConceptMoveConflictingConstraintRemovals(concept, removals);
		}
	}

	private class ConstraintAdditionConflictsResolver extends ConstraintConflictsResolver {

		ConstraintAdditionConflictsResolver(Constraint constraint) {

			initialise(findConflicts(constraint));
		}

		boolean confirmConstraintRemovals(List<Constraint> removals) {

			return confirmations.confirmConstraintAdditionConflictRemovals(removals);
		}

		private List<Constraint> findConflicts(Constraint constraint) {

			Concept source = constraint.getSourceValue();

			ConstraintConflictFinder upFinder = new UpwardsConstraintConflictFinder(constraint);
			ConstraintConflictFinder downFinder = new DownwardsConstraintConflictFinder(constraint);

			List<Constraint> conflicts = new ArrayList<Constraint>();

			conflicts.addAll(upFinder.findAllFromLinkedConcepts(source));
			conflicts.addAll(downFinder.findAllFromLinkedConcepts(source));

			return conflicts;
		}
	}

	void setConfirmations(Confirmations confirmations) {

		this.confirmations = confirmations;
	}

	ConflictResolution checkConceptMove(Concept concept, Concept newParent) {

		ConflictResolution orphans = checkConceptMoveOrphanedConstraints(concept, newParent);

		if (orphans == ConflictResolution.NO_RESOLUTION) {

			return ConflictResolution.NO_RESOLUTION;
		}

		ConflictResolution invalids = checkConceptMoveConflictingConstraints(concept, newParent);

		if (invalids == ConflictResolution.NO_RESOLUTION) {

			return ConflictResolution.NO_RESOLUTION;
		}

		if (orphans == ConflictResolution.NO_CONFLICTS) {

			return invalids;
		}

		if (invalids == ConflictResolution.NO_CONFLICTS) {

			return orphans;
		}

		return orphans.combineWith(invalids);
	}

	ConflictResolution checkConstraintAddition(Constraint constraint) {

		return new ConstraintAdditionConflictsResolver(constraint).check();
	}

	private ConflictResolution checkConceptMoveOrphanedConstraints(
									Concept concept,
									Concept newParent) {

		return new ConceptMoveOrphanedConstraintsResolver(concept, newParent).check();
	}

	private ConflictResolution checkConceptMoveConflictingConstraints(
									Concept concept,
									Concept newParent) {

		return new ConceptMoveConflictingConstraintsResolver(concept, newParent).check();
	}
}
