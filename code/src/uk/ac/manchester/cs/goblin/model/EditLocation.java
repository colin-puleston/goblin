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

	public Concept getEditedConcept() {

		return target.getEditTargetConcept();
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
