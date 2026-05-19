package uk.ac.manchester.cs.goblin.model;

import java.util.*;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public class DynamicAttribute extends Attribute {

	private EntityId attributeId;
	private List<EditableIdListener> idListeners = new ArrayList<EditableIdListener>();

	private abstract class DynamicAttributeEditTarget extends ModelEditTarget {

		Hierarchy getEditedHierarchy() {

			return getRootSourceConcept().getHierarchy();
		}

		Concept getEditedConceptOrNull(boolean postRemovalOp) {

			return getRootSourceConcept();
		}
	}

	private class IdUpdateTarget extends DynamicAttributeEditTarget {

		private EntityId id;

		public void doAdd() {

			attributeId = id;

			onIdUpdate();
		}

		public void doRemove() {
		}

		IdUpdateTarget(EntityId id) {

			this.id = id;
		}

		Attribute getEditedAttributeOrNull(boolean postRemovalOp) {

			return DynamicAttribute.this;
		}
	}

	private class AddRemoveTarget extends DynamicAttributeEditTarget {

		public void doAdd() {

			getRootSourceConcept().addDynamicAttribute(DynamicAttribute.this);
		}

		public void doRemove() {

			getRootSourceConcept().removeDynamicAttribute(DynamicAttribute.this);
		}

		Attribute getEditedAttributeOrNull(boolean postRemovalOp) {

			return postRemovalOp ? null : DynamicAttribute.this;
		}
	}

	public void resetAttributeId(EntityId attrId) {

		Concept source = getRootSourceConcept();

		if (source.applicableDynamicAttribute(attrId)) {

			throw new RuntimeException("Attribute already exists for concept: " + source);
		}

		performAction(createReplaceAttributeIdAction(attrId));
	}

	public void addIdListener(EditableIdListener listener) {

		idListeners.add(listener);
	}

	public void removeIdListener(EditableIdListener listener) {

		idListeners.remove(listener);
	}

	public void removeIdListenersOfType(Class<? extends EditableIdListener> type) {

		for (EditableIdListener listener : copyIdListeners()) {

			if (type.isAssignableFrom(listener.getClass())) {

				idListeners.remove(listener);
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

		return attributeId.getLabel();
	}

	public EntityId getAttributeId() {

		return attributeId;
	}

	public ConstraintsOption getConstraintsOption() {

		return getRootSourceConcept().getHierarchy().getDynamicAttributeConstraintsOption();
	}

	public boolean currentlyActive() {

		return getRootSourceConcept().hasDynamicAttribute(this);
	}

	DynamicAttribute(EntityId attributeId, Concept rootSourceConcept, Concept rootTargetConcept) {

		super(rootSourceConcept, rootTargetConcept);

		this.attributeId = attributeId;
	}

	boolean add() {

		performAction(new AddAction(new AddRemoveTarget()));

		return true;
	}

	private ReplaceAction<IdUpdateTarget> createReplaceAttributeIdAction(EntityId newId) {

		return new ReplaceAction<IdUpdateTarget>(
						new IdUpdateTarget(attributeId),
						new IdUpdateTarget(newId));
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

		getModel().getEditActions().perform(action);
	}

	private void onIdUpdate() {

		for (EditableIdListener listener : copyIdListeners()) {

			listener.onIdUpdate();
		}
	}

	private List<EditableIdListener> copyIdListeners() {

		return new ArrayList<EditableIdListener>(idListeners);
	}
}
