package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
abstract class ReplaceAction<T extends EditTarget> extends EditAction {

	private Atomic add;
	private Atomic remove;

	private abstract class Atomic extends AtomicEditAction<T> {

		Atomic(T target) {

			super(target);
		}

		boolean replaceSubAction() {

			return true;
		}
	}

	private class Add extends Atomic {

		Add(T target) {

			super(target);
		}

		boolean addAction() {

			return true;
		}
	}

	private class Remove extends Atomic {

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

	abstract EntityTracking<T, ?> getTargetTracking(T target);

	private void perform(boolean forward, Atomic first, Atomic second) {

		first.perform(forward);
		updateTargetTracking(first, second);
		second.perform(forward);
	}

	private void updateTargetTracking(Atomic first, Atomic second) {

		T target1 = first.getTarget();
		T target2 = second.getTarget();

		getTargetTracking(target1).updateForReplacement(target1, target2);
	}
}
