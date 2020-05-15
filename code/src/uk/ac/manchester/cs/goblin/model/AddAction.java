package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class AddAction extends AtomicEditAction<EditTarget> {

	AddAction(EditTarget target) {

		super(target);
	}

	boolean addAction() {

		return true;
	}
}
