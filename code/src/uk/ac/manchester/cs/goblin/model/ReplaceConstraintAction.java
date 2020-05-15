package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ReplaceConstraintAction extends ReplaceAction<Constraint> {

	ReplaceConstraintAction(Constraint removeTarget, Constraint addTarget) {

		super(removeTarget, addTarget);
	}

	ConstraintTracking getTargetTracking(Constraint target) {

		return target.getModel().getConstraintTracking();
	}
}
