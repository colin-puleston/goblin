package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class CoreHierarchyContainer {

	private List<Hierarchy> hierarchies = new ArrayList<Hierarchy>();

	public List<Hierarchy> getCoreHierarchies() {

		return new ArrayList<Hierarchy>(hierarchies);
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

			if (hierarchy.containsConcept(conceptId)) {

				return hierarchy.getConcept(conceptId);
			}
		}

		return null;
	}

	void addHierarchy(Hierarchy hierarchy) {

		hierarchies.add(hierarchy);
	}
}
