package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
class ReplaceConceptAction extends ReplaceAction<Concept> {

	ReplaceConceptAction(Concept removeTarget, Concept addTarget) {

		super(removeTarget, addTarget);
	}

	void performInterSubActionUpdates(Concept target1, Concept target2) {

		getTargetTracking(target1).updateForReplacement(target1, target2);
	}

	private ConceptTracking getTargetTracking(Concept target) {

		return target.getModel().getConceptTracking();
	}
}
