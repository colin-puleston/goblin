package uk.ac.manchester.cs.goblin.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ModelSectionConfig extends LabelledConfigEntity {

	private ModelConfig model;
	private List<CoreHierarchyConfig> hierarchies = new ArrayList<CoreHierarchyConfig>();

	private List<HierarchyGrabListener> hierarchyGrabListeners = new ArrayList<HierarchyGrabListener>();

	public CoreHierarchyConfig addHierarchy(EntityId rootConceptId) {

		CoreHierarchyConfig hierarchy = new CoreHierarchyConfig(model, rootConceptId);

		hierarchies.add(hierarchy);

		return hierarchy;
	}

	public void addHierarchyGrabListener(HierarchyGrabListener listener) {

		hierarchyGrabListeners.add(listener);
	}

	public void addHierarchies(List<CoreHierarchyConfig> hierarchies) {

		this.hierarchies.addAll(hierarchies);
	}

	public void grabHierarchy(ModelSectionConfig fromSection, CoreHierarchyConfig hierarchy) {

		hierarchies.add(hierarchy);

		fromSection.removeGrabbedHierarchy(hierarchy);
	}

	public void removeHierarchy(CoreHierarchyConfig hierarchy) {

		hierarchies.remove(hierarchy);

		model.onCoreHierarchyRemoved(hierarchy);
	}

	public void reorderHierarchies(List<CoreHierarchyConfig> newOrderedHierarchies) {

		new ListReorderer<CoreHierarchyConfig>(hierarchies).reorder(newOrderedHierarchies);
	}

	public boolean hasHierarchies() {

		return !hierarchies.isEmpty();
	}

	public boolean hasHierarchy(CoreHierarchyConfig hierarchy) {

		return hierarchies.contains(hierarchy);
	}

	public List<CoreHierarchyConfig> getHierarchies() {

		return new ArrayList<CoreHierarchyConfig>(hierarchies);
	}

	ModelSectionConfig(ModelConfig model, String label) {

		super(label);

		this.model = model;
	}

	ModelSection createSection(Model createdModel) {

		ModelSection createdSection = new ModelSection(createdModel, getLabel());

		for (CoreHierarchyConfig hierarchy : hierarchies) {

			createdSection.addCoreHierarchy(hierarchy.createHierarchy(createdModel));
		}

		return createdSection;
	}

	void addCoreAttributes(ModelSection createdSection) {

		Iterator<Hierarchy> createdHierarchies = createdSection.getCoreHierarchies().iterator();

		for (CoreHierarchyConfig hierarchy : hierarchies) {

			hierarchy.addCoreAttributes(createdHierarchies.next());
		}
	}

	private void removeGrabbedHierarchy(CoreHierarchyConfig hierarchy) {

		hierarchies.remove(hierarchy);

		for (HierarchyGrabListener listener : hierarchyGrabListeners) {

			listener.onHierarchyGrabbed();
		}
	}
}
