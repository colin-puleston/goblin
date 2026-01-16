package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public interface HierarchyListener {

	public void onAddedDynamicConstraintType(DynamicConstraintType type);

	public void onRemovedDynamicConstraintType(DynamicConstraintType type);
}
