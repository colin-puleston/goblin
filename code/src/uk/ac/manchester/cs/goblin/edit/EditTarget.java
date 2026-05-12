package uk.ac.manchester.cs.goblin.edit;

/**
 * @author Colin Puleston
 */
public interface EditTarget {

	public void doAdd(boolean replacement);

	public void doRemove(boolean replacing);

	public EditLocation createLocation(boolean postRemovalOp);
}
