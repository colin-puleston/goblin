package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class Concept extends EditTarget {

	static public boolean allSubsumed(
							Collection<Concept> testSubsumers,
							Collection<Concept> testSubsumeds) {

		for (Concept testSubsumed : testSubsumeds) {

			if (!testSubsumed.subsumedByAny(testSubsumers)) {

				return false;
			}
		}

		return true;
	}

	private Hierarchy hierarchy;

	private ConceptId conceptId;

	private ConceptTracker parent;
	private ConceptTrackerSet children = new ConceptTrackerSet();

	private AttributeTrackerSet dynamicAttributes = new AttributeTrackerSet();
	private AttributeTrackerSet inwardDynamicAttributes = new AttributeTrackerSet();

	private ConstraintTrackerSet constraints = new ConstraintTrackerSet();
	private ConstraintTrackerSet inwardConstraints = new ConstraintTrackerSet();

	private List<ConceptListener> listeners = new ArrayList<ConceptListener>();

	private class ConceptId extends EditTarget {

		final EntityId id;

		ConceptId(EntityId id) {

			this.id = id;
		}

		void doAdd(boolean replacement) {

			conceptId = this;

			onIdReset();
		}

		void doRemove(boolean replacing) {
		}

		Concept getEditTargetConcept() {

			return Concept.this;
		}
	}

	private class ReplaceConceptIdAction extends ReplaceAction<ConceptId> {

		ReplaceConceptIdAction(ConceptId removeTarget, ConceptId addTarget) {

			super(removeTarget, addTarget);
		}

		void performInterSubActionUpdates(ConceptId target1, ConceptId target2) {
		}
	}

	private class ConstraintMatcher {

		private Attribute attribute;
		private boolean inwards;

		private ConstraintSemantics semantics = null;
		private List<Concept> targetValues = null;

		ConstraintMatcher(Attribute attribute, boolean inwards) {

			this.attribute = attribute;
			this.inwards = inwards;
		}

		void setMatchSemantics(ConstraintSemantics semantics) {

			this.semantics = semantics;
		}

		void setMatchTargetValues(Collection<Concept> targetValues) {

			this.targetValues = new ArrayList<Concept>(targetValues);
		}

		boolean anyMatches() {

			return !findMatches(true).isEmpty();
		}

		List<Constraint> getAll() {

			return findMatches(false);
		}

		Constraint getOneOrZero() {

			List<Constraint> matches = findMatches(true);

			return matches.isEmpty() ? null : matches.iterator().next();
		}

		private List<Constraint> findMatches(boolean maxOne) {

			List<Constraint> selections = new ArrayList<Constraint>();

			for (Constraint candidate : getCandidates()) {

				if (match(candidate)) {

					selections.add(candidate);

					if (maxOne) {

						break;
					}
				}
			}

			return selections;
		}

		private List<Constraint> getCandidates() {

			return (inwards ? inwardConstraints : constraints).getEntities();
		}

		private boolean match(Constraint candidate) {

			return candidate.onAttribute(attribute)
					&& checkSemanticsMatch(candidate)
					&& checkTargetValuesMatch(candidate);
		}

		private boolean checkSemanticsMatch(Constraint candidate) {

			return semantics == null || candidate.hasSemantics(semantics);
		}

		private boolean checkTargetValuesMatch(Constraint candidate) {

			return targetValues == null || candidate.getTargetValues().equals(targetValues);
		}
	}

	public void addListener(ConceptListener listener) {

		listeners.add(listener);
	}

	public void removeListener(ConceptListener listener) {

		listeners.remove(listener);
	}

	public boolean resetId(EntityId newConceptId) {

		checkCanPerformOperation(canResetId());

		if (!getConceptId().equals(newConceptId)) {

			if (getModel().containsConcept(newConceptId)) {

				return false;
			}

			performAction(createReplaceIdAction(newConceptId));
		}

		return true;
	}

	public boolean move(Concept newParent) {

		checkCanPerformOperation(canMove());

		EditAction action = checkCreateMoveAction(newParent);

		if (action != null) {

			performAction(action);

			return true;
		}

		return false;
	}

	public void remove() {

		checkCanPerformOperation(canMove());

		performAction(createRemoveAction());
	}

	public Concept addChild(EntityId id) {

		Concept child = createChild(id);

		child.setParent(this);
		child.add();

		return child;
	}

	public DynamicAttribute addDynamicAttribute(EntityId attrId, EntityId rootTargetConceptId) {

		Hierarchy targets = getModel().createDynamicValueHierarchy(rootTargetConceptId);

		return addDynamicAttribute(attrId, targets.getRootConcept());
	}

	public DynamicAttribute addDynamicAttribute(EntityId attrId, Concept rootTargetConcept) {

		if (!applicableDynamicAttribute(attrId)) {

			DynamicAttribute attribute = new DynamicAttribute(attrId, this, rootTargetConcept);

			if (attribute.add()) {

				return attribute;
			}
		}

		return null;
	}

	public boolean addValidValuesConstraint(Attribute attribute, Collection<Concept> targetValues) {

		if (constraintExists(attribute, ConstraintSemantics.VALID_VALUES, targetValues)) {

			return false;
		}

		return attribute.createValidValues(this, targetValues).add();
	}

	public boolean addImpliedValueConstraint(Attribute attribute, Concept targetValue) {

		if (constraintExists(attribute, ConstraintSemantics.IMPLIED_VALUE, targetValue)) {

			return false;
		}

		return attribute.createImpliedValue(this, targetValue).add();
	}

	public String toString() {

		return conceptId.id.toString();
	}

	public Model getModel() {

		return hierarchy.getModel();
	}

	public Hierarchy getHierarchy() {

		return hierarchy;
	}

	public EntityId getConceptId() {

		return conceptId.id;
	}

	public boolean isRoot() {

		return parent == null;
	}

	public boolean isLeaf() {

		return children.isEmpty();
	}

	public abstract boolean coreConcept();

	public abstract boolean canResetId();

	public abstract boolean canMove();

	public Concept getParent() {

		if (parent == null) {

			throw new RuntimeException("Cannot perform operation on root concept!");
		}

		return parent.getEntity();
	}

	public List<Concept> getParents() {

		return Collections.singletonList(getParent());
	}

	public List<Concept> getChildren() {

		return children.getEntities();
	}

	public boolean subsumedBy(Concept testSubsumer) {

		return equals(testSubsumer) || descendantOf(testSubsumer);
	}

	public boolean subsumedByAny(Collection<Concept> testSubsumers) {

		for (Concept testSubsumer : testSubsumers) {

			if (subsumedBy(testSubsumer)) {

				return true;
			}
		}

		return false;
	}

	public boolean descendantOf(Concept testAncestor) {

		return getParent().equals(testAncestor) || getParent().descendantOf(testAncestor);
	}

	public List<Attribute> getApplicableAttributes() {

		List<Attribute> attributes = new ArrayList<Attribute>();

		attributes.addAll(hierarchy.getCoreAttributes());
		collectDynamicAttributesUpwards(attributes);

		return attributes;
	}

	public List<Attribute> getApplicableInwardAttributes() {

		List<Attribute> attributes = new ArrayList<Attribute>();

		attributes.addAll(hierarchy.getInwardCoreAttributes());
		collectInwardDynamicAttributesUpwards(attributes);

		return attributes;
	}

	public boolean applicableDynamicAttribute(EntityId attrId) {

		for (Attribute attribute : dynamicAttributes.getEntities()) {

			if (((DynamicAttribute)attribute).getAttributeId().equals(attrId)) {

				return true;
			}
		}

		if (isRoot()) {

			return false;
		}

		return getParent().applicableDynamicAttribute(attrId);
	}

	public List<Constraint> getConstraints() {

		return constraints.getEntities();
	}

	public List<Constraint> getConstraints(Attribute attribute) {

		return new ConstraintMatcher(attribute, false).getAll();
	}

	public Constraint lookForConstraint(Attribute attribute, ConstraintSemantics semantics) {

		ConstraintMatcher matcher = new ConstraintMatcher(attribute, false);

		matcher.setMatchSemantics(semantics);

		return matcher.getOneOrZero();
	}

	public Constraint lookForValidValuesConstraint(Attribute attribute) {

		return lookForConstraint(attribute, ConstraintSemantics.VALID_VALUES);
	}

	public Constraint lookForImpliedValueConstraint(Attribute attribute) {

		return lookForConstraint(attribute, ConstraintSemantics.IMPLIED_VALUE);
	}

	public List<Constraint> getImpliedValueConstraints(Attribute attribute) {

		ConstraintMatcher matcher = new ConstraintMatcher(attribute, false);

		matcher.setMatchSemantics(ConstraintSemantics.IMPLIED_VALUE);

		return matcher.getAll();
	}

	public Constraint getClosestValidValuesConstraint(Attribute attribute) {

		Constraint sub = lookForValidValuesConstraint(attribute);

		return sub != null ? sub : getClosestAncestorValidValuesConstraint(attribute);
	}

	public Constraint getClosestAncestorValidValuesConstraint(Attribute attribute) {

		return getParent().getClosestValidValuesConstraint(attribute);
	}

	public List<Constraint> getInwardConstraints() {

		return inwardConstraints.getEntities();
	}

	public List<Constraint> getInwardConstraints(Attribute attribute) {

		return new ConstraintMatcher(attribute, true).getAll();
	}

	Concept(Hierarchy hierarchy, EntityId conceptId) {

		this.hierarchy = hierarchy;
		this.conceptId = new ConceptId(conceptId);
	}

	Concept(Concept replaced, Concept newParent) {

		hierarchy = replaced.hierarchy;
		conceptId = new ConceptId(replaced.conceptId.id);
		children = replaced.children.copy();
		constraints = replaced.constraints.copy();
		inwardConstraints = replaced.inwardConstraints.copy();

		parent = newParent.toTracker();
	}

	EditAction checkCreateMoveAction(Concept newParent) {

		ConflictResolution conflictRes = checkMoveConflicts(newParent);

		if (conflictRes.resolvable()) {

			Concept replacement = createMovedReplacement(newParent);
			EditAction action = new ReplaceConceptAction(this, replacement);

			return conflictRes.incorporateResolvingEdits(action);
		}

		return null;
	}

	EditAction createRemoveAction() {

		EditAction action = new RemoveAction(this);

		if (!inwardConstraints.isEmpty()) {

			action = incorporateInwardTargetRemovalEdits(action);
		}

		return action;
	}

	void doAdd(boolean replacement) {

		Concept parent = getParent();

		parent.children.add(this);
		parent.onChildAdded(this, replacement);
	}

	void doRemove(boolean replacing) {

		Concept parent = getParent();

		parent.children.remove(this);
		onConceptRemoved(replacing);

		removeAllSubTreeListeners();
	}

	void addDynamicAttribute(DynamicAttribute attribute) {

		dynamicAttributes.add(attribute);

		attribute.getRootTargetConcept().inwardDynamicAttributes.add(attribute);

		addConstraint(attribute.createRootConstraint());

		hierarchy.onAddedDynamicAttribute(attribute);
	}

	void removeDynamicAttribute(DynamicAttribute attribute) {

		dynamicAttributes.remove(attribute);

		attribute.getRootTargetConcept().inwardDynamicAttributes.remove(attribute);

		removeAllConstraintsForDynamicAttribute(attribute);

		hierarchy.onRemovedDynamicAttribute(attribute);
	}

	void addConstraint(Constraint constraint) {

		ConstraintTracker tracker = constraints.add(constraint);

		onConstraintAdded(constraint, false);

		for (Concept target : constraint.getTargetValues()) {

			target.inwardConstraints.add(tracker);
			target.onConstraintAdded(constraint, true);
		}
	}

	void removeConstraint(Constraint constraint) {

		ConstraintTracker tracker = constraints.remove(constraint);

		onConstraintRemoved(constraint, false);

		for (Concept target : constraint.getTargetValues()) {

			target.inwardConstraints.remove(tracker);
			target.onConstraintRemoved(constraint, true);
		}
	}

	ConceptTracker toTracker() {

		return getModel().getConceptTracking().toTracker(this);
	}

	Concept getEditTargetConcept() {

		return this;
	}

	List<Attribute> getDynamicAttributesDownwards() {

		List<Attribute> attributes = new ArrayList<Attribute>();

		collectDynamicAttributesDownwards(attributes);

		return attributes;
	}

	List<Constraint> getConstraintsDownwards(Attribute attribute) {

		List<Constraint> constraints = new ArrayList<Constraint>();

		collectConstraintsDownwards(attribute, constraints);

		return constraints;
	}

	abstract Concept createMovedReplacement(Concept newParent);

	RuntimeException createInvalidOperationException() {

		return new RuntimeException("Cannot perform operation on this concept!");
	}

	private void add() {

		performAction(new AddAction(this));
	}

	private Concept createChild(EntityId id) {

		if (id.dynamicId()) {

			if (hierarchy.referenceOnly()) {

				throw createInvalidOperationException();
			}

			return new NonRootDynamicConcept(hierarchy, id);
		}

		return new NonRootCoreConcept(hierarchy, id);
	}

	private void setParent(Concept parent) {

		this.parent = parent.toTracker();
	}

	private ReplaceConceptIdAction createReplaceIdAction(EntityId newId) {

		return new ReplaceConceptIdAction(conceptId, new ConceptId(newId));
	}

	private EditAction incorporateInwardTargetRemovalEdits(EditAction action) {

		CompoundEditAction cpmd = new CompoundEditAction();

		for (Constraint constraint : inwardConstraints.getEntities()) {

			cpmd.addSubAction(constraint.createTargetValueRemovalEditAction(this));
		}

		cpmd.addSubAction(action);

		return cpmd;
	}

	private ConflictResolution checkMoveConflicts(Concept newParent) {

		ConceptTracker savedParent = parent;

		setParent(newParent);

		ConflictResolution conflicts = checkMovedConflicts();

		parent = savedParent;

		return conflicts;
	}

	private ConflictResolution checkMovedConflicts() {

		return getModel().getConflictResolver().checkConceptMove(this);
	}

	private void performAction(EditAction action) {

		getModel().getEditActions().perform(action);
	}

	private void removeAllConstraintsForDynamicAttribute(Attribute attribute) {

		for (Constraint constraint : getConstraints(attribute)) {

			removeConstraint(constraint);
		}

		for (Concept sub : getChildren()) {

			sub.removeAllConstraintsForDynamicAttribute(attribute);
		}
	}

	private void removeAllSubTreeListeners() {

		listeners.clear();

		for (Concept sub : getChildren()) {

			sub.removeAllSubTreeListeners();
		}
	}

	private void collectDynamicAttributesUpwards(List<Attribute> attributes) {

		if (!isRoot()) {

			getParent().collectDynamicAttributesUpwards(attributes);
		}

		attributes.addAll(dynamicAttributes.getEntities());
	}

	private void collectInwardDynamicAttributesUpwards(List<Attribute> attributes) {

		if (!isRoot()) {

			getParent().collectInwardDynamicAttributesUpwards(attributes);
		}

		attributes.addAll(inwardDynamicAttributes.getEntities());
	}

	private void collectDynamicAttributesDownwards(List<Attribute> attributes) {

		attributes.addAll(dynamicAttributes.getEntities());

		for (Concept child : getChildren()) {

			child.collectDynamicAttributesDownwards(attributes);
		}
	}

	private void collectConstraintsDownwards(Attribute attribute, List<Constraint> constraints) {

		constraints.addAll(getConstraints(attribute));

		for (Concept child : getChildren()) {

			child.collectConstraintsDownwards(attribute, constraints);
		}
	}

	private boolean constraintExists(
						Attribute attribute,
						ConstraintSemantics semantics,
						Concept targetValue) {

		return constraintExists(attribute, semantics, Collections.singleton(targetValue));
	}

	private boolean constraintExists(
						Attribute attribute,
						ConstraintSemantics semantics,
						Collection<Concept> targetValues) {

		ConstraintMatcher matcher = new ConstraintMatcher(attribute, false);

		matcher.setMatchSemantics(semantics);
		matcher.setMatchTargetValues(targetValues);

		return matcher.anyMatches();
	}

	private void onIdReset() {

		for (ConceptListener listener : copyListeners()) {

			listener.onIdReset(this);
		}
	}

	private void onChildAdded(Concept child, boolean replacement) {

		hierarchy.registerConcept(child);

		for (ConceptListener listener : copyListeners()) {

			listener.onChildAdded(child, replacement);
		}
	}

	private void onConstraintAdded(Constraint constraint, boolean inward) {

		for (ConceptListener listener : copyListeners()) {

			listener.onConstraintAdded(constraint, inward);
		}
	}

	private void onConstraintRemoved(Constraint constraint, boolean inward) {

		for (ConceptListener listener : copyListeners()) {

			listener.onConstraintRemoved(constraint, inward);
		}
	}

	private void onConceptRemoved(boolean replacing) {

		hierarchy.deregisterConcept(this);

		for (ConceptListener listener : copyListeners()) {

			listener.onConceptRemoved(this, replacing);
		}
	}

	private List<ConceptListener> copyListeners() {

		return new ArrayList<ConceptListener>(listeners);
	}

	private void checkCanPerformOperation(boolean canDo) {

		if (!canDo) {

			throw createInvalidOperationException();
		}
	}
}
