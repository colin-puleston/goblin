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

	ConstraintTracker add(Constraint constraint) {

		ConstraintTracker tracker = new ConstraintTracker(constraint);

		add(tracker);

		return tracker;
	}

	ConstraintTracker remove(Constraint constraint) {

		ConstraintTracker tracker = getTrackerFor(constraint);

		remove(tracker);

		return tracker;
	}

	private ConstraintTrackerSet(ConstraintTrackerSet template) {

		super(template);
	}
}
