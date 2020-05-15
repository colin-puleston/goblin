package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ReplaceConceptAction extends ReplaceAction<Concept> {

	ReplaceConceptAction(Concept removeTarget, Concept addTarget) {

		super(removeTarget, addTarget);
	}

	ConceptTracking getTargetTracking(Concept target) {

		return target.getModel().getConceptTracking();
	}
}
