package uk.ac.manchester.cs.goblin.model;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
class ReplaceConceptAction extends ReplaceAction<Concept> {

	protected void performInterSubActionUpdates(Concept target1, Concept target2) {

		getTargetTracking(target1).updateForReplacement(target1, target2);
	}

	ReplaceConceptAction(Concept removeTarget, Concept addTarget) {

		super(removeTarget, addTarget);
	}

	private ConceptTracking getTargetTracking(Concept target) {

		return target.getModel().getConceptTracking();
	}
}
