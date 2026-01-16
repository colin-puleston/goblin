package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
abstract class EntityTrackerSet<E, T extends EntityTracker<E>> {

	private List<T> trackers = new ArrayList<T>();

	EntityTrackerSet() {
	}

	EntityTrackerSet(Collection<E> entities) {

		for (E entity : entities) {

			add(entity);
		}
	}

	EntityTrackerSet(EntityTrackerSet<E, T> template) {

		trackers.addAll(template.trackers);
	}

	void add(T tracker) {

		trackers.add(tracker);
	}

	void remove(T tracker) {

		trackers.remove(tracker);
	}

	T add(E entity) {

		T tracker = toTracker(entity);

		add(tracker);

		return tracker;
	}

	T remove(E entity) {

		T tracker = getTrackerFor(entity);

		remove(tracker);

		return tracker;
	}

	boolean isEmpty() {

		return trackers.isEmpty();
	}

	List<E> getEntities() {

		List<E> entities = new ArrayList<E>();

		for (T tracker : trackers) {

			entities.add(tracker.getEntity());
		}

		return entities;
	}

	T getTrackerFor(E entity) {

		for (T tracker : trackers) {

			if (tracker.getEntity().equals(entity)) {

				return tracker;
			}
		}

		throw new Error("Cannot find tracker for: " + entity);
	}

	abstract T toTracker(E entity);
}
