package uk.ac.manchester.cs.goblin.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ModelSectionConfig extends LabelledConfigObject<ModelSectionConfig> {

	private ModelConfig model;
	private DataArray<CoreHierarchyConfig> hierarchies;

	public CoreHierarchyConfig addHierarchy(EntityId rootConceptId) {

		CoreHierarchyConfig hierarchy = new CoreHierarchyConfig(model, rootConceptId);

		hierarchies.add(hierarchy);

		return hierarchy;
	}

	public void addHierarchies(List<CoreHierarchyConfig> hierarchies) {

		this.hierarchies.addAll(hierarchies);
	}

	public void grabHierarchy(ModelSectionConfig fromSection, CoreHierarchyConfig hierarchy) {

		hierarchies.add(hierarchy);

		fromSection.hierarchies.remove(hierarchy);
	}

	public void removeHierarchy(CoreHierarchyConfig hierarchy) {

		hierarchies.remove(hierarchy);

		checkTargetHierarchyRemoved(hierarchy);
	}

	public void reorderHierarchies(List<CoreHierarchyConfig> reorderedHierarchies) {

		hierarchies.reorder(reorderedHierarchies);
	}

	public boolean hasHierarchies() {

		return !hierarchies.isEmpty();
	}

	public boolean hasHierarchy(CoreHierarchyConfig hierarchy) {

		return hierarchies.contains(hierarchy);
	}

	public List<CoreHierarchyConfig> getHierarchies() {

		return hierarchies.copy();
	}

	ModelSectionConfig(ModelConfig model, String label) {

		this(model, label, new ArrayList<CoreHierarchyConfig>());
	}

	ModelSectionConfig(ModelConfig model, String label, List<CoreHierarchyConfig> hierarchies) {

		super(label);

		this.model = model;
		this.hierarchies = new DataArray<CoreHierarchyConfig>(hierarchies);
	}

	ModelSection createSection(Model createdModel) {

		ModelSection createdSection = new ModelSection(createdModel, getLabel());

		for (CoreHierarchyConfig hierarchy : hierarchies.get()) {

			createdSection.addCoreHierarchy(hierarchy.createHierarchy(createdModel));
		}

		return createdSection;
	}

	void addCoreAttributes(ModelSection createdSection) {

		Iterator<Hierarchy> createdHierarchies = createdSection.getCoreHierarchies().iterator();

		for (CoreHierarchyConfig hierarchy : hierarchies.get()) {

			hierarchy.addCoreAttributes(createdHierarchies.next());
		}
	}

	private void checkTargetHierarchyRemoved(CoreHierarchyConfig removedHierarchy) {

		EntityId rootTargetId = removedHierarchy.getRootConceptId();

		for (CoreHierarchyConfig sourceHierarchy : model.getHierarchies()) {

			for (CoreAttributeConfig attribute : sourceHierarchy.getCoreAttributes()) {

				if (attribute.getRootTargetConceptId().equals(rootTargetId)) {

					sourceHierarchy.removeCoreAttribute(attribute);
				}
			}
		}
	}
}
