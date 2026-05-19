package uk.ac.manchester.cs.goblin.edit;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public class ReplaceAction<T extends EditTarget> extends EditAction {

	private AtomicEditAction<T> add;
	private AtomicEditAction<T> remove;

	private class AddSubAction extends AtomicEditAction<T> {

		AddSubAction(T target) {

			super(target);
		}

		boolean addAction() {

			return true;
		}
	}

	private class RemoveSubAction extends AtomicEditAction<T> {

		RemoveSubAction(T target) {

			super(target);
		}

		boolean addAction() {

			return false;
		}
	}

	public ReplaceAction(T removeTarget, T addTarget) {

		add = new AddSubAction(addTarget);
		remove = new RemoveSubAction(removeTarget);
	}

	protected void performInterSubActionUpdates(T target1, T target2) {
	}

	void perform(boolean forward) {

		if (forward) {

			perform(true, remove, add);
		}
		else {

			perform(false, add, remove);
		}
	}

	AtomicEditAction<T> getFinalAtomicAction(boolean forward) {

		return forward ? add : remove;
	}

	private void perform(boolean forward, AtomicEditAction<T> first, AtomicEditAction<T> second) {

		first.perform(forward);
		performInterSubActionUpdates(first.getTarget(), second.getTarget());
		second.perform(forward);
	}
}
