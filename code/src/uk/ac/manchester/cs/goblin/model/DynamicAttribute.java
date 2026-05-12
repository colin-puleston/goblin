package uk.ac.manchester.cs.goblin.model;

import java.util.*;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public class DynamicAttribute extends Attribute {

	private AttributeId attributeId;

	private List<DynamicAttributeListener> listeners = new ArrayList<DynamicAttributeListener>();

	private class AttributeId extends ModelEditTarget {

		final EntityId id;

		public void doAdd(boolean replacement) {

			attributeId = this;

			onAttributeIdReset();
		}

		public void doRemove(boolean replacing) {
		}

		public Concept getEditTargetConcept() {

			return getRootSourceConcept();
		}

		Attribute getEditTargetAttributeOrNull() {

			return DynamicAttribute.this;
		}

		AttributeId(EntityId id) {

			this.id = id;
		}
	}

	private class ReplaceAttributeIdAction extends ReplaceAction<AttributeId> {

		protected void performInterSubActionUpdates(AttributeId target1, AttributeId target2) {
		}

		ReplaceAttributeIdAction(AttributeId removeTarget, AttributeId addTarget) {

			super(removeTarget, addTarget);
		}
	}

	public void resetAttributeId(EntityId attrId) {

		Concept source = getRootSourceConcept();

		if (source.applicableDynamicAttribute(attrId)) {

			throw new RuntimeException("Attribute already exists for concept: " + source);
		}

		performAction(createReplaceAttributeIdAction(attrId));
	}

	public void addListener(DynamicAttributeListener listener) {

		listeners.add(listener);
	}

	public void removeListener(DynamicAttributeListener listener) {

		listeners.remove(listener);
	}

	public void removeListenersOfType(Class<? extends DynamicAttributeListener> type) {

		for (DynamicAttributeListener listener : copyListeners()) {

			if (type.isAssignableFrom(listener.getClass())) {

				listeners.remove(listener);
			}
		}
	}

	public void remove() {

		performAction(createRemoveAction());
	}

	public boolean dynamicAttribute() {

		return true;
	}

	public String getLabel() {

		return attributeId.id.getLabel();
	}

	public EntityId getAttributeId() {

		return attributeId.id;
	}

	public ConstraintsOption getConstraintsOption() {

		return getRootSourceConcept().getHierarchy().getDynamicAttributeConstraintsOption();
	}

	public boolean currentlyActive() {

		return getRootSourceConcept().hasDynamicAttribute(this);
	}

	DynamicAttribute(EntityId attrId, Concept rootSourceConcept, Concept rootTargetConcept) {

		super(rootSourceConcept, rootTargetConcept);

		attributeId = new AttributeId(attrId);
	}

	boolean add() {

		performAction(new AddAction(this));

		return true;
	}

	public void doAdd(boolean replacement) {

		getRootSourceConcept().addDynamicAttribute(this);
	}

	public void doRemove(boolean replacing) {

		getRootSourceConcept().removeDynamicAttribute(this);
	}

	private ReplaceAttributeIdAction createReplaceAttributeIdAction(EntityId newId) {

		return new ReplaceAttributeIdAction(attributeId, new AttributeId(newId));
	}

	private EditAction createRemoveAction() {

		EditAction action = new RemoveAction(this);

		List<Constraint> constraints = getConstraintsDownwards();

		constraints.remove(getRootConstraint());

		if (!constraints.isEmpty()) {

			action = incorporateConstraintRemovalEdits(action, constraints);
		}

		return action;
	}

	private EditAction incorporateConstraintRemovalEdits(
							EditAction action,
							List<Constraint> constraints) {

		CompoundEditAction compoundAction = new CompoundEditAction();

		for (Constraint constraint : constraints) {

			compoundAction.addSubAction(new RemoveAction(constraint));
		}

		compoundAction.addSubAction(action);

		return compoundAction;
	}

	private List<Constraint> getConstraintsDownwards() {

		return getRootSourceConcept().getConstraintsDownwards(this);
	}

	private void performAction(EditAction action) {

		getModel().getEditActions().perform(action);
	}

	private void onAttributeIdReset() {

		for (DynamicAttributeListener listener : copyListeners()) {

			listener.onAttributeIdReset();
		}
	}

	private List<DynamicAttributeListener> copyListeners() {

		return new ArrayList<DynamicAttributeListener>(listeners);
	}
}
