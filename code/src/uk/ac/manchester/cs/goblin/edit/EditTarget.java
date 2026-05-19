package uk.ac.manchester.cs.goblin.edit;

/**
 * @author Colin Puleston
 */
public interface EditTarget {

	public void doAdd();

	public void doRemove();

	public EditLocation createLocation(boolean postRemovalOp);
}
