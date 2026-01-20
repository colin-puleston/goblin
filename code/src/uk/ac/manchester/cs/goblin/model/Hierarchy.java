package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class Hierarchy {

	private Model model;
	private String label;

	private RootConcept rootConcept;
	private Map<EntityId, Concept> conceptsById = new HashMap<EntityId, Concept>();

	private List<Attribute> inwardCoreAttributes = new ArrayList<Attribute>();

	public void addListener(HierarchyListener listener) {

		throw createListenerOperationException();
	}

	public void removeListener(HierarchyListener listener) {

		throw createListenerOperationException();
	}

	public void setLabel(String label) {

		this.label = label;
	}

	public void addCoreAttribute(Attribute attribute) {

		throw createAttributeAddException(attribute, "outward");
	}

	public Model getModel() {

		return model;
	}

	public String getLabel() {

		return label;
	}

	public boolean referenceOnly() {

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

	public boolean hasCoreAttributes() {

		return false;
	}

	public boolean dynamicAttributesEnabled() {

		return false;
	}

	public boolean potentiallyHasAttributes() {

		return hasCoreAttributes() || dynamicAttributesEnabled();
	}

	public boolean hasInwardCoreAttributes() {

		return !inwardCoreAttributes.isEmpty();
	}

	public List<Attribute> getAllAttributes() {

		return Collections.emptyList();
	}

	public List<Attribute> getCoreAttributes() {

		return Collections.emptyList();
	}

	public List<Attribute> getDynamicAttributes() {

		return Collections.emptyList();
	}

	public List<Attribute> getInwardCoreAttributes() {

		return new ArrayList<Attribute>(inwardCoreAttributes);
	}

	Hierarchy(Model model, EntityId rootConceptId) {

		this.model = model;

		label = rootConceptId.getLabel();
		rootConcept = createRootConcept(rootConceptId);
	}

	abstract RootConcept createRootConcept(EntityId rootConceptId);

	void registerConcept(Concept concept) {

		conceptsById.put(concept.getConceptId(), concept);
	}

	void deregisterConcept(Concept concept) {

		conceptsById.remove(concept.getConceptId());
	}

	void addInwardCoreAttribute(Attribute attribute) {

		inwardCoreAttributes.add(attribute);
	}

	void onAddedDynamicAttribute(DynamicAttribute attribute) {

		throw createListenerOperationException();
	}

	void onRemovedDynamicAttribute(DynamicAttribute attribute) {

		throw createListenerOperationException();
	}

	private RuntimeException createListenerOperationException() {

		return new RuntimeException("Illegal operation on non-editable hierachy: " + label);
	}

	private RuntimeException createAttributeAddException(
								Attribute attribute,
								String direction) {

		return new RuntimeException(
						"Cannot add " + direction + " attribute to: "
						+ getClass().getSimpleName());
	}
}
