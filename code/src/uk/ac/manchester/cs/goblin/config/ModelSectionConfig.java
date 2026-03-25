package uk.ac.manchester.cs.goblin.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ModelSectionConfig extends LabelledConfigEntity {

	private List<CoreHierarchyConfig> hierarchies = new ArrayList<CoreHierarchyConfig>();

	public ModelSectionConfig(String label) {

		super(label);
	}

	public void addHierarchy(CoreHierarchyConfig hierarchy) {

		hierarchies.add(hierarchy);
	}

	public void removeHierarchy(CoreHierarchyConfig hierarchy) {

		hierarchies.remove(hierarchy);
	}

	public void replaceHierarchy(
					CoreHierarchyConfig oldHierarchy,
					CoreHierarchyConfig newHierarchy) {

		int index = hierarchies.indexOf(oldHierarchy);

		if (index == -1) {

			throw new RuntimeException("Hierarchy not currently present");
		}

		hierarchies.remove(oldHierarchy);
		hierarchies.add(index, newHierarchy);
	}

	public List<CoreHierarchyConfig> getHierarchies() {

		return new ArrayList<CoreHierarchyConfig>(hierarchies);
	}

	ModelSection createSection(Model model) {

		ModelSection section = new ModelSection(model, getLabel());

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
