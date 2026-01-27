package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public class EditLocation {

	private EditTarget target;
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

	EditLocation(EditTarget target, boolean postRemovalOp) {

		this.target = target;
		this.postRemovalOp = postRemovalOp;
	}
}
