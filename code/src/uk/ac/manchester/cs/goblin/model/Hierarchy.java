package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class Hierarchy {

	private Model model;

	private String name;
	private RootConcept root;
	private Map<EntityId, Concept> conceptsById = new HashMap<EntityId, Concept>();

	public abstract void addConstraintType(ConstraintType type);

	public abstract boolean dynamicHierarchy();

	public void setName(String name) {

		this.name = name;
	}

	public Model getModel() {

		return model;
	}

	public String getName() {

		return name;
	}

	public Concept getRootConcept() {

		return root;
	}

	public boolean hasRootConcept(EntityId conceptId) {

		return root.getConceptId().equals(conceptId);
	}

	public boolean hasConcept(EntityId conceptId) {

		return conceptsById.containsKey(conceptId);
	}

	public Concept getConcept(EntityId conceptId) {

		Concept concept = conceptsById.get(conceptId);

		if (concept == null) {

			throw new RuntimeException("Cannot find concept: " + conceptId);
		}

		return concept;
	}

	public abstract boolean hasConstraintTypes();

	public abstract List<ConstraintType> getConstraintTypes();

	Hierarchy(Model model, EntityId rootConceptId) {

		this.model = model;

		name = rootConceptId.getLabel();
		root = new RootConcept(this, rootConceptId);
	}

	void registerConcept(Concept concept) {

		conceptsById.put(concept.getConceptId(), concept);
	}

	void deregisterConcept(Concept concept) {

		conceptsById.remove(concept.getConceptId());
	}
}
