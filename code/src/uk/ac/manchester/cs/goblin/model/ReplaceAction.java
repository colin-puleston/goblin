package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
abstract class ReplaceAction<T extends EditTarget> extends EditAction {

	private ComponentAction add;
	private ComponentAction remove;

	private abstract class ComponentAction extends AtomicEditAction<T> {

		ComponentAction(T target) {

			super(target);
		}

		boolean replaceSubAction() {

			return true;
		}
	}

	private class Add extends ComponentAction {

		Add(T target) {

			super(target);
		}

		boolean addAction() {

			return true;
		}
	}

	private class Remove extends ComponentAction {

		Remove(T target) {

			super(target);
		}

		boolean addAction() {

			return false;
		}
	}

	ReplaceAction(T removeTarget, T addTarget) {

		add = new Add(addTarget);
		remove = new Remove(removeTarget);
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

	abstract void performInterSubActionUpdates(T target1, T target2);

	private void perform(boolean forward, ComponentAction first, ComponentAction second) {

		first.perform(forward);
		performInterSubActionUpdates(first.getTarget(), second.getTarget());
		second.perform(forward);
	}
}
