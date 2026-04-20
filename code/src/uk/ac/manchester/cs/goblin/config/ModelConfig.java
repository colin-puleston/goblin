package uk.ac.manchester.cs.goblin.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ModelConfig {

	static private final String SINGLE_SECTION_MODEL_LABEL = "SINGLE SECTION MODEL";

	private List<ModelSectionConfig> sections = new ArrayList<ModelSectionConfig>();

	private TargetHierarchyManager targetHierarchyManager = new TargetHierarchyManager(this);

	public ModelConfig() {

		addSingleSection();
	}

	public ModelSectionConfig addSingleSection() {

		return addSection(SINGLE_SECTION_MODEL_LABEL);
	}

	public ModelSectionConfig addSection(String label) {

		ModelSectionConfig section = new ModelSectionConfig(this, label);

		checkRemoveDefaultSection();
		sections.add(section);

		return section;
	}

	public void addTargetHierarchyListener(TargetHierarchyListener listener) {

		targetHierarchyManager.addListener(listener);
	}

	public void removeSection(ModelSectionConfig section) {

		sections.remove(section);
	}

	public void reorderSections(List<ModelSectionConfig> newOrderedSections) {

		sections.clear();
		sections.addAll(newOrderedSections);
	}

	public void toSingleSectionMode() {

		List<CoreHierarchyConfig> allHierarchies = getHierarchies();

		sections.clear();
		addSingleSection().addHierarchies(allHierarchies);
	}

	public boolean singleSectionMode() {

		ModelSectionConfig section = getSingleSectionModeSectionOrNull();

		return section != null && section.getLabel().equals(SINGLE_SECTION_MODEL_LABEL);
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

		Model createdModel = new Model();

		for (ModelSectionConfig section : sections) {

			createdModel.addSection(section.createSection(createdModel));
		}

		Iterator<ModelSection> createdSections = createdModel.getSections().iterator();

		for (ModelSectionConfig section : sections) {

			section.addCoreAttributes(createdSections.next());
		}

		return createdModel;
	}

	void onCoreHierarchyRelabelled(CoreHierarchyConfig hierarchy) {

		targetHierarchyManager.onCoreHierarchyRelabelled(hierarchy);
	}

	void onCoreHierarchyRemoved(CoreHierarchyConfig hierarchy) {

		targetHierarchyManager.onCoreHierarchyRemoved(hierarchy);
	}

	private void checkRemoveDefaultSection() {

		ModelSectionConfig section = getSingleSectionModeSectionOrNull();

		if (section != null) {

			if (section.hasHierarchies()) {

				throw new RuntimeException("Cannot add new section to single-section model!");
			}

			sections.clear();
		}
	}

	private ModelSectionConfig getSingleSectionModeSectionOrNull() {

		if (sections.size() == 1) {

			ModelSectionConfig section = sections.get(0);

			if (section.getLabel().equals(SINGLE_SECTION_MODEL_LABEL)) {

				return section;
			}
		}

		return null;
	}
}
