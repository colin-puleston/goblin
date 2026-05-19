package uk.ac.manchester.cs.goblin.model;

import java.util.*;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public class DynamicAttribute extends Attribute {

	private AttributeId attributeId;

	private class AttributeId extends EditableId<EditableIdListener> {

		AttributeId(EntityId id) {

			super(id, getEditActions(), new ArrayList<EditableIdListener>());
		}

		EditLocation createEditLocation() {

			return new ModelEditLocation(DynamicAttribute.this);
		}
	}

	private class AddRemoveTarget implements EditTarget {

		public void doAdd() {

			getRootSourceConcept().addDynamicAttribute(DynamicAttribute.this);
		}

		public void doRemove() {

			getRootSourceConcept().removeDynamicAttribute(DynamicAttribute.this);
		}

		public EditLocation createLocation(boolean postRemovalOp) {

			return postRemovalOp
					? new ModelEditLocation(getRootSourceConcept())
					: new ModelEditLocation(DynamicAttribute.this);
		}
	}

	public void resetAttributeId(EntityId attrId) {

		Concept source = getRootSourceConcept();

		if (source.applicableDynamicAttribute(attrId)) {

			throw new RuntimeException("Attribute already exists for concept: " + source);
		}

		attributeId.resetId(attrId);
	}

	public void addIdListener(EditableIdListener listener) {

		attributeId.addListener(listener);
	}

	public void removeIdListener(EditableIdListener listener) {

		attributeId.removeListener(listener);
	}

	public void removeIdListenersOfType(Class<? extends EditableIdListener> type) {

		attributeId.removeListenersOfType(type);
	}

	public void remove() {

		performAction(createRemoveAction());
	}

	public boolean dynamicAttribute() {

		return true;
	}

	public String getLabel() {

		return attributeId.getId().getLabel();
	}

	public EntityId getAttributeId() {

		return attributeId.getId();
	}

	public ConstraintsOption getConstraintsOption() {

		return getRootSourceConcept().getHierarchy().getDynamicAttributeConstraintsOption();
	}

	public boolean currentlyActive() {

		return getRootSourceConcept().hasDynamicAttribute(this);
	}

	DynamicAttribute(EntityId attributeId, Concept rootSourceConcept, Concept rootTargetConcept) {

		super(rootSourceConcept, rootTargetConcept);

		this.attributeId = new AttributeId(attributeId);
	}

	boolean add() {

		performAction(new AddAction(new AddRemoveTarget()));

		return true;
	}

	private EditAction createRemoveAction() {

		EditAction action = new RemoveAction(new AddRemoveTarget());

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

			compoundAction.addSubAction(constraint.createRemoveAction());
		}

		compoundAction.addSubAction(action);

		return compoundAction;
	}

	private List<Constraint> getConstraintsDownwards() {

		return getRootSourceConcept().getConstraintsDownwards(this);
	}

	private void performAction(EditAction action) {

		getEditActions().perform(action);
	}

	private EditActions<?> getEditActions() {

		return getModel().getEditActions();
	}
}
