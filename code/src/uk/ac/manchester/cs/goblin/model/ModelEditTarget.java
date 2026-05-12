package uk.ac.manchester.cs.goblin.model;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
abstract class ModelEditTarget implements EditTarget {

	public void doAdd(boolean replacement) {

		addToModel(replacement);
	}

	public void doRemove(boolean replacing) {

		removeFromModel(replacing);
	}

	public EditLocation createLocation(boolean postRemovalOp) {

		return new ModelEditLocation(this, postRemovalOp);
	}

	abstract void addToModel(boolean replacement);

	abstract void removeFromModel(boolean replacing);

	abstract Concept getEditedConceptOrNull(boolean postRemovalOp);

	Attribute getEditedAttributeOrNull(boolean postRemovalOp) {

		return null;
	}
}
