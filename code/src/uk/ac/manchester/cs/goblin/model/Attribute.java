package uk.ac.manchester.cs.goblin.model;

import java.util.*;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public abstract class Attribute {

	private Concept rootSourceConcept;
	private Concept rootTargetConcept;

	private Constraint rootConstraint;

	public String toString() {

		return getLabel() + "(" + rootSourceConcept + " --> " + rootTargetConcept + ")";
	}

	public boolean dynamicAttribute() {

		return false;
	}

	public boolean currentlyActive() {

		return true;
	}

	public abstract String getLabel();

	public Concept getRootSourceConcept() {

		return rootSourceConcept;
	}

	public Concept getRootTargetConcept() {

		return rootTargetConcept;
	}

	public Constraint getRootConstraint() {

		return rootConstraint;
	}

	public abstract ConstraintsOption getConstraintsOption();

	protected Attribute(Concept rootSourceConcept, Concept rootTargetConcept) {

		this.rootSourceConcept = rootSourceConcept;
		this.rootTargetConcept = rootTargetConcept;

		rootConstraint = createRootConstraint();
	}

	void initialiseAsCoreAttribute() {

		rootTargetConcept.getHierarchy().addInwardCoreAttribute(this);

		rootSourceConcept.addConstraint(rootConstraint);
	}

	Model getModel() {

		return rootSourceConcept.getModel();
	}

	Constraint createValidValues(Concept sourceValue, Collection<Concept> targetValues) {

		validateSourceValue(sourceValue);

		for (Concept targetValue : targetValues) {

			validateTargetValue(targetValue);
		}

		return new ValidValuesConstraint(this, sourceValue, targetValues);
	}

	Constraint createImpliedValue(Concept sourceValue, Concept targetValue) {

		validateSourceValue(sourceValue);
		validateTargetValue(targetValue);

		return new ImpliedValueConstraint(this, sourceValue, targetValue);
	}

	private Constraint createRootConstraint() {

		return new ValidValuesConstraint(this, rootSourceConcept, rootTargetConcept);
	}

	private void validateSourceValue(Concept value) {

		validateValue(rootSourceConcept, value, "Source");
	}

	private void validateTargetValue(Concept value) {

		validateValue(rootTargetConcept, value, "Target");
	}

	private void validateValue(Concept root, Concept value, String function) {

		if (!value.descendantOf(root)) {

			throw new RuntimeException(
						function + "-value concept \"" + value + "\""
						+ " not a descendant-concept of \"" + root + "\"");
		}
	}
}
