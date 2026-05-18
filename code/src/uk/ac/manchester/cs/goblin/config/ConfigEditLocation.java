package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public class ConfigEditLocation extends EditLocation {

	private ModelSectionConfig section = null;
	private CoreHierarchyConfig hierarchy = null;
	private CoreAttributeConfig attribute = null;

	public ModelSectionConfig getEditedSectionOrNull() {

		return section;
	}

	public CoreHierarchyConfig getEditedHierarchyOrNull() {

		return hierarchy;
	}

	public CoreAttributeConfig getEditedAttributeOrNull() {

		return attribute;
	}

	ConfigEditLocation() {
	}

	ConfigEditLocation(ModelSectionConfig editedObject) {

		this();

		section = editedObject;
	}

	ConfigEditLocation(CoreHierarchyConfig editedObject) {

		this(editedObject.getSection());

		hierarchy = editedObject;
	}

	ConfigEditLocation(CoreAttributeConfig editedObject) {

		this(editedObject.getSourceHierarchy());

		attribute = editedObject;
	}
}
