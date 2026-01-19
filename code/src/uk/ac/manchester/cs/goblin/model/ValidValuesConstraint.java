package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ValidValuesConstraint extends Constraint {

	public ConstraintSemantics getSemantics() {

		return ConstraintSemantics.VALID_VALUES;
	}

	ValidValuesConstraint(Attribute attribute, Concept sourceValue, Concept targetValue) {

		super(attribute, sourceValue, targetValue);
	}

	ValidValuesConstraint(
		Attribute attribute,
		Concept sourceValue,
		Collection<Concept> targetValues) {

		super(attribute, sourceValue, targetValues);
	}

	EditAction createTargetValueRemovalEditAction(Concept target) {

		Collection<Concept> targets = getTargetValues();

		if (targets.size() == 1) {

			return new RemoveAction(this);
		}

		return new ReplaceConstraintAction(this, new ValidValuesConstraint(this, target));
	}

	EditAction checkIncorporateConstraintRemoval(EditAction action) {

		return action;
	}

	private ValidValuesConstraint(ValidValuesConstraint template, Concept minusTargetValue) {

		super(template, minusTargetValue);
	}
}