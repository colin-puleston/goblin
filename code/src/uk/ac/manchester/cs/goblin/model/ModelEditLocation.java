package uk.ac.manchester.cs.goblin.model;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public class ModelEditLocation extends EditLocation {

	private ModelEditTarget target;
	private boolean postRemovalOp;

	public Hierarchy getEditedHierarchy() {

		return target.getEditedHierarchy();
	}

	public Concept getEditedConceptOrNull() {

		return target.getEditedConceptOrNull(postRemovalOp);
	}

	public Attribute getEditedAttributeOrNull() {

		return target.getEditedAttributeOrNull(postRemovalOp);
	}

	ModelEditLocation(ModelEditTarget target, boolean postRemovalOp) {

		this.target = target;
		this.postRemovalOp = postRemovalOp;
	}
}
