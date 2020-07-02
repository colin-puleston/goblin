package uk.ac.manchester.cs.goblin.io;

import java.net.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon_util.config.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class CoreModelLoader extends ConfigFileVocab {

	private Model model;
	private Ontology ontology;

	private abstract class HierarchiesLoader {

		HierarchiesLoader(ModelSection section, KConfigNode node) {

			for (KConfigNode hierarchyNode : node.getChildren(getStatusTag())) {

				addHierarchy(section, hierarchyNode);
			}
		}

		abstract String getStatusTag();

		abstract Hierarchy addHierarchy(ModelSection section, EntityId rootConceptId);

		private void addHierarchy(ModelSection section, KConfigNode node) {

			Hierarchy hierarchy = addHierarchy(section, getRootConceptId(node));
			String name = getEntityNameOrNull(node);

			if (name != null) {

				hierarchy.setName(name);
			}
		}
	}

	private class DynamicHierarchiesLoader extends HierarchiesLoader {

		DynamicHierarchiesLoader(ModelSection section, KConfigNode node) {

			super(section, node);
		}

		String getStatusTag() {

			return DYNAMIC_HIERARCHY_TAG;
		}

		Hierarchy addHierarchy(ModelSection section, EntityId rootConceptId) {

			return section.addDynamicHierarchy(rootConceptId);
		}
	}

	private class ReferenceOnlyHierarchiesLoader extends HierarchiesLoader {

		ReferenceOnlyHierarchiesLoader(ModelSection section, KConfigNode node) {

			super(section, node);
		}

		String getStatusTag() {

			return REFERENCE_ONLY_HIERARCHY_TAG;
		}

		Hierarchy addHierarchy(ModelSection section, EntityId rootConceptId) {

			return section.addReferenceOnlyHierarchy(rootConceptId);
		}
	}

	private abstract class ConstraintTypesLoader {

		ConstraintTypesLoader(ModelSection section, KConfigNode node) {

			Iterator<Hierarchy> hierarchies = section.getDynamicHierarchies().iterator();

			for (KConfigNode hierarchyNode : node.getChildren(DYNAMIC_HIERARCHY_TAG)) {

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

			String name = getEntityName(node);
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

		SimpleConstraintTypesLoader(ModelSection section, KConfigNode node) {

			super(section, node);
		}

		String getTypeTag() {

			return SIMPLE_CONSTRAINT_TYPE_TAG;
		}

		ConstraintType loadSpecificType(KConfigNode node, String name, Concept rootSrc, Concept rootTgt) {

			EntityId lnkProp = getPropertyId(node, LINKING_PROPERTY_ATTR);

			return new SimpleConstraintType(name, lnkProp, rootSrc, rootTgt);
		}
	}

	private class AnchoredConstraintTypesLoader extends ConstraintTypesLoader {

		AnchoredConstraintTypesLoader(ModelSection section, KConfigNode node) {

			super(section, node);
		}

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

	CoreModelLoader(Model model, Ontology ontology) {

		this.model = model;
		this.ontology = ontology;
	}

	void load(KConfigNode rootNode) {

		for (KConfigNode sectionNode : rootNode.getChildren(MODEL_SECTION_TAG)) {

			loadSection(addSection(sectionNode), sectionNode);
		}
	}

	private void loadSection(ModelSection section, KConfigNode node) {

		new DynamicHierarchiesLoader(section, node);
		new ReferenceOnlyHierarchiesLoader(section, node);

		new SimpleConstraintTypesLoader(section, node);
		new AnchoredConstraintTypesLoader(section, node);
	}

	private ModelSection addSection(KConfigNode node) {

		String name = getEntityNameOrNull(node);

		return name != null ? model.addSection(name) : model.addSection();
	}

	private String getEntityName(KConfigNode node) {

		return node.getString(ENTITY_NAME_ATTR);
	}

	private String getEntityNameOrNull(KConfigNode node) {

		return node.getString(ENTITY_NAME_ATTR, null);
	}

	private EntityId getRootConceptId(KConfigNode node) {

		return getPropertyId(node, ROOT_CONCEPT_ATTR);
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