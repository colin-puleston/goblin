package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class ModelSection extends HierarchyContainer {

	private Model model;
	private String name;

	public Hierarchy addHierarchy(EntityId rootConceptId, boolean referenceOnly) {

		return addSectionHierarchy(createHierarchy(rootConceptId, referenceOnly));
	}

	public String getName() {

		return name;
	}

	ModelSection(Model model, String name) {

		this.model = model;
		this.name = name;
	}

	private Hierarchy createHierarchy(EntityId rootConceptId, boolean referenceOnly) {

		return referenceOnly
				? new ReferenceOnlyHierarchy(model, rootConceptId)
				: new DynamicHierarchy(model, rootConceptId);
	}

	private Hierarchy addSectionHierarchy(Hierarchy hierarchy) {

		addHierarchy(hierarchy);
		model.addHierarchy(hierarchy);

		return hierarchy;
	}
}
