package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class ModelSection extends HierarchyContainer {

	private Model model;
	private String label;

	public Hierarchy addCoreHierarchy(EntityId rootConceptId, boolean referenceOnly) {

		Hierarchy hierarchy = createHierarchy(rootConceptId, referenceOnly);

		addHierarchy(hierarchy);
		model.addHierarchy(hierarchy);

		return hierarchy;
	}

	public String getLabel() {

		return label;
	}

	ModelSection(Model model, String label) {

		this.model = model;
		this.label = label;
	}

	private Hierarchy createHierarchy(EntityId rootConceptId, boolean referenceOnly) {

		if (referenceOnly) {

			return new ReferenceOnlyCoreHierarchy(model, rootConceptId);
		}

		return new EditableCoreHierarchy(model, rootConceptId);
	}
}
