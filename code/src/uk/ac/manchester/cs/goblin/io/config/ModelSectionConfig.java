package uk.ac.manchester.cs.goblin.io.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ModelSectionConfig {

	private String label;
	private List<HierarchyConfig> hierarchies = new ArrayList<HierarchyConfig>();

	public ModelSectionConfig(String label) {

		this.label = label;
	}

	public HierarchyConfig addHierarchy(EntityId rootConceptId, boolean fixedStructure) {

		HierarchyConfig hierarchy = new HierarchyConfig(rootConceptId, fixedStructure);

		hierarchies.add(hierarchy);

		return hierarchy;
	}

	public List<HierarchyConfig> getHierarchies() {

		return new ArrayList<HierarchyConfig>(hierarchies);
	}

	ModelSection createSection(Model model) {

		ModelSection section = new ModelSection(model, label);

		for (HierarchyConfig hierarchy : hierarchies) {

			section.addCoreHierarchy(hierarchy.createHierarchy(model));
		}

		return section;
	}

	void addCoreAttributes(ModelSection section) {

		Iterator<Hierarchy> createdHierarchies = section.getCoreHierarchies().iterator();

		for (HierarchyConfig hierarchy : hierarchies) {

			hierarchy.addCoreAttributes(createdHierarchies.next());
		}
	}
}
