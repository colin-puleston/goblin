package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class EntityIdTracker extends EntityTracker<EntityId> {

	EntityIdTracker(EntityId concept) {

		super(concept);
	}

	EntityIdTracker(EntityIdTracker template) {

		super(template);
	}

	EntityIdTracker copy() {

		return new EntityIdTracker(this);
	}
}
