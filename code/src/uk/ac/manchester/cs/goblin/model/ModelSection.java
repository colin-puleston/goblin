package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class ModelSection extends HierarchyContainer {

	private Model model;
	private String name;

	public Hierarchy addDynamicHierarchy(EntityId rootConceptId) {

		return addSectionHierarchy(new DynamicHierarchy(model, rootConceptId));
	}

	public Hierarchy addReferenceOnlyHierarchy(EntityId rootConceptId) {

		return addSectionHierarchy(new ReferenceOnlyHierarchy(model, rootConceptId));
	}

	public String getName() {

		return name;
	}

	ModelSection(Model model, String name) {

		this.model = model;
		this.name = name;
	}

	private Hierarchy addSectionHierarchy(Hierarchy hierarchy) {

		addHierarchy(hierarchy);
		model.addHierarchy(hierarchy);

		return hierarchy;
	}
}
