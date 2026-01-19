package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class AttributeTrackerSet extends EntityTrackerSet<Attribute, AttributeTracker> {

	AttributeTrackerSet() {
	}

	AttributeTrackerSet(Collection<Attribute> attributes) {

		super(attributes);
	}

	AttributeTrackerSet copy() {

		return new AttributeTrackerSet(this);
	}

	AttributeTracker toTracker(Attribute attribute) {

		return new AttributeTracker(attribute);
	}

	private AttributeTrackerSet(AttributeTrackerSet template) {

		super(template);
	}
}
