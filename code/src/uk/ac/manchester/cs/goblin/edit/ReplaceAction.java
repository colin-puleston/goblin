package uk.ac.manchester.cs.goblin.edit;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public class ReplaceAction<T extends EditTarget> extends EditAction {

	private SubAction add;
	private SubAction remove;

	private abstract class SubAction extends AtomicEditAction<T> {

		SubAction(T target) {

			super(target);
		}

		boolean replaceSubAction() {

			return true;
		}
	}

	private class AddSubAction extends SubAction {

		AddSubAction(T target) {

			super(target);
		}

		boolean addAction() {

			return true;
		}
	}

	private class RemoveSubAction extends SubAction {

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

	private void perform(boolean forward, SubAction first, SubAction second) {

		first.perform(forward);
		performInterSubActionUpdates(first.getTarget(), second.getTarget());
		second.perform(forward);
	}
}
