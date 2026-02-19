package uk.ac.manchester.cs.goblin.io.config;

/**
 * @author Colin Puleston
 */
class ConfigFileVocab {

	static final String MODEL_SECTION_TAG = "ModelSection";
	static final String HIERARCHY_TAG = "Hierarchy";
	static final String SIMPLE_CONSTRAINT_TYPE_TAG = "SimpleAttribute";
	static final String ANCHORED_CONSTRAINT_TYPE_TAG = "AnchoredAttribute";
	static final String HIERARCHICAL_CONSTRAINT_TYPE_TAG = "HierarchicalAttribute";

	static final String DYNAMIC_NAMESPACE_ATTR = "dynamicNamespace";
	static final String DYNAMIC_FILE_ATTR = "dynamicFilename";

	static final String ENTITY_LABEL_ATTR = "label";
	static final String ROOT_CONCEPT_ATTR = "rootConcept";
	static final String ANCHOR_CONCEPT_ATTR = "anchorConcept";
	static final String SOURCE_PROPERTY_ATTR = "sourceProperty";
	static final String TARGET_PROPERTY_ATTR = "targetProperty";
	static final String LINKING_PROPERTY_ATTR = "linkingProperty";
	static final String ROOT_TARGET_CONCEPT_ATTR = "rootTargetConcept";
	static final String FIXED_HIERARCHY_STRUCTURE_ATTR = "fixedStructure";
	static final String CORE_ATTRIBUTE_CONSTRAINTS_OPTION_ATTR = "constraints";
	static final String DYNAMIC_ATTRIBUTE_CONSTRAINTS_OPTION_ATTR = "dynamicAttributeConstraints";
	static final String HIERARCHICAL_LINKS_OPTION_ATTR = "linksOption";
}
