package uk.ac.manchester.cs.goblin.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ModelConfig extends ConfigObject<ModelConfig> {

	static private final String SINGLE_SECTION_MODEL_LABEL = "SINGLE SECTION MODEL";

	private DataArray<ModelSectionConfig> sections = new DataArray<ModelSectionConfig>();

	private ConfigEditActions editActions = new ConfigEditActions();

	public ModelConfig() {

		addSection(SINGLE_SECTION_MODEL_LABEL);
	}

	public void toSingleSectionMode() {

		if (singleSectionMode()) {

			throw new RuntimeException("Not a multi-section model!");
		}

		replaceSingleSection(SINGLE_SECTION_MODEL_LABEL);
	}

	public void toMultiSectionMode(String initialSectionLabel) {

		if (!singleSectionMode()) {

			throw new RuntimeException("Not a single-section model!");
		}

		replaceSingleSection(initialSectionLabel);
	}

	public ModelSectionConfig addSection(String label) {

		ModelSectionConfig section = new ModelSectionConfig(this, label);
		ModelSectionConfig ssmSection = getSingleSectionModeSectionOrNull();

		if (ssmSection != null) {

			if (ssmSection.hasHierarchies()) {

				throw new RuntimeException("Cannot add new section to single-section model!");
			}

			sections.replace(section);
		}
		else {

			sections.add(section);
		}

		return section;
	}

	public void removeSection(ModelSectionConfig section) {

		sections.remove(section);
	}

	public void reorderSections(List<ModelSectionConfig> reorderedSections) {

		sections.reorder(reorderedSections);
	}

	public boolean singleSectionMode() {

		return getSingleSectionModeSectionOrNull() != null;
	}

	public List<ModelSectionConfig> getSections() {

		return sections.copy();
	}

	public ModelSectionConfig getSingleSectionModeSection() {

		ModelSectionConfig section = getSingleSectionModeSectionOrNull();

		if (section != null) {

			return section;
		}

		throw new RuntimeException("Not a single-section model!");
	}

	public List<CoreHierarchyConfig> getHierarchies() {

		List<CoreHierarchyConfig> hierarchies = new ArrayList<CoreHierarchyConfig>();

		for (ModelSectionConfig section : sections.get()) {

			hierarchies.addAll(section.getHierarchies());
		}

		return hierarchies;
	}

	public CoreHierarchyConfig findHierarchy(EntityId rootConceptId) {

		for (CoreHierarchyConfig hierarchy : getHierarchies()) {

			if (hierarchy.getRootConceptId().equals(rootConceptId)) {

				return hierarchy;
			}
		}

		throw new Error("Hierarchy not found with root concept: " + rootConceptId);
	}

	public ConfigEditActions getEditActions() {

		return editActions;
	}

	public Model createModel() {

		Model createdModel = new Model();

		for (ModelSectionConfig section : sections.get()) {

			createdModel.addSection(section.createSection(createdModel));
		}

		Iterator<ModelSection> createdSections = createdModel.getSections().iterator();

		for (ModelSectionConfig section : sections.get()) {

			section.addCoreAttributes(createdSections.next());
		}

		return createdModel;
	}

	ConfigEditLocation createEditLocation() {

		return new ConfigEditLocation();
	}

	private void replaceSingleSection(String newSectionLabel) {

		sections.replace(new ModelSectionConfig(this, newSectionLabel, getHierarchies()));
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
