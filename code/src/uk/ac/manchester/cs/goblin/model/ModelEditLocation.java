package uk.ac.manchester.cs.goblin.model;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public class ModelEditLocation extends EditLocation {

	private ModelEditTarget target;
	private boolean postRemovalOp;

	public Hierarchy getEditedHierarchy() {

		return target.getEditTargetConcept().getHierarchy();
	}

	public Concept getEditedConceptOrNull() {

		if (target instanceof Concept && postRemovalOp) {

			return null;
		}

		return target.getEditTargetConcept();
	}

	public Attribute getEditedAttributeOrNull() {

		if (target instanceof Attribute && postRemovalOp) {

			return null;
		}

		return target.getEditTargetAttributeOrNull();
	}

	ModelEditLocation(ModelEditTarget target, boolean postRemovalOp) {

		this.target = target;
		this.postRemovalOp = postRemovalOp;
	}
}
