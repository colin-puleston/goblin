package uk.ac.manchester.cs.goblin.model;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
class ImpliedValueConstraint extends Constraint {

	public ConstraintSemantics getSemantics() {

		return ConstraintSemantics.IMPLIED_VALUE;
	}

	ImpliedValueConstraint(Attribute attribute, Concept sourceValue, Concept targetValue) {

		super(attribute, sourceValue, targetValue);
	}

	EditAction createTargetValueRemovalEditAction(Concept target) {

		return new RemoveAction(this);
	}

	boolean onlySingleConstraintOfTypeAllowed() {

		return getAttribute().getConstraintsOption().singleImpliedValues();
	}
}