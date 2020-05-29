package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class Concept extends EditTarget {

	static public boolean allSubsumed(Set<Concept> testSubsumers, Set<Concept> testSubsumeds) {

		for (Concept testSubsumed : testSubsumeds) {

			if (!testSubsumed.subsumedByAny(testSubsumers)) {

				return false;
			}
		}

		return true;
	}

	private Hierarchy hierarchy;

	private EntityId conceptId;

	private ConceptTracker parent;
	private ConceptTrackerSet children;

	private ConstraintTrackerSet constraints;
	private ConstraintTrackerSet inwardConstraints;

	private List<ConceptListener> conceptListeners = new ArrayList<ConceptListener>();

	private class ConstraintMatcher {

		private ConstraintType type;
		private ConstraintSemantics semantics = null;
		private Set<Concept> targetValues = null;

		ConstraintMatcher(ConstraintType type) {

			this.type = type;
		}

		void setMatchSemantics(ConstraintSemantics semantics) {

			this.semantics = semantics;
		}

		void setMatchTargetValues(Collection<Concept> targetValues) {

			this.targetValues = new HashSet<Concept>(targetValues);
		}

		boolean anyMatches() {

			return !findMatches(true).isEmpty();
		}

		Set<Constraint> getAll() {

			return findMatches(false);
		}

		Constraint getOneOrZero() {

			Set<Constraint> matches = findMatches(true);

			return matches.isEmpty() ? null : matches.iterator().next();
		}

		private Set<Constraint> findMatches(boolean maxOne) {

			Set<Constraint> selections = new HashSet<Constraint>();

			for (Constraint candidate : getConstraints()) {

				if (match(candidate)) {

					selections.add(candidate);

					if (maxOne) {

						break;
					}
				}
			}

			return selections;
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

		conceptListeners.add(listener);
	}


	public boolean resetId(DynamicId newDynamicId) {

		if (canResetId(newDynamicId)) {

			replace(createCopy(toEntityId(newDynamicId)));

			return true;
		}

		return false;
	}

	public boolean move(Concept newParent) {

		ConflictResolution conflictRes = checkMoveConflicts(newParent);

		if (conflictRes.resolvable()) {

			replace(createCopy(newParent), conflictRes);

			return true;
		}

		return false;
	}

	public void remove() {

		EditAction action = new RemoveAction(this);

		if (!inwardConstraints.isEmpty()) {

			action = incorporateInwardTargetRemovalEdits(action);
		}

		performAction(action);
	}

	public Concept addChild(DynamicId dynamicId) {

		return addChild(toEntityId(dynamicId));
	}

	public Concept addChild(EntityId id) {

		Concept child = createChild(id);

		child.add();

		return child;
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

		return conceptId.toString();
	}

	public Model getModel() {

		return hierarchy.getModel();
	}

	public Hierarchy getHierarchy() {

		return hierarchy;
	}

	public EntityId getConceptId() {

		return conceptId;
	}

	public boolean isRoot() {

		return parent == null;
	}

	public boolean isLeaf() {

		return children.isEmpty();
	}

	public Concept getParent() {

		if (parent == null) {

			throw new RuntimeException("Cannot perform operation on root concept!");
		}

		return parent.getEntity();
	}

	public Set<Concept> getParents() {

		return Collections.singleton(getParent());
	}

	public Set<Concept> getChildren() {

		return children.getEntities();
	}

	public boolean subsumedBy(Concept testSubsumer) {

		return equals(testSubsumer) || descendantOf(testSubsumer);
	}

	public boolean subsumedByAny(Set<Concept> testSubsumers) {

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

	public Set<Constraint> getConstraints() {

		return constraints.getEntities();
	}

	public Set<Constraint> getConstraints(ConstraintType type) {

		return new ConstraintMatcher(type).getAll();
	}

	public Constraint lookForConstraint(ConstraintType type, ConstraintSemantics semantics) {

		ConstraintMatcher matcher = new ConstraintMatcher(type);

		matcher.setMatchSemantics(semantics);

		return matcher.getOneOrZero();
	}

	public Constraint lookForValidValuesConstraint(ConstraintType type) {

		return lookForConstraint(type, ConstraintSemantics.VALID_VALUES);
	}

	public Constraint lookForImpliedValueConstraint(ConstraintType type) {

		return lookForConstraint(type, ConstraintSemantics.IMPLIED_VALUE);
	}

	public Set<Constraint> getImpliedValueConstraints(ConstraintType type) {

		ConstraintMatcher matcher = new ConstraintMatcher(type);

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

	public boolean constraintExists(
						ConstraintType type,
						ConstraintSemantics semantics,
						Concept targetValue) {

		return constraintExists(type, semantics, Collections.singleton(targetValue));
	}

	public boolean constraintExists(
						ConstraintType type,
						ConstraintSemantics semantics,
						Collection<Concept> targetValues) {

		ConstraintMatcher matcher = new ConstraintMatcher(type);

		matcher.setMatchSemantics(semantics);
		matcher.setMatchTargetValues(targetValues);

		return matcher.anyMatches();
	}

	public Set<Constraint> getInwardConstraints() {

		return inwardConstraints.getEntities();
	}

	Concept(Hierarchy hierarchy, EntityId conceptId) {

		this.hierarchy = hierarchy;
		this.conceptId = conceptId;

		Model model = getModel();

		children = new ConceptTrackerSet(model);
		constraints = new ConstraintTrackerSet(model);
		inwardConstraints = new ConstraintTrackerSet(model);
	}

	void add() {

		performAction(new AddAction(this));
	}

	void replace(Concept replacement) {

		replace(replacement, ConflictResolution.NO_CONFLICTS);
	}

	void replace(Concept replacement, ConflictResolution conflictRes) {

		EditAction action = new ReplaceConceptAction(this, replacement);

		performAction(conflictRes.incorporateResolvingEdits(action));
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

	void addRootConstraint(ConstraintType type) {

		constraints.add(type.createRootConstraint());
	}

	void doAddConstraint(Constraint constraint) {

		constraints.add(constraint);
		onConstraintAdded(constraint);
	}

	void doRemoveConstraint(Constraint constraint) {

		constraints.remove(constraint);
		onConstraintRemoved(constraint);
	}

	void addInwardConstraint(Constraint constraint) {

		inwardConstraints.add(constraint);
	}

	void removeInwardConstraint(Constraint constraint) {

		inwardConstraints.remove(constraint);
	}

	Hierarchy getPrimaryEditHierarchy() {

		return hierarchy;
	}

	private Concept(Concept replaced) {

		hierarchy = replaced.hierarchy;
		conceptId = replaced.conceptId;
		parent = replaced.parent.copy();
		children = replaced.children.copy();
		constraints = replaced.constraints.copy();
		inwardConstraints = replaced.inwardConstraints.copy();
	}

	private Concept createChild(EntityId id) {

		Concept child = hierarchy.dynamicHierarchy()
							? new Concept(hierarchy, id)
							: new ReferenceOnlyConcept(hierarchy, id);

		child.setParent(this);

		return child;
	}

	private Concept createCopy(Concept newParent) {

		Concept copy = new Concept(this);

		copy.setParent(newParent);

		return copy;
	}

	private Concept createCopy(EntityId newConceptId) {

		Concept copy = new Concept(this);

		copy.conceptId = newConceptId;

		return copy;
	}

	private void setParent(Concept parent) {

		this.parent = toConceptTracker(parent);
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
		parent = toConceptTracker(newParent);

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

	private void removeAllSubTreeListeners() {

		conceptListeners.clear();

		for (Concept sub : getChildren()) {

			sub.removeAllSubTreeListeners();
		}
	}

	private void onChildAdded(Concept child, boolean replacement) {

		hierarchy.registerConcept(child);

		for (ConceptListener listener : conceptListeners) {

			listener.onChildAdded(child, replacement);
		}
	}

	private void onConstraintAdded(Constraint constraint) {

		for (ConceptListener listener : conceptListeners) {

			listener.onConstraintAdded(constraint);
		}
	}

	private void onConstraintRemoved(Constraint constraint) {

		for (ConceptListener listener : conceptListeners) {

			listener.onConstraintRemoved(constraint);
		}
	}

	private void onConceptRemoved(boolean replacing) {

		hierarchy.deregisterConcept(this);

		for (ConceptListener listener : conceptListeners) {

			listener.onConceptRemoved(this, replacing);
		}
	}

	private boolean canResetId(DynamicId newDynamicId) {

		return getModel().canResetDynamicConceptId(this, newDynamicId);
	}

	private EntityId toEntityId(DynamicId dynamicId) {

		return getModel().toEntityId(dynamicId);
	}

	private ConceptTracker toConceptTracker(Concept concept) {

		return getModel().getConceptTracking().toTracker(concept);
	}
}
