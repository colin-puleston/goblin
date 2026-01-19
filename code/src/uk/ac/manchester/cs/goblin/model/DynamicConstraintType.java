package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class DynamicConstraintType extends ConstraintType {

	private AttributeId attributeId;

	private List<DynamicConstraintTypeListener> listeners = new ArrayList<DynamicConstraintTypeListener>();

	private class AttributeId extends EditTarget {

		final EntityId id;

		AttributeId(EntityId id) {

			this.id = id;
		}

		void doAdd(boolean replacement) {

			attributeId = this;

			onAttributeIdReset();
		}

		void doRemove(boolean replacing) {
		}

		Concept getEditTargetConcept() {

			return getRootSourceConcept();
		}
	}

	private class ReplaceAttributeIdAction extends ReplaceAction<AttributeId> {

		ReplaceAttributeIdAction(AttributeId removeTarget, AttributeId addTarget) {

			super(removeTarget, addTarget);
		}

		void performInterSubActionUpdates(AttributeId target1, AttributeId target2) {
		}
	}

	public void addListener(DynamicConstraintTypeListener listener) {

		listeners.add(listener);
	}

	public void removeListener(DynamicConstraintTypeListener listener) {

		listeners.remove(listener);
	}

	public void removeTypeListeners(Class<? extends DynamicConstraintTypeListener> removeType) {

		for (DynamicConstraintTypeListener listener : copyListeners()) {

			if (removeType.isAssignableFrom(listener.getClass())) {

				listeners.remove(listener);
			}
		}
	}

	public void resetAttributeId(EntityId attrId) {

		Concept source = getRootSourceConcept();

		if (source.applicableDynamicConstraintType(attrId)) {

			throw new RuntimeException("Attribute already exists for concept: " + source);
		}

		performAction(createReplaceAttributeIdAction(attrId));
	}

	public void remove() {

		performAction(createRemoveAction());
	}

	public String getName() {

		return attributeId.id.getLabel();
	}

	public EntityId getAttributeId() {

		return attributeId.id;
	}

	public boolean dynamicConstraintType() {

		return true;
	}

	public boolean definesValidValues() {

		return true;
	}

	public boolean definesImpliedValues() {

		return false;
	}

	public boolean singleImpliedValues() {

		return false;
	}

	DynamicConstraintType(EntityId attrId, Concept rootSourceConcept, Concept rootTargetConcept) {

		super(rootSourceConcept, rootTargetConcept);

		attributeId = new AttributeId(attrId);
	}

	boolean add() {

		performAction(new AddAction(this));

		return true;
	}

	void doAdd(boolean replacement) {

		getRootSourceConcept().addDynamicConstraintType(this);
	}

	void doRemove(boolean replacing) {

		getRootSourceConcept().removeDynamicConstraintType(this);
	}

	private ReplaceAttributeIdAction createReplaceAttributeIdAction(EntityId newId) {

		return new ReplaceAttributeIdAction(attributeId, new AttributeId(newId));
	}

	private EditAction createRemoveAction() {

		EditAction action = new RemoveAction(this);

		List<Constraint> constraints = getAllConstraintsOfType();

		if (!constraints.isEmpty()) {

			action = incorporateConstraintRemovalEdits(action, constraints);
		}

		return action;
	}

	private EditAction incorporateConstraintRemovalEdits(
							EditAction action,
							List<Constraint> constraints) {

		CompoundEditAction cpmd = new CompoundEditAction();

		for (Constraint constraint : constraints) {

			cpmd.addSubAction(new RemoveAction(constraint));
		}

		cpmd.addSubAction(action);

		return cpmd;
	}

	private List<Constraint> getAllConstraintsOfType() {

		return getRootSourceConcept().getConstraintsDownwards(this);
	}

	private void performAction(EditAction action) {

		getModel().getEditActions().perform(action);
	}

	private void onAttributeIdReset() {

		for (DynamicConstraintTypeListener listener : copyListeners()) {

			listener.onAttributeIdReset();
		}
	}

	private List<DynamicConstraintTypeListener> copyListeners() {

		return new ArrayList<DynamicConstraintTypeListener>(listeners);
	}
}
