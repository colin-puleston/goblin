package uk.ac.manchester.cs.goblin.edit;

/**
 * @author Colin Puleston
 */
public abstract class AtomicEditAction<T extends EditTarget> extends EditAction {

	private T target;

	public AtomicEditAction(T target) {

		this.target = target;
	}

	public T getTarget() {

		return target;
	}

	void perform(boolean forward) {

		if (forward == addAction()) {

			target.doAdd();
		}
		else {

			target.doRemove();
		}
	}

	AtomicEditAction<T> getFinalAtomicAction(boolean forward) {

		return this;
	}

	EditLocation getEditLocation(boolean forward) {

		return target.createLocation(addAction() != forward);
	}

	abstract boolean addAction();
}
