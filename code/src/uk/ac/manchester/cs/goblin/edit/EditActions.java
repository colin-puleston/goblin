package uk.ac.manchester.cs.goblin.edit;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class EditActions<L extends EditLocation> {

	private Deque<EditAction> undos = new ArrayDeque<EditAction>();
	private Deque<EditAction> redos = new ArrayDeque<EditAction>();

	private boolean trackingStarted = false;

	private List<EditListener> listeners = new ArrayList<EditListener>();

	public void startTracking() {

		trackingStarted = true;
	}

	public void addListener(EditListener listener) {

		listeners.add(listener);
	}

	public boolean canUndo() {

		return !undos.isEmpty();
	}

	public boolean canRedo() {

		return !redos.isEmpty();
	}

	public L undo() {

		return flip(false);
	}

	public L redo() {

		return flip(true);
	}

	public void perform(EditAction action) {

		redos.clear();

		perfom(action, true, undos);
	}

	protected abstract Class<L> getEditLocationClass();

	private L flip(boolean forward) {

		Deque<EditAction> froms = getActionStack(forward, true);
		Deque<EditAction> tos = getActionStack(forward, false);

		if (froms.isEmpty()) {

			throw new RuntimeException(
						"Cannot perform undo/redo operation: "
						+ "No actions available");
		}

		EditAction action = froms.pop();

		perfom(action, forward, tos);

		return getEditLocation(action.getFinalAtomicAction(forward), forward);
	}

	private void perfom(EditAction action, boolean forward, Deque<EditAction> tos) {

		action.perform(forward);

		if (trackingStarted) {

			tos.push(action);
		}

		pollListenersForEdit();
	}

	private L getEditLocation(AtomicEditAction<?> atomicAction, boolean forward) {

		return getEditLocationClass().cast(atomicAction.getEditLocation(forward));
	}

	private void pollListenersForEdit() {

		for (EditListener listener : listeners) {

			listener.onEdit();
		}
	}

	private Deque<EditAction> getActionStack(boolean forward, boolean froms) {

		return forward == froms ? redos : undos;
	}
}
