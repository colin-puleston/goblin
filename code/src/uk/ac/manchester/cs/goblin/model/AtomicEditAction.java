package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
abstract class AtomicEditAction<T extends EditTarget> extends EditAction {

	private T target;

	AtomicEditAction(T target) {

		this.target = target;
	}

	T getTarget() {

		return target;
	}

	void perform(boolean forward) {

		if (forward == addAction()) {

			target.doAdd(replaceSubAction());
		}
		else {

			target.doRemove(replaceSubAction());
		}
	}

	AtomicEditAction<T> getFinalAtomicAction(boolean forward) {

		return this;
	}

	EditLocation getEditLocation() {

		return new EditLocation(target);
	}

	abstract boolean addAction();

	boolean replaceSubAction() {

		return false;
	}
}
