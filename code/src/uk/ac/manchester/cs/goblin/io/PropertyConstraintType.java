package uk.ac.manchester.cs.goblin.io;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class PropertyConstraintType extends ConstraintType {

	private EntityId targetPropertyId;

	private boolean definesValidValues = true;
	private boolean definesImpliedValues = false;

	private boolean singleImpliedValues = false;

	public boolean definesValidValues() {

		return definesValidValues;
	}

	public boolean definesImpliedValues() {

		return definesImpliedValues;
	}

	public boolean singleImpliedValues() {

		return singleImpliedValues;
	}

	PropertyConstraintType(
		String name,
		EntityId targetPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept) {

		super(name, rootSourceConcept, rootTargetConcept);

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
}
