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

	public void setLabel(String label) {

		this.label = label;
	}

	public abstract void enableDynamicAttributes(ConstraintsOption constraintsOption);

	public abstract void addCoreAttribute(Attribute attribute);

	public abstract void addListener(HierarchyListener listener);

	public abstract void removeListener(HierarchyListener listener);

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

	public boolean potentiallyHasAttributes() {

		return hasCoreAttributes() || dynamicAttributesEnabled();
	}

	public List<Attribute> getAllAttributes() {

		return Collections.emptyList();
	}

	public boolean hasCoreAttributes() {

		return false;
	}

	public List<Attribute> getCoreAttributes() {

		return Collections.emptyList();
	}

	public boolean potentiallyHasInwardAttributes() {

		return hasInwardCoreAttributes();
	}

	public boolean hasInwardCoreAttributes() {

		return !inwardCoreAttributes.isEmpty();
	}

	public List<Attribute> getInwardCoreAttributes() {

		return new ArrayList<Attribute>(inwardCoreAttributes);
	}

	public boolean dynamicAttributesEnabled() {

		return false;
	}

	public boolean hasDynamicAttributes() {

		return false;
	}

	public List<DynamicAttribute> getDynamicAttributes() {

		return Collections.emptyList();
	}

	Hierarchy(Model model, EntityId rootConceptId, String label) {

		this.model = model;
		this.label = label;

		rootConcept = createRootConcept(rootConceptId);

		registerConcept(rootConcept);
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

	abstract ConstraintsOption getDynamicAttributeConstraintsOption();

	abstract void onAddedDynamicAttribute(DynamicAttribute attribute);

	abstract void onRemovedDynamicAttribute(DynamicAttribute attribute);
}
