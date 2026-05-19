package uk.ac.manchester.cs.goblin.model;

import java.util.*;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
abstract class EditableId<L extends EditableIdListener> {

	private EntityId id;
	private List<L> listeners;

	private EditActions<?> editActions;

	private class IdUpdateTarget implements EditTarget {

		private EntityId editId;

		public void doAdd() {

			id = editId;

			onIdUpdate();
		}

		public void doRemove() {
		}

		public EditLocation createLocation(boolean postRemovalOp) {

			return createEditLocation();
		}

		IdUpdateTarget(EntityId editId) {

			this.editId = editId;
		}
	}

	EditableId(EntityId id, EditActions<?> editActions, List<L> listeners) {

		this.id = id;
		this.editActions = editActions;
		this.listeners = listeners;
	}

	void resetId(EntityId attrId) {

		editActions.perform(createReplaceIdAction(attrId));
	}

	void addListener(L listener) {

		listeners.add(listener);
	}

	void removeListener(L listener) {

		listeners.remove(listener);
	}

	void removeListenersOfType(Class<? extends L> type) {

		for (L listener : copyListeners()) {

			if (type.isAssignableFrom(listener.getClass())) {

				listeners.remove(listener);
			}
		}
	}

	EntityId getId() {

		return id;
	}

	abstract EditLocation createEditLocation();

	private ReplaceAction<IdUpdateTarget> createReplaceIdAction(EntityId newId) {

		return new ReplaceAction<IdUpdateTarget>(
						new IdUpdateTarget(id),
						new IdUpdateTarget(newId));
	}

	private void onIdUpdate() {

		for (EditableIdListener listener : copyListeners()) {

			listener.onIdUpdate();
		}
	}

	private List<L> copyListeners() {

		return new ArrayList<L>(listeners);
	}
}
