package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ValidValuesConstraint extends Constraint {

	public ConstraintSemantics getSemantics() {

		return ConstraintSemantics.VALID_VALUES;
	}

	ValidValuesConstraint(ConstraintType type, Concept sourceValue, Concept targetValue) {

		super(type, sourceValue, targetValue);
	}

	ValidValuesConstraint(
		ConstraintType type,
		Concept sourceValue,
		Collection<Concept> targetValues) {

		super(type, sourceValue, targetValues);
	}

	EditAction createTargetValueRemovalEditAction(Concept target) {

		Set<Concept> targets = getTargetValues();

		if (targets.size() == 1) {

			return new RemoveAction(this);
		}

		return new ReplaceConstraintAction(this, new ValidValuesConstraint(this, target));
	}

	boolean singleConstraintOfTypeAndSemanticsPerConcept() {

		return true;
	}

	private ValidValuesConstraint(ValidValuesConstraint template, Concept minusTargetValue) {

		super(template, minusTargetValue);
	}
}