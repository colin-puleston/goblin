package uk.ac.manchester.cs.goblin.model;

import java.util.*;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public abstract class Constraint {

	private Attribute attribute;

	private ConceptTracker sourceValue;
	private ConceptTrackerSet targetValues;

	private class AddRemoveTarget extends ModelEditTarget {

		public void doAdd(boolean replacement) {

			getSourceValue().addConstraint(Constraint.this);
		}

		public void doRemove(boolean replacing) {

			getSourceValue().removeConstraint(Constraint.this);
		}

		Concept getEditedConceptOrNull(boolean postRemovalOp) {

			return getSourceValue();
		}

		Attribute getEditedAttributeOrNull(boolean postRemovalOp) {

			return attribute;
		}
	}

	public void remove() {

		performAction(new RemoveAction(new AddRemoveTarget()));
	}

	public String toString() {

		return getSemantics() + ": " + getSourceValue() + " --> " + getTargetValues();
	}

	public Model getModel() {

		return getSourceValue().getModel();
	}

	public Attribute getAttribute() {

		return attribute;
	}

	public Concept getSourceValue() {

		return sourceValue.getEntity();
	}

	public List<Concept> getTargetValues() {

		return targetValues.getEntities();
	}

	public Concept getTargetValue() {

		List<Concept> targets = getTargetValues();

		if (targets.size() == 1) {

			return targets.iterator().next();
		}

		throw new RuntimeException("Expected exactly 1 value, found " + targets.size());
	}

	public boolean hasSemantics(ConstraintSemantics semantics) {

		return getSemantics() == semantics;
	}

	public abstract ConstraintSemantics getSemantics();

	Constraint(Attribute attribute, Concept sourceValue, Concept targetValue) {

		this(attribute, sourceValue, Collections.singletonList(targetValue));
	}

	Constraint(Attribute attribute, Concept sourceValue, Collection<Concept> targetValues) {

		this.attribute = attribute;
		this.sourceValue = sourceValue.toTracker();
		this.targetValues = new ConceptTrackerSet(targetValues);

		checkTargetConflicts(targetValues);
	}

	Constraint(Constraint template, Concept minusTargetValue) {

		attribute = template.attribute;
		sourceValue = template.sourceValue;
		targetValues = template.targetValues.copy();

		targetValues.remove(minusTargetValue);
	}

	boolean add() {

		ConflictResolution conflictRes = checkAdditionConflicts();

		if (conflictRes.resolvable()) {

			EditAction action = new AddAction(new AddRemoveTarget());

			action = conflictRes.incorporateResolvingEdits(action);
			action = checkIncorporateConstraintRemoval(action);

			performAction(action);

			return true;
		}

		return false;
	}

	boolean onAttribute(Attribute testAttr) {

		return testAttr.equals(attribute);
	}

	RemoveAction createRemoveAction() {

		return new RemoveAction(new AddRemoveTarget());
	}

	ReplaceAction<?> createReplaceAction(Constraint replacement) {

		return new ReplaceAction<AddRemoveTarget>(
						new AddRemoveTarget(),
						replacement.createAddRemoveTarget());
	}

	abstract EditAction createTargetValueRemovalEditAction(Concept target);

	abstract boolean onlySingleConstraintOfTypeAllowed();

	private void checkTargetConflicts(Collection<Concept> targetValues) {

		for (Concept value1 : targetValues) {

			for (Concept value2 : targetValues) {

				if (value1 != value2 && value1.descendantOf(value2)) {

					throw new RuntimeException(
								"Conflicting target-values: "
								+ value1 + " descendant-of " + value2);
				}
			}
		}
	}

	private void performAction(EditAction action) {

		getModel().getEditActions().perform(action);
	}

	private ConflictResolution checkAdditionConflicts() {

		return getModel().getConflictResolver().checkConstraintAddition(this);
	}

	private EditAction checkIncorporateConstraintRemoval(EditAction action) {

		if (onlySingleConstraintOfTypeAllowed()) {

			Constraint constraint = lookForCurrentConstraintOfType();

			if (constraint != null) {

				return new CompoundEditAction(createRemoveAction(), action);
			}
		}

		return action;
	}

	private Constraint lookForCurrentConstraintOfType() {

		return getSourceValue().lookForConstraint(getAttribute(), getSemantics());
	}

	private AddRemoveTarget createAddRemoveTarget() {

		return new AddRemoveTarget();
	}
}
