package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class RemoveAction extends AtomicEditAction<EditTarget> {

	RemoveAction(EditTarget target) {

		super(target);
	}

	boolean addAction() {

		return false;
	}
}
