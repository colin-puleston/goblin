package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public interface ConceptListener extends EditableIdListener {

	public void onChildAdded(Concept child);

	public void onConstraintAdded(Constraint constraint, boolean outward);

	public void onConstraintRemoved(Constraint constraint, boolean outward);

	public void onConceptRemoved();
}
