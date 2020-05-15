package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class Constraint extends EditTarget {

	private ConstraintType type;

	private ConceptTracker sourceValue;
	private ConceptTrackerSet targetValues;

	public void remove() {

		performAction(new RemoveAction(this));
	}

	public String toString() {

		return getSourceValue() + " --> " + getTargetValues();
	}

	public Model getModel() {

		return getSourceValue().getModel();
	}

	public ConstraintType getType() {

		return type;
	}

	public Concept getSourceValue() {

		return sourceValue.getEntity();
	}

	public Set<Concept> getTargetValues() {

		return targetValues.getEntities();
	}

	public Concept getTargetValue() {

		Set<Concept> targets = getTargetValues();

		if (targets.size() == 1) {

			return targets.iterator().next();
		}

		throw new RuntimeException("Expected exactly 1 value, found " + targets.size());
	}

	public boolean hasSemantics(ConstraintSemantics semantics) {

		return getSemantics() == semantics;
	}

	public abstract ConstraintSemantics getSemantics();

	Constraint(ConstraintType type, Concept sourceValue, Concept targetValue) {

		this(type, sourceValue, Collections.singletonList(targetValue));
	}

	Constraint(ConstraintType type, Concept sourceValue, Collection<Concept> targetValues) {

		this.type = type;
		this.sourceValue = toConceptTracker(sourceValue);
		this.targetValues = new ConceptTrackerSet(getModel(), targetValues);

		checkTargetConflicts(targetValues);
	}

	Constraint(Constraint template, Concept minusTargetValue) {

		type = template.type;
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

		getSourceValue().doAddConstraint(this);

		for (Concept target : getTargetValues()) {

			target.addInwardConstraint(this);
		}
	}

	void doRemove(boolean replacing) {

		getSourceValue().doRemoveConstraint(this);

		for (Concept target : getTargetValues()) {

			target.removeInwardConstraint(this);
		}
	}

	Hierarchy getPrimaryEditHierarchy() {

		return getSourceValue().getHierarchy();
	}

	boolean hasType(ConstraintType testType) {

		return testType.equals(type);
	}

	abstract boolean singleConstraintOfTypeAndSemanticsPerConcept();

	private ConceptTracker toConceptTracker(Concept concept) {

		return concept.getModel().getConceptTracking().toTracker(concept);
	}

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

	private EditAction checkIncorporateConstraintRemoval(EditAction action) {

		if (singleConstraintOfTypeAndSemanticsPerConcept()) {

			Constraint constraint = lookForTypeAndSemanticsConstraint();

			if (constraint != null) {

				return new CompoundEditAction(new RemoveAction(constraint), action);
			}
		}

		return action;
	}

	private Constraint lookForTypeAndSemanticsConstraint() {

		return getSourceValue().lookForConstraint(type, getSemantics());
	}

	private void performAction(EditAction action) {

		getModel().getEditActions().perform(action);
	}

	private ConflictResolution checkAdditionConflicts() {

		return getModel().getConflictResolver().checkConstraintAddition(this);
	}
}
