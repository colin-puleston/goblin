package uk.ac.manchester.cs.goblin.io;

/**
 * @author Colin Puleston
 */
class ConfigFileVocab {

	static final String MODEL_SECTION_TAG = "ModelSection";
	static final String DYNAMIC_HIERARCHY_TAG = "DynamicHierarchy";
	static final String REFERENCE_ONLY_HIERARCHY_TAG = "ReferenceOnlyHierarchy";
	static final String SIMPLE_CONSTRAINT_TYPE_TAG = "SimpleConstraintType";
	static final String ANCHORED_CONSTRAINT_TYPE_TAG = "AnchoredConstraintType";
	static final String HIERARCHICAL_CONSTRAINT_TYPE_TAG = "HierarchicalConstraintType";
	static final String SEMANTICS_OPTION_TAG = "SemanticsOption";

	static final String DYNAMIC_NAMESPACE_ATTR = "dynamicNamespace";
	static final String DYNAMIC_FILE_ATTR = "dynamicFilename";

	static final String ENTITY_NAME_ATTR = "name";
	static final String ROOT_CONCEPT_ATTR = "rootConcept";
	static final String ANCHOR_CONCEPT_ATTR = "anchorConcept";
	static final String SOURCE_PROPERTY_ATTR = "sourceProperty";
	static final String TARGET_PROPERTY_ATTR = "targetProperty";
	static final String LINKING_PROPERTY_ATTR = "linkingProperty";
	static final String ROOT_TARGET_CONCEPT_ATTR = "rootTargetConcept";
	static final String SINGLE_IMPLIED_VALUES_ATTR = "singleImpliedValues";
	static final String SEMANTICS_OPTION_ATTR = "semantics";
}
