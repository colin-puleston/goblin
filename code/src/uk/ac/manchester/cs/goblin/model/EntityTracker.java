package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class EntityTracker<E> {

	private E entity;

	EntityTracker(E entity) {

		this.entity = entity;
	}

	void replaceEntity(E replacement) {

		entity = replacement;
	}

	E getEntity() {

		return entity;
	}
}
