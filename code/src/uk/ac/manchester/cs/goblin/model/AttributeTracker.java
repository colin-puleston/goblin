package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class AttributeTracker extends EntityTracker<Attribute> {

	AttributeTracker(Attribute attribute) {

		super(attribute);
	}

	AttributeTracker(AttributeTracker template) {

		super(template);
	}

	AttributeTracker copy() {

		return new AttributeTracker(this);
	}
}
