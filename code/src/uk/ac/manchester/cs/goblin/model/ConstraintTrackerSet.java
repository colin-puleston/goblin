package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ConstraintTrackerSet extends EntityTrackerSet<Constraint> {

	ConstraintTrackerSet(Model model) {

		super(model.getConstraintTracking());
	}

	ConstraintTrackerSet(Model model, Collection<Constraint> constraints) {

		super(model.getConstraintTracking(), constraints);
	}

	ConstraintTrackerSet copy() {

		return new ConstraintTrackerSet(this);
	}

	private ConstraintTrackerSet(ConstraintTrackerSet template) {

		super(template);
	}
}
