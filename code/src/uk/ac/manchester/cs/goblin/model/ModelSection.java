package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public class ModelSection extends CoreHierarchyContainer {

	private Model model;
	private String label;

	public Hierarchy addCoreHierarchy(EntityId rootConceptId, boolean referenceOnly) {

		Hierarchy hierarchy = new CoreHierarchy(model, rootConceptId, referenceOnly);

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
}
