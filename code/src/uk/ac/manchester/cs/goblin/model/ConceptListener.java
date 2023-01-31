package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public interface ConceptListener {

	public void onChildAdded(Concept child, boolean replacement);

	public void onConstraintAdded(Constraint constraint, boolean inward);

	public void onConstraintRemoved(Constraint constraint, boolean inward);

	public void onConceptRemoved(Concept concept, boolean replacing);
}
