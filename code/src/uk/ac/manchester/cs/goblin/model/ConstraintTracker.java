package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ConstraintTracker extends EntityTracker<Constraint> {

	ConstraintTracker(Constraint constraint) {

		super(constraint);
	}

	ConstraintTracker(ConstraintTracker template) {

		super(template);
	}

	ConstraintTracker copy() {

		return new ConstraintTracker(this);
	}
}
