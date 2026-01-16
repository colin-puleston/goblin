package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class Hierarchy {

	private Model model;
	private String name;

	private RootConcept rootConcept;
	private Map<EntityId, Concept> conceptsById = new HashMap<EntityId, Concept>();

	private List<ConstraintType> inwardCoreConstraintTypes = new ArrayList<ConstraintType>();

	public void addListener(HierarchyListener listener) {

		throw createListenerOperationException();
	}

	public void removeListener(HierarchyListener listener) {

		throw createListenerOperationException();
	}

	public void setName(String name) {

		this.name = name;
	}

	public void addCoreConstraintType(ConstraintType type) {

		throw createConstraintTypeAddException(type, "outward");
	}

	public Model getModel() {

		return model;
	}

	public String getName() {

		return name;
	}

	public boolean referenceOnly() {

		return false;
	}

	public boolean dynamicConstraintsEnabled() {

		return false;
	}

	public Concept getRootConcept() {

		return rootConcept;
	}

	public boolean hasRootConcept(EntityId conceptId) {

		return rootConcept.getConceptId().equals(conceptId);
	}

	public boolean containsConcept(EntityId conceptId) {

		return conceptsById.containsKey(conceptId);
	}

	public Concept getConcept(EntityId conceptId) {

		Concept concept = conceptsById.get(conceptId);

		if (concept == null) {

			throw new RuntimeException("Cannot find concept: " + conceptId);
		}

		return concept;
	}

	public boolean hasCoreConstraintTypes() {

		return false;
	}

	public boolean hasPotentialConstraintTypes() {

		return hasCoreConstraintTypes() || dynamicConstraintsEnabled();
	}

	public boolean hasInwardCoreConstraintTypes() {

		return !inwardCoreConstraintTypes.isEmpty();
	}

	public List<ConstraintType> getAllConstraintTypes() {

		return Collections.emptyList();
	}

	public List<ConstraintType> getCoreConstraintTypes() {

		return Collections.emptyList();
	}

	public List<ConstraintType> getInwardCoreConstraintTypes() {

		return new ArrayList<ConstraintType>(inwardCoreConstraintTypes);
	}

	Hierarchy(Model model, EntityId rootConceptId) {

		this.model = model;

		name = rootConceptId.getLabel();
		rootConcept = createRootConcept(rootConceptId);
	}

	abstract RootConcept createRootConcept(EntityId rootConceptId);

	void registerConcept(Concept concept) {

		conceptsById.put(concept.getConceptId(), concept);
	}

	void deregisterConcept(Concept concept) {

		conceptsById.remove(concept.getConceptId());
	}

	void addInwardCoreConstraintType(ConstraintType type) {

		inwardCoreConstraintTypes.add(type);
	}

	void onAddedDynamicConstraintType(DynamicConstraintType type) {

		throw createListenerOperationException();
	}

	void onRemovedDynamicConstraintType(DynamicConstraintType type) {

		throw createListenerOperationException();
	}

	private RuntimeException createListenerOperationException() {

		return new RuntimeException("Illegal operation on non-editable hierachy: " + name);
	}

	private RuntimeException createConstraintTypeAddException(
								ConstraintType type,
								String direction) {

		return new RuntimeException(
						"Cannot add " + direction + " constraint-types to: "
						+ getClass().getSimpleName());
	}
}
