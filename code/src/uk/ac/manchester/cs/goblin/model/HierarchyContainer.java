package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class HierarchyContainer {

	private List<Hierarchy> hierarchies = new ArrayList<Hierarchy>();

	public List<Hierarchy> getAllHierarchies() {

		return new ArrayList<Hierarchy>(hierarchies);
	}

	public List<Hierarchy> getDynamicHierarchies() {

		return getStatusHierarchies(true);
	}

	public List<Hierarchy> getReferenceOnlyHierarchies() {

		return getStatusHierarchies(false);
	}

	public Hierarchy getHierarchy(EntityId rootConceptId) {

		for (Hierarchy hierarchy : hierarchies) {

			if (hierarchy.hasRootConcept(rootConceptId)) {

				return hierarchy;
			}
		}

		throw new RuntimeException(
					"Not root-concept of contained hierarchy: "
					+ rootConceptId);
	}

	public boolean containsConcept(EntityId conceptId) {

		return lookForConcept(conceptId) != null;
	}

	public Concept getConcept(EntityId conceptId) {

		Concept concept = lookForConcept(conceptId);

		if (concept != null) {

			return concept;
		}

		throw new RuntimeException("Cannot find concept: " + conceptId);
	}

	public Concept lookForConcept(EntityId conceptId) {

		for (Hierarchy hierarchy : hierarchies) {

			if (hierarchy.hasConcept(conceptId)) {

				return hierarchy.getConcept(conceptId);
			}
		}

		return null;
	}

	void addHierarchy(Hierarchy hierarchy) {

		hierarchies.add(hierarchy);
	}

	private List<Hierarchy> getStatusHierarchies(boolean dynamic) {

		List<Hierarchy> statusHierarchies = new ArrayList<Hierarchy>();

		for (Hierarchy hierarchy : hierarchies) {

			if (hierarchy.dynamicHierarchy() == dynamic) {

				statusHierarchies.add(hierarchy);
			}
		}

		return statusHierarchies;
	}
}
