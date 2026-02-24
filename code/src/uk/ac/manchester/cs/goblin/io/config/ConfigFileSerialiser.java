package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.config.*;

/**
 * @author Colin Puleston
 */
class ConfigFileSerialiser {

	static final String ROOT_NODE_TAG = "GoblinConfiguration";
	static final String SECTION_TAG = "ModelSection";
	static final String HIERARCHY_TAG = "Hierarchy";

	static final String SIMPLE_ATTRIBUTE_TAG = "SimpleAttribute";
	static final String ANCHORED_ATTRIBUTE_TAG = "AnchoredAttribute";
	static final String HIERARCHICAL_ATTRIBUTE_TAG = "HierarchicalAttribute";

	static final String CORE_FILENAME_ATTR = "coreFilename";
	static final String DYNAMIC_FILENAME_ATTR = "dynamicFilename";

	static final String ENTITY_LABEL_ATTR = "label";

	static final String ROOT_CONCEPT_ATTR = "rootConcept";
	static final String FIXED_HIERARCHY_STRUCTURE_ATTR = "fixedStructure";
	static final String DYNAMIC_ATTR_CONSTRAINTS_OPT_ATTR = "dynamicAttributeConstraints";

	static final String ROOT_TARGET_CONCEPT_ATTR = "rootTargetConcept";
	static final String LINKING_PROPERTY_ATTR = "linkingProperty";
	static final String ANCHOR_CONCEPT_ATTR = "anchorConcept";
	static final String SOURCE_PROPERTY_ATTR = "sourceProperty";
	static final String TARGET_PROPERTY_ATTR = "targetProperty";
	static final String PROPERTY_ATTR_CONSTRAINTS_OPT_ATTR = "constraints";
	static final String HIERARCHICAL_LINKS_OPT_ATTR = "linksOption";

	static private final String CONFIG_FILENAME = "goblin.xml";

	static File getConfigFile() {

		return getFileFromClasspath(CONFIG_FILENAME);
	}

	static File getFileFromClasspath(String name) {

		return KConfigResourceFinder.FILES.getResource(name);
	}
}
