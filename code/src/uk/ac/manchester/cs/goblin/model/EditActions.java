package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class EditActions {

	private Deque<EditAction> undos = new ArrayDeque<EditAction>();
	private Deque<EditAction> redos = new ArrayDeque<EditAction>();

	private boolean trackingStarted = false;

	private List<ModelEditListener> listeners = new ArrayList<ModelEditListener>();

	void startTracking() {

		trackingStarted = true;
	}

	void addListener(ModelEditListener listener) {

		listeners.add(listener);
	}

	void perform(EditAction action) {

		redos.clear();

		perfom(action, true, undos);
	}

	boolean canUndo() {

		return !undos.isEmpty();
	}

	boolean canRedo() {

		return !redos.isEmpty();
	}

	EditLocation undo() {

		return flip(false);
	}

	EditLocation redo() {

		return flip(true);
	}

	private EditLocation flip(boolean forward) {

		Deque<EditAction> froms = getActionStack(forward, true);
		Deque<EditAction> tos = getActionStack(forward, false);

		if (froms.isEmpty()) {

			throw new RuntimeException(
						"Cannot perform undo/redo operation: "
						+ "No actions available");
		}

		EditAction action = froms.pop();

		perfom(action, forward, tos);

		return action.getFinalAtomicAction(forward).getEditLocation();
	}

	private void perfom(EditAction action, boolean forward, Deque<EditAction> tos) {

		action.perform(forward);

		if (trackingStarted) {

			tos.push(action);
		}

		pollListenersForEdit();
	}

	private void pollListenersForEdit() {

		for (ModelEditListener listener : listeners) {

			listener.onEdit();
		}
	}

	private Deque<EditAction> getActionStack(boolean forward, boolean froms) {

		return forward == froms ? redos : undos;
	}
}
