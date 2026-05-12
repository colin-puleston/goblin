package uk.ac.manchester.cs.goblin.model;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
abstract class ModelEditTarget implements EditTarget {

	public EditLocation createLocation(boolean postRemovalOp) {

		return new ModelEditLocation(this, postRemovalOp);
	}

	abstract Concept getEditedConceptOrNull(boolean postRemovalOp);

	Attribute getEditedAttributeOrNull(boolean postRemovalOp) {

		return null;
	}
}
