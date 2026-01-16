package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public abstract class PropertyConstraintType extends ConstraintType {

	private EntityId targetPropertyId;

	public abstract EntityId getTargetPropertyId();

	protected PropertyConstraintType(Concept rootSourceConcept, Concept rootTargetConcept) {

		super(rootSourceConcept, rootTargetConcept);
	}
}
