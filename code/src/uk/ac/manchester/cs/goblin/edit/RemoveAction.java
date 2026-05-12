package uk.ac.manchester.cs.goblin.edit;

/**
 * @author Colin Puleston
 */
public class RemoveAction extends AtomicEditAction<EditTarget> {

	public RemoveAction(EditTarget target) {

		super(target);
	}

	boolean addAction() {

		return false;
	}
}
