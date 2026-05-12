package uk.ac.manchester.cs.goblin.edit;

/**
 * @author Colin Puleston
 */
public class AddAction extends AtomicEditAction<EditTarget> {

	public AddAction(EditTarget target) {

		super(target);
	}

	boolean addAction() {

		return true;
	}
}
