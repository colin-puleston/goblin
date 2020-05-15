package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class ConstraintType {

	private String name;
	private Concept rootSourceConcept;
	private Concept rootTargetConcept;

	private CardinalityType cardinalityType = CardinalityType.SINGLE_VALUE;

	private Set<ConstraintSemantics> semanticsOptions
				= Collections.singleton(ConstraintSemantics.VALID_VALUES);

	public void setCardinalityType(CardinalityType cardinalityType) {

		this.cardinalityType = cardinalityType;
	}

	public void setSemanticsOptions(Set<ConstraintSemantics> semanticsOptions) {

		this.semanticsOptions = new HashSet<ConstraintSemantics>(semanticsOptions);
	}

	public String getName() {

		return name;
	}

	public Concept getRootSourceConcept() {

		return rootSourceConcept;
	}

	public Concept getRootTargetConcept() {

		return rootTargetConcept;
	}

	public boolean singleValue() {

		return cardinalityType.singleValue();
	}

	public boolean semanticsOption(ConstraintSemantics semantics) {

		return semanticsOptions.contains(semantics);
	}

	protected ConstraintType(String name, Concept rootSourceConcept, Concept rootTargetConcept) {

		this.name = name;
		this.rootSourceConcept = rootSourceConcept;
		this.rootTargetConcept = rootTargetConcept;
	}

	Constraint createRootConstraint() {

		return new ValidValuesConstraint(this, rootSourceConcept, rootTargetConcept);
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
