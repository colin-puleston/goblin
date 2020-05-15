package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
abstract class EntityTracking<E, T extends EntityTracker<E>> {

	private Map<E, T> trackersByEntity = new HashMap<E, T>();

	T toTracker(E entity) {

		T tracker = trackersByEntity.get(entity);

		if (tracker == null) {

			tracker = createTracker(entity);
			trackersByEntity.put(entity, tracker);
		}

		return tracker;
	}

	void updateForReplacement(E replaced, E replacement) {

		T tracker = trackersByEntity.remove(replaced);

		if (tracker != null) {

			tracker.replaceEntity(replacement);
			trackersByEntity.put(replacement, tracker);
		}
	}

	abstract T createTracker(E entity);
}
