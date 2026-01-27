package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class Constraint extends EditTarget {

	private Attribute attribute;

	private ConceptTracker sourceValue;
	private ConceptTrackerSet targetValues;

	public void remove() {

		performAction(new RemoveAction(this));
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

			EditAction action = new AddAction(this);

			action = conflictRes.incorporateResolvingEdits(action);
			action = checkIncorporateConstraintRemoval(action);

			performAction(action);

			return true;
		}

		return false;
	}

	abstract EditAction createTargetValueRemovalEditAction(Concept target);

	void doAdd(boolean replacement) {

		getSourceValue().addConstraint(this);
	}

	void doRemove(boolean replacing) {

		getSourceValue().removeConstraint(this);
	}

	Concept getEditTargetConcept() {

		return getSourceValue();
	}

	Attribute getEditTargetAttributeOrNull() {

		return attribute;
	}

	boolean onAttribute(Attribute testAttr) {

		return testAttr.equals(attribute);
	}

	abstract EditAction checkIncorporateConstraintRemoval(EditAction action);

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
}
