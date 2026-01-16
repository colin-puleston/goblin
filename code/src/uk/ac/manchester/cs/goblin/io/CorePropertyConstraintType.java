package uk.ac.manchester.cs.goblin.io;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class CorePropertyConstraintType extends PropertyConstraintType {

	private String name;
	private EntityId targetPropertyId;

	private boolean definesValidValues = true;
	private boolean definesImpliedValues = false;

	private boolean singleImpliedValues = false;

	public void setSemanticsOptions(Set<ConstraintSemantics> options) {

		definesValidValues = options.contains(ConstraintSemantics.VALID_VALUES);
		definesImpliedValues = options.contains(ConstraintSemantics.IMPLIED_VALUE);
	}

	public void setSingleImpliedValues(boolean value) {

		singleImpliedValues = value;
	}

	public String getName() {

		return name;
	}

	public EntityId getTargetPropertyId() {

		return targetPropertyId;
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

	CorePropertyConstraintType(
		String name,
		EntityId targetPropertyId,
		Concept rootSourceConcept,
		Concept rootTargetConcept) {

		super(rootSourceConcept, rootTargetConcept);

		this.name = name;
		this.targetPropertyId = targetPropertyId;
	}

	abstract Collection<EntityId> getInvolvedPropertyIds();
}
