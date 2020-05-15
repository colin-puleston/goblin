package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ConstraintTracking extends EntityTracking<Constraint, ConstraintTracker> {

	ConstraintTracker createTracker(Constraint constraint) {

		return new ConstraintTracker(constraint);
	}
}
