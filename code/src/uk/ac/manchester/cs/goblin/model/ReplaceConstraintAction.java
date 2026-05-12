package uk.ac.manchester.cs.goblin.model;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
class ReplaceConstraintAction extends ReplaceAction<Constraint> {

	protected void performInterSubActionUpdates(Constraint target1, Constraint target2) {
	}

	ReplaceConstraintAction(Constraint removeTarget, Constraint addTarget) {

		super(removeTarget, addTarget);
	}
}
