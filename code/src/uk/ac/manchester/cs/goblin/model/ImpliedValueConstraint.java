package uk.ac.manchester.cs.goblin.model;

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

	EditAction checkIncorporateConstraintRemoval(EditAction action) {

		Attribute attribute = getAttribute();

		if (attribute.getConstraintsOption().singleImpliedValues()) {

			Constraint constraint = lookForImpliedValueConstraint(attribute);

			if (constraint != null) {

				return new CompoundEditAction(new RemoveAction(constraint), action);
			}
		}

		return action;
	}

	private Constraint lookForImpliedValueConstraint(Attribute attribute) {

		return getSourceValue().lookForConstraint(attribute, ConstraintSemantics.IMPLIED_VALUE);
	}
}