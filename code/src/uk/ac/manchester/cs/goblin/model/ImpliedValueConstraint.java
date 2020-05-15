package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ImpliedValueConstraint extends Constraint {

	public ConstraintSemantics getSemantics() {

		return ConstraintSemantics.IMPLIED_VALUE;
	}

	ImpliedValueConstraint(ConstraintType type, Concept sourceValue, Concept targetValue) {

		super(type, sourceValue, targetValue);
	}

	EditAction createTargetValueRemovalEditAction(Concept target) {

		return new RemoveAction(this);
	}

	boolean singleConstraintOfTypeAndSemanticsPerConcept() {

		return getType().singleValue();
	}
}