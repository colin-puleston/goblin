package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ConstraintTypeTrackerSet extends EntityTrackerSet<ConstraintType, ConstraintTypeTracker> {

	ConstraintTypeTrackerSet() {
	}

	ConstraintTypeTrackerSet(Collection<ConstraintType> types) {

		super(types);
	}

	ConstraintTypeTrackerSet copy() {

		return new ConstraintTypeTrackerSet(this);
	}

	ConstraintTypeTracker toTracker(ConstraintType type) {

		return new ConstraintTypeTracker(type);
	}

	private ConstraintTypeTrackerSet(ConstraintTypeTrackerSet template) {

		super(template);
	}
}
