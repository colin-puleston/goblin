package uk.ac.manchester.cs.goblin.io.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ModelSectionConfig {

	private String label;
	private List<CoreHierarchyConfig> hierarchies = new ArrayList<CoreHierarchyConfig>();

	public ModelSectionConfig(String label) {

		this.label = label;
	}

	public CoreHierarchyConfig addHierarchy(EntityId rootConceptId) {

		CoreHierarchyConfig hierarchy = new CoreHierarchyConfig(rootConceptId);

		hierarchies.add(hierarchy);

		return hierarchy;
	}

	public String getLabel() {

		return label;
	}

	public List<CoreHierarchyConfig> getHierarchies() {

		return new ArrayList<CoreHierarchyConfig>(hierarchies);
	}

	ModelSection createSection(Model model) {

		ModelSection section = new ModelSection(model, label);

		for (CoreHierarchyConfig hierarchy : hierarchies) {

			section.addCoreHierarchy(hierarchy.createHierarchy(model));
		}

		return section;
	}

	void addCoreAttributes(ModelSection section) {

		Iterator<Hierarchy> createdHierarchies = section.getCoreHierarchies().iterator();

		for (CoreHierarchyConfig hierarchy : hierarchies) {

			hierarchy.addCoreAttributes(createdHierarchies.next());
		}
	}
}
