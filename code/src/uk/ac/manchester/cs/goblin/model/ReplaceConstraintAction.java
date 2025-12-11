package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ReplaceConstraintAction extends ReplaceAction<Constraint> {

	ReplaceConstraintAction(Constraint removeTarget, Constraint addTarget) {

		super(removeTarget, addTarget);
	}

	void performInterSubActionUpdates(Constraint target1, Constraint target2) {
	}
}
