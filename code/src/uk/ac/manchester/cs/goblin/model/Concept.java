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

	private ConstraintTypeTrackerSet dynamicConstraintTypes = new ConstraintTypeTrackerSet();
	private ConstraintTypeTrackerSet inwardDynamicConstraintTypes = new ConstraintTypeTrackerSet();

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

		private ConstraintType type;
		private boolean inwards;

		private ConstraintSemantics semantics = null;
		private List<Concept> targetValues = null;

		ConstraintMatcher(ConstraintType type, boolean inwards) {

			this.type = type;
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

			return candidate.hasType(type)
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

	public boolean addDynamicConstraintType(EntityId attrId, EntityId rootTargetConceptId) {

		Hierarchy targets = getModel().createDynamicValueHierarchy(rootTargetConceptId);

		return addDynamicConstraintType(attrId, targets.getRootConcept());
	}

	public boolean addDynamicConstraintType(EntityId attrId, Concept rootTargetConcept) {

		if (applicableDynamicConstraintType(attrId)) {

			return false;
		}

		return new DynamicConstraintType(attrId, this, rootTargetConcept).add();
	}

	public boolean addValidValuesConstraint(ConstraintType type, Collection<Concept> targetValues) {

		if (constraintExists(type, ConstraintSemantics.VALID_VALUES, targetValues)) {

			return false;
		}

		return type.createValidValues(this, targetValues).add();
	}

	public boolean addImpliedValueConstraint(ConstraintType type, Concept targetValue) {

		if (constraintExists(type, ConstraintSemantics.IMPLIED_VALUE, targetValue)) {

			return false;
		}

		return type.createImpliedValue(this, targetValue).add();
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

	public List<ConstraintType> getApplicableConstraintTypes() {

		List<ConstraintType> types = new ArrayList<ConstraintType>();

		types.addAll(hierarchy.getCoreConstraintTypes());
		collectDynamicConstraintTypesUpwards(types);

		return types;
	}

	public List<ConstraintType> getApplicableInwardConstraintTypes() {

		List<ConstraintType> types = new ArrayList<ConstraintType>();

		types.addAll(hierarchy.getInwardCoreConstraintTypes());
		collectInwardDynamicConstraintTypesUpwards(types);

		return types;
	}

	public boolean applicableDynamicConstraintType(EntityId attrId) {

		for (ConstraintType type : dynamicConstraintTypes.getEntities()) {

			DynamicConstraintType pType = (DynamicConstraintType)type;

			if (pType.getTargetPropertyId().equals(attrId)) {

				return true;
			}
		}

		if (isRoot()) {

			return false;
		}

		return getParent().applicableDynamicConstraintType(attrId);
	}

	public List<Constraint> getConstraints() {

		return constraints.getEntities();
	}

	public List<Constraint> getConstraints(ConstraintType type) {

		return new ConstraintMatcher(type, false).getAll();
	}

	public Constraint lookForConstraint(ConstraintType type, ConstraintSemantics semantics) {

		ConstraintMatcher matcher = new ConstraintMatcher(type, false);

		matcher.setMatchSemantics(semantics);

		return matcher.getOneOrZero();
	}

	public Constraint lookForValidValuesConstraint(ConstraintType type) {

		return lookForConstraint(type, ConstraintSemantics.VALID_VALUES);
	}

	public Constraint lookForImpliedValueConstraint(ConstraintType type) {

		return lookForConstraint(type, ConstraintSemantics.IMPLIED_VALUE);
	}

	public List<Constraint> getImpliedValueConstraints(ConstraintType type) {

		ConstraintMatcher matcher = new ConstraintMatcher(type, false);

		matcher.setMatchSemantics(ConstraintSemantics.IMPLIED_VALUE);

		return matcher.getAll();
	}

	public Constraint getClosestValidValuesConstraint(ConstraintType type) {

		Constraint sub = lookForValidValuesConstraint(type);

		return sub != null ? sub : getClosestAncestorValidValuesConstraint(type);
	}

	public Constraint getClosestAncestorValidValuesConstraint(ConstraintType type) {

		return getParent().getClosestValidValuesConstraint(type);
	}

	public List<Constraint> getInwardConstraints() {

		return inwardConstraints.getEntities();
	}

	public List<Constraint> getInwardConstraints(ConstraintType type) {

		return new ConstraintMatcher(type, true).getAll();
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

	void addDynamicConstraintType(DynamicConstraintType type) {

		dynamicConstraintTypes.add(type);

		type.getRootTargetConcept().inwardDynamicConstraintTypes.add(type);

		addConstraint(type.createRootConstraint());

		hierarchy.onAddedDynamicConstraintType(type);
	}

	void removeDynamicConstraintType(DynamicConstraintType type) {

		dynamicConstraintTypes.remove(type);

		type.getRootTargetConcept().inwardDynamicConstraintTypes.remove(type);

		removeAllTypeDynamicConstraints(type);

		hierarchy.onRemovedDynamicConstraintType(type);
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

	List<ConstraintType> getDynamicConstraintTypesDownwards() {

		List<ConstraintType> types = new ArrayList<ConstraintType>();

		collectDynamicConstraintTypesDownwards(types);

		return types;
	}

	List<Constraint> getConstraintsDownwards(ConstraintType type) {

		List<Constraint> constraints = new ArrayList<Constraint>();

		collectConstraintsDownwards(type, constraints);

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

	private void removeAllTypeDynamicConstraints(ConstraintType type) {

		for (Constraint constraint : getConstraints(type)) {

			removeConstraint(constraint);
		}

		for (Concept sub : getChildren()) {

			sub.removeAllTypeDynamicConstraints(type);
		}
	}

	private void removeAllSubTreeListeners() {

		listeners.clear();

		for (Concept sub : getChildren()) {

			sub.removeAllSubTreeListeners();
		}
	}

	private void collectDynamicConstraintTypesUpwards(List<ConstraintType> types) {

		if (!isRoot()) {

			getParent().collectDynamicConstraintTypesUpwards(types);
		}

		types.addAll(dynamicConstraintTypes.getEntities());
	}

	private void collectInwardDynamicConstraintTypesUpwards(List<ConstraintType> types) {

		if (!isRoot()) {

			getParent().collectInwardDynamicConstraintTypesUpwards(types);
		}

		types.addAll(inwardDynamicConstraintTypes.getEntities());
	}

	private void collectDynamicConstraintTypesDownwards(List<ConstraintType> types) {

		types.addAll(dynamicConstraintTypes.getEntities());

		for (Concept child : getChildren()) {

			child.collectDynamicConstraintTypesDownwards(types);
		}
	}

	private void collectConstraintsDownwards(ConstraintType type, List<Constraint> constraints) {

		constraints.addAll(getConstraints(type));

		for (Concept child : getChildren()) {

			child.collectConstraintsDownwards(type, constraints);
		}
	}

	private boolean constraintExists(
						ConstraintType type,
						ConstraintSemantics semantics,
						Concept targetValue) {

		return constraintExists(type, semantics, Collections.singleton(targetValue));
	}

	private boolean constraintExists(
						ConstraintType type,
						ConstraintSemantics semantics,
						Collection<Concept> targetValues) {

		ConstraintMatcher matcher = new ConstraintMatcher(type, false);

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
