package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class ModelSection extends HierarchyContainer {

	private Model model;
	private String name;

	public Hierarchy addCoreHierarchy(EntityId rootConceptId, boolean referenceOnly) {

		Hierarchy hierarchy = createHierarchy(rootConceptId, referenceOnly);

		addHierarchy(hierarchy);
		model.addHierarchy(hierarchy);

		return hierarchy;
	}

	public String getName() {

		return name;
	}

	ModelSection(Model model, String name) {

		this.model = model;
		this.name = name;
	}

	private Hierarchy createHierarchy(EntityId rootConceptId, boolean referenceOnly) {

		if (referenceOnly) {

			return new ReferenceOnlyCoreHierarchy(model, rootConceptId);
		}

		return new EditableCoreHierarchy(model, rootConceptId);
	}
}
