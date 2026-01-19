package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public interface HierarchyListener {

	public void onAddedDynamicAttribute(DynamicAttribute attribute);

	public void onRemovedDynamicAttribute(DynamicAttribute attribute);
}
