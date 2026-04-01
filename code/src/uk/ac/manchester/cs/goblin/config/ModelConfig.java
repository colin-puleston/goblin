package uk.ac.manchester.cs.goblin.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ModelConfig {

	static private final String SINGLE_SECTION_MODEL_LABEL = "SINGLE SECTION MODEL";

	private ModelSectionConfig defaultSection = new ModelSectionConfig(SINGLE_SECTION_MODEL_LABEL);
	private List<ModelSectionConfig> sections = new ArrayList<ModelSectionConfig>();

	public ModelConfig() {

		sections.add(defaultSection);
	}

	public ModelSectionConfig addSingleSection() {

		return addSection(SINGLE_SECTION_MODEL_LABEL);
	}

	public ModelSectionConfig addSection(String label) {

		ModelSectionConfig section = new ModelSectionConfig(label);

		checkRemoveDefaultSection();
		sections.add(section);

		return section;
	}

	public void removeSection(ModelSectionConfig section) {

		sections.remove(section);
	}

	public void toSingleSection() {

		List<CoreHierarchyConfig> allHierarchies = getHierarchies();

		sections.clear();
		addSingleSection().addHierarchies(allHierarchies);
	}

	public boolean singleSectionModel() {

		return sections.size() == 1 && sections.get(0).getLabel().equals(SINGLE_SECTION_MODEL_LABEL);
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

	private void checkRemoveDefaultSection() {

		if (sections.size() == 1 && sections.get(0) == defaultSection) {

			sections.clear();
		}
	}
}
