package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public class EditLocation {

	private EditTarget target;

	public boolean conceptEdit() {

		return target instanceof Concept;
	}

	public boolean constraintEdit() {

		return target instanceof Constraint;
	}

	public Hierarchy getPrimaryEditHierarchy() {

		return target.getPrimaryEditHierarchy();
	}

	public Constraint getEditedConstraint() {

		if (constraintEdit()) {

			return (Constraint)target;
		}

		throw new RuntimeException("Not a constraint edit");
	}

	EditLocation(EditTarget target) {

		this.target = target;
	}
}
