package uk.ac.manchester.cs.goblin.io;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class PropertyAttribute extends Attribute {

	private String label;
	private EntityId targetPropertyId;

	private boolean definesValidValues = true;
	private boolean definesImpliedValues = false;

	private boolean singleImpliedValues = false;

	public String getLabel() {

		return label;
	}

	public boolean definesValidValues() {

		return definesValidValues;
	}

	public boolean definesImpliedValues() {

		return definesImpliedValues;
	}

	public boolean singleImpliedValues() {

		return singleImpliedValues;
	}

	PropertyAttribute(
		String label,
		EntityId targetPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept) {

		super(rootSourceConcept, rootTargetConcept);

		this.label = label;
		this.targetPropertyId = targetPropertyId;
	}

	void setSemanticsOptions(Set<ConstraintSemantics> options) {

		definesValidValues = options.contains(ConstraintSemantics.VALID_VALUES);
		definesImpliedValues = options.contains(ConstraintSemantics.IMPLIED_VALUE);
	}

	void setSingleImpliedValues(boolean value) {

		singleImpliedValues = value;
	}

	EntityId getTargetPropertyId() {

		return targetPropertyId;
	}

	abstract Collection<EntityId> getInvolvedPropertyIds();
}
