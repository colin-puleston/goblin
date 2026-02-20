package uk.ac.manchester.cs.goblin.io.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ModelConfig {

	static private final String DEFAULT_SECTION_NAME_PREFIX = "Section-";

	private List<ModelSectionConfig> sections = new ArrayList<ModelSectionConfig>();

	public ModelSectionConfig addSection() {

		return addSection(DEFAULT_SECTION_NAME_PREFIX + sections.size());
	}

	public ModelSectionConfig addSection(String label) {

		ModelSectionConfig section = new ModelSectionConfig(label);

		sections.add(section);

		return section;
	}

	public List<ModelSectionConfig> getSections() {

		return new ArrayList<ModelSectionConfig>(sections);
	}

	public List<CoreHierarchyConfig> getHierarchies() {

		List<CoreHierarchyConfig> hierarchies = new ArrayList<CoreHierarchyConfig>();

		for (ModelSectionConfig section : sections) {

			hierarchies.addAll(section.getHierarchies());
		}

		return hierarchies;
	}

	public Model createModel() {

		Model model = new Model();

		for (ModelSectionConfig section : sections) {

			model.addSection(section.createSection(model));
		}

		Iterator<ModelSection> createdSections = model.getSections().iterator();

		for (ModelSectionConfig section : sections) {

			section.addCoreAttributes(createdSections.next());
		}

		return model;
	}
}
