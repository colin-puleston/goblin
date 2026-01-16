package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ConstraintTypeTracker extends EntityTracker<ConstraintType> {

	ConstraintTypeTracker(ConstraintType type) {

		super(type);
	}

	ConstraintTypeTracker(ConstraintTypeTracker template) {

		super(template);
	}

	ConstraintTypeTracker copy() {

		return new ConstraintTypeTracker(this);
	}
}
