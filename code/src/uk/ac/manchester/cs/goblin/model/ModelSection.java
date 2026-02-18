package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public class ModelSection extends CoreHierarchyContainer {

	private Model model;
	private String label;

	public ModelSection(Model model, String label) {

		this.model = model;
		this.label = label;
	}

	public Hierarchy addCoreHierarchy(CoreHierarchy hierarchy) {

		addHierarchy(hierarchy);
		model.addHierarchy(hierarchy);

		return hierarchy;
	}

	public String getLabel() {

		return label;
	}
}
