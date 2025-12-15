package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ConstraintTrackerSet extends EntityTrackerSet<Constraint, ConstraintTracker> {

	ConstraintTrackerSet() {
	}

	ConstraintTrackerSet(Collection<Constraint> constraints) {

		super(constraints);
	}

	ConstraintTrackerSet copy() {

		return new ConstraintTrackerSet(this);
	}

	ConstraintTracker toTracker(Constraint constraint) {

		return new ConstraintTracker(constraint);
	}

	private ConstraintTrackerSet(ConstraintTrackerSet template) {

		super(template);
	}
}
