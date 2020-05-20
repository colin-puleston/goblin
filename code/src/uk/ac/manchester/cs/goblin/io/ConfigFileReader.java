package uk.ac.manchester.cs.goblin.io;

import java.io.*;
import java.net.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.config.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConfigFileReader {

	static private final String CONFIG_FILE_NAME = "goblin.xml";

	static private final String DYNAMIC_HIERARCHY_TAG = "DynamicHierarchy";
	static private final String REFERENCE_ONLY_HIERARCHY_TAG = "ReferenceOnlyHierarchy";
	static private final String ANCHORED_CONSTRAINT_TYPE_TAG = "AnchoredConstraintType";
	static private final String SIMPLE_CONSTRAINT_TYPE_TAG = "SimpleConstraintType";
	static private final String SEMANTICS_OPTION_TAG = "SemanticsOption";

	static private final String DYNAMIC_NAMESPACE_ATTR = "dynamicNamespace";
	static private final String DYNAMIC_FILE_ATTR = "dynamicFilename";

	static private final String ROOT_CONCEPT_ATTR = "rootConcept";

	static private final String CONSTRAINT_TYPE_NAME_ATTR = "name";
	static private final String ANCHOR_CONCEPT_ATTR = "anchorConcept";
	static private final String SOURCE_PROPERTY_ATTR = "sourceProperty";
	static private final String TARGET_PROPERTY_ATTR = "targetProperty";
	static private final String LINKING_PROPERTY_ATTR = "linkingProperty";
	static private final String ROOT_TARGET_CONCEPT_ATTR = "rootTargetConcept";
	static private final String IMPLIED_VALUES_MULT_ATTR = "impliedValuesMultiplicity";
	static private final String SEMANTICS_OPTION_ATTR = "semantics";

	private KConfigNode rootNode;

	private class CoreModelPopulator {

		private Model model;
		private Ontology ontology;

		private abstract class HierarchiesLoader {

			HierarchiesLoader() {

				for (KConfigNode hierarchyNode : rootNode.getChildren(getStatusTag())) {

					addHierarchy(getRootConceptId(hierarchyNode));
				}
			}

			abstract String getStatusTag();

			abstract void addHierarchy(EntityId rootConceptId);
		}

		private class DynamicHierarchiesLoader extends HierarchiesLoader {

			String getStatusTag() {

				return DYNAMIC_HIERARCHY_TAG;
			}

			void addHierarchy(EntityId rootConceptId) {

				model.addDynamicHierarchy(rootConceptId);
			}
		}

		private class ReferenceOnlyHierarchiesLoader extends HierarchiesLoader {

			String getStatusTag() {

				return REFERENCE_ONLY_HIERARCHY_TAG;
			}

			void addHierarchy(EntityId rootConceptId) {

				model.addReferenceOnlyHierarchy(rootConceptId);
			}
		}

		private abstract class ConstraintTypesLoader {

			ConstraintTypesLoader() {

				Iterator<Hierarchy> hierarchies = model.getDynamicHierarchies().iterator();

				for (KConfigNode hierarchyNode : rootNode.getChildren(DYNAMIC_HIERARCHY_TAG)) {

					loadHierarchyTypes(hierarchyNode, hierarchies.next());
				}
			}

			abstract String getTypeTag();

			abstract ConstraintType loadSpecificType(
										KConfigNode node,
										String name,
										Concept rootSrc,
										Concept rootTgt);

			private void loadHierarchyTypes(KConfigNode hierarchyNode, Hierarchy hierarchy) {

				for (KConfigNode typeNode : hierarchyNode.getChildren(getTypeTag())) {

					hierarchy.addConstraintType(loadType(typeNode, hierarchy));
				}
			}

			private ConstraintType loadType(KConfigNode node, Hierarchy hierarchy) {

				String name = getConstraintTypeName(node);
				Concept rootSrc = hierarchy.getRootConcept();
				Concept rootTgt = getRootTargetConcept(node);

				ConstraintType type = loadSpecificType(node, name, rootSrc, rootTgt);

				Set<ConstraintSemantics> semanticsOpts = getSemanticsOptions(node);
				ImpliedValuesMultiplicity impValuesMult = getImpliedValuesMultiplicityOrNull(node);

				if (!semanticsOpts.isEmpty()) {

					type.setSemanticsOptions(semanticsOpts);
				}

				if (impValuesMult != null) {

					type.setImpliedValuesMultiplicity(impValuesMult);
				}

				return type;
			}

			private Set<ConstraintSemantics> getSemanticsOptions(KConfigNode allNode) {

				Set<ConstraintSemantics> options = new HashSet<ConstraintSemantics>();

				for (KConfigNode oneNode : allNode.getChildren(SEMANTICS_OPTION_TAG)) {

					options.add(getSemanticsOption(oneNode));
				}

				return options;
			}
		}

		private class SimpleConstraintTypesLoader extends ConstraintTypesLoader {

			String getTypeTag() {

				return SIMPLE_CONSTRAINT_TYPE_TAG;
			}

			ConstraintType loadSpecificType(KConfigNode node, String name, Concept rootSrc, Concept rootTgt) {

				EntityId lnkProp = getPropertyId(node, LINKING_PROPERTY_ATTR);

				return new SimpleConstraintType(name, lnkProp, rootSrc, rootTgt);
			}
		}

		private class AnchoredConstraintTypesLoader extends ConstraintTypesLoader {

			String getTypeTag() {

				return ANCHORED_CONSTRAINT_TYPE_TAG;
			}

			ConstraintType loadSpecificType(KConfigNode node, String name, Concept rootSrc, Concept rootTgt) {

				EntityId anchor = getConceptId(node, ANCHOR_CONCEPT_ATTR);

				EntityId srcProp = getPropertyId(node, SOURCE_PROPERTY_ATTR);
				EntityId tgtProp = getPropertyId(node, TARGET_PROPERTY_ATTR);

				return new AnchoredConstraintType(name, anchor, srcProp, tgtProp, rootSrc, rootTgt);
			}
		}

		CoreModelPopulator(Model model, Ontology ontology) {

			this.model = model;
			this.ontology = ontology;

			new DynamicHierarchiesLoader();
			new ReferenceOnlyHierarchiesLoader();

			new SimpleConstraintTypesLoader();
			new AnchoredConstraintTypesLoader();
		}

		private EntityId getRootConceptId(KConfigNode node) {

			return getPropertyId(node, ROOT_CONCEPT_ATTR);
		}

		private String getConstraintTypeName(KConfigNode node) {

			return node.getString(CONSTRAINT_TYPE_NAME_ATTR);
		}

		private Concept getRootTargetConcept(KConfigNode node) {

			return model.getHierarchy(getRootTargetConceptId(node)).getRootConcept();
		}

		private EntityId getRootTargetConceptId(KConfigNode node) {

			return getPropertyId(node, ROOT_TARGET_CONCEPT_ATTR);
		}

		private ImpliedValuesMultiplicity getImpliedValuesMultiplicityOrNull(KConfigNode node) {

			return node.getEnum(IMPLIED_VALUES_MULT_ATTR, ImpliedValuesMultiplicity.class, null);
		}

		private ConstraintSemantics getSemanticsOption(KConfigNode node) {

			return node.getEnum(SEMANTICS_OPTION_ATTR, ConstraintSemantics.class);
		}

		private EntityId getConceptId(KConfigNode node, String tag) {

			URI uri = node.getURI(tag);

			return model.createEntityId(uri, lookForConceptLabel(uri));
		}

		private EntityId getPropertyId(KConfigNode node, String tag) {

			return model.createEntityId(node.getURI(tag), null);
		}

		private String lookForConceptLabel(URI uri) {

			return ontology.lookForLabel(ontology.getClass(IRI.create(uri)));
		}
	}

	ConfigFileReader() {

		rootNode = new KConfigFile(CONFIG_FILE_NAME).getRootNode();
	}

	File getDynamicFile() {

		return rootNode.getResource(DYNAMIC_FILE_ATTR, KConfigResourceFinder.FILES);
	}

	String getDynamicNamespace() {

		return rootNode.getString(DYNAMIC_NAMESPACE_ATTR);
	}

	Model loadCoreModel(Ontology ontology) {

		Model model = new Model(getDynamicNamespace());

		new CoreModelPopulator(model, ontology);

		return model;
	}
}