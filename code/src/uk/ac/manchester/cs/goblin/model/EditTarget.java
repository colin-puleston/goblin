package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
abstract class EditTarget {

	abstract void doAdd(boolean replacement);

	abstract void doRemove(boolean replacing);

	abstract Hierarchy getPrimaryEditHierarchy();
}
