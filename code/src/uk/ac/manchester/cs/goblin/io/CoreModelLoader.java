package uk.ac.manchester.cs.goblin.io;

import java.net.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.config.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class CoreModelLoader extends ConfigFileVocab {

	private Model model;
	private Ontology ontology;

	private abstract class ConstraintTypesLoader {

		void loadAll(ModelSection section, KConfigNode node) {

			Iterator<Hierarchy> hierarchies = section.getDynamicHierarchies().iterator();

			for (KConfigNode hierarchyNode : node.getChildren(HIERARCHY_TAG)) {

				if (!referenceOnlyHierarchy(hierarchyNode)) {

					loadHierarchyTypes(hierarchyNode, hierarchies.next());
				}
			}
		}

		abstract String getTypeTag();

		abstract ConstraintType loadType(
									KConfigNode node,
									String name,
									Concept rootSrc,
									Concept rootTgt);

		private void loadHierarchyTypes(KConfigNode hierarchyNode, Hierarchy hierarchy) {

			for (KConfigNode typeNode : hierarchyNode.getChildren(getTypeTag())) {

				hierarchy.addCoreConstraintType(loadType(typeNode, hierarchy));
			}
		}

		private ConstraintType loadType(KConfigNode node, Hierarchy hierarchy) {

			String name = getEntityName(node);
			Concept rootSrc = hierarchy.getRootConcept();
			Concept rootTgt = getRootTargetConcept(node);

			return loadType(node, name, rootSrc, rootTgt);
		}
	}

	private abstract class PropertyConstraintTypesLoader extends ConstraintTypesLoader {

		ConstraintType loadType(KConfigNode node, String name, Concept rootSrc, Concept rootTgt) {

			CorePropertyConstraintType type = loadPropertyType(node, name, rootSrc, rootTgt);

			Set<ConstraintSemantics> semanticsOpts = getSemanticsOptions(node);

			if (!semanticsOpts.isEmpty()) {

				type.setSemanticsOptions(semanticsOpts);
			}

			type.setSingleImpliedValues(getSingleImpliedValues(node));

			return type;
		}

		abstract CorePropertyConstraintType loadPropertyType(
												KConfigNode node,
												String name,
												Concept rootSrc,
												Concept rootTgt);

		private Set<ConstraintSemantics> getSemanticsOptions(KConfigNode allNode) {

			Set<ConstraintSemantics> options = new HashSet<ConstraintSemantics>();

			for (KConfigNode oneNode : allNode.getChildren(SEMANTICS_OPTION_TAG)) {

				options.add(getSemanticsOption(oneNode));
			}

			return options;
		}
	}

	private class SimpleConstraintTypesLoader extends PropertyConstraintTypesLoader {

		SimpleConstraintTypesLoader(ModelSection section, KConfigNode node) {

			loadAll(section, node);
		}

		String getTypeTag() {

			return SIMPLE_CONSTRAINT_TYPE_TAG;
		}

		CorePropertyConstraintType loadPropertyType(
										KConfigNode node,
										String name,
										Concept rootSrc,
										Concept rootTgt) {

			EntityId lnkProp = getPropertyId(node, LINKING_PROPERTY_ATTR);

			return new SimpleConstraintType(name, lnkProp, rootSrc, rootTgt);
		}
	}

	private class AnchoredConstraintTypesLoader extends PropertyConstraintTypesLoader {

		AnchoredConstraintTypesLoader(ModelSection section, KConfigNode node) {

			loadAll(section, node);
		}

		String getTypeTag() {

			return ANCHORED_CONSTRAINT_TYPE_TAG;
		}

		CorePropertyConstraintType loadPropertyType(
										KConfigNode node,
										String name,
										Concept rootSrc,
										Concept rootTgt) {

			EntityId anchor = getConceptId(node, ANCHOR_CONCEPT_ATTR);

			EntityId srcProp = getPropertyId(node, SOURCE_PROPERTY_ATTR);
			EntityId tgtProp = getPropertyId(node, TARGET_PROPERTY_ATTR);

			return new AnchoredConstraintType(name, anchor, srcProp, tgtProp, rootSrc, rootTgt);
		}
	}

	private class HierarchicalConstraintTypesLoader extends ConstraintTypesLoader {

		private KListMap<Hierarchy, Link> linksBySource = new KListMap<Hierarchy, Link>();
		private KSetMap<Hierarchy, EntityId> targetConstraintProps = new KSetMap<Hierarchy, EntityId>();

		private class Link {

			private Hierarchy source;
			private Hierarchy target;

			private Set<EntityId> sourceConstraintProps;

			Link(Hierarchy source, Hierarchy target) {

				this.source = source;
				this.target = target;

				sourceConstraintProps = getConstraintProps(source);

				checkHierarchyOrder();
			}

			void recursivelyCheckNoPropertyConstraintLoops() {

				recursivelyCheckNoPropertyConstraintLoops(target);
			}

			private void recursivelyCheckNoPropertyConstraintLoops(Hierarchy testTarget) {

				checkNoPropertyConstraintLoops(testTarget);

				for (Link link : linksBySource.getList(testTarget)) {

					recursivelyCheckNoPropertyConstraintLoops(link.target);
				}
			}

			private void checkNoPropertyConstraintLoops(Hierarchy testTarget) {

				Set<EntityId> commonProps = findCommonPropConstraintsWithSource(testTarget);

				if (!commonProps.isEmpty()) {

					throwException(
						"Potential conflicting constraints on properties: "
						+ commonProps);
				}
			}

			private void checkHierarchyOrder() {

				List<Hierarchy> all = model.getAllHierarchies();

				if (all.indexOf(source) > all.indexOf(target)) {

					throwException(
						"Source-hierarchy must be defined before "
						+ "target-hierarchy in config file");
				}
			}

			private Set<EntityId> findCommonPropConstraintsWithSource(Hierarchy testTarget) {

				Set<EntityId> tgtConstProps = targetConstraintProps.getSet(testTarget);
				Set<EntityId> commonProps = new HashSet<EntityId>(tgtConstProps);

				commonProps.retainAll(sourceConstraintProps);

				return commonProps;
			}

			private void throwException(String specificMsg) {

				throw new RuntimeException(
							"Cannot create hierarchical constraint-type: "
							+ describeConstraintType()
							+ ": " + specificMsg);
			}

			private String describeConstraintType() {

				return "[" + getRootLabel(source) + " -> " + getRootLabel(target) + "]";
			}

			private String getRootLabel(Hierarchy hierarchy) {

				return hierarchy.getRootConcept().getConceptId().getLabel();
			}
		}

		HierarchicalConstraintTypesLoader(ModelSection section, KConfigNode node) {

			loadAll(section, node);

			for (Hierarchy source : linksBySource.keySet()) {

				for (Link link : linksBySource.getList(source)) {

					link.recursivelyCheckNoPropertyConstraintLoops();
				}
			}
		}

		ConstraintType loadType(KConfigNode node, String name, Concept rootSrc, Concept rootTgt) {

			addLink(getHierarchy(rootSrc), getHierarchy(rootTgt));

			return new HierarchicalConstraintType(name, rootSrc, rootTgt);
		}

		String getTypeTag() {

			return HIERARCHICAL_CONSTRAINT_TYPE_TAG;
		}

		private void addLink(Hierarchy source, Hierarchy target) {

			linksBySource.add(source, new Link(source, target));
			targetConstraintProps.addAll(target, getConstraintProps(target));
		}

		private Set<EntityId> getConstraintProps(Hierarchy hierarchy) {

			Set<EntityId> props = new HashSet<EntityId>();

			for (ConstraintType type : hierarchy.getCoreConstraintTypes()) {

				if (type instanceof PropertyConstraintType) {

					props.add(((PropertyConstraintType)type).getTargetPropertyId());
				}
			}

			return props;
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

	private ModelSection addSection(KConfigNode node) {

		String name = getEntityNameOrNull(node);

		return name != null ? model.addSection(name) : model.addSection();
	}

	private void loadSection(ModelSection section, KConfigNode node) {

		loadHierarchies(section, node);

		new SimpleConstraintTypesLoader(section, node);
		new AnchoredConstraintTypesLoader(section, node);
		new HierarchicalConstraintTypesLoader(section, node);
	}

	private void loadHierarchies(ModelSection section, KConfigNode node) {

		for (KConfigNode hierarchyNode : node.getChildren(HIERARCHY_TAG)) {

			loadHierarchy(section, hierarchyNode);
		}
	}

	private void loadHierarchy(ModelSection section, KConfigNode node) {

		EntityId rootConceptId = getRootConceptId(node);
		boolean refOnly = referenceOnlyHierarchy(node);
		String name = getEntityNameOrNull(node);

		Hierarchy hierarchy = section.addCoreHierarchy(rootConceptId, refOnly);

		if (name != null) {

			hierarchy.setName(name);
		}
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

	private boolean referenceOnlyHierarchy(KConfigNode node) {

		return node.getBoolean(REFERENCE_ONLY_HIERARCHY_ATTR, false);
	}

	private Concept getRootTargetConcept(KConfigNode node) {

		return getHierarchy(getRootTargetConceptId(node)).getRootConcept();
	}

	private EntityId getRootTargetConceptId(KConfigNode node) {

		return getPropertyId(node, ROOT_TARGET_CONCEPT_ATTR);
	}

	private ConstraintSemantics getSemanticsOption(KConfigNode node) {

		return node.getEnum(SEMANTICS_OPTION_ATTR, ConstraintSemantics.class);
	}

	private boolean getSingleImpliedValues(KConfigNode node) {

		return node.getBoolean(SINGLE_IMPLIED_VALUES_ATTR, false);
	}

	private Hierarchy getHierarchy(Concept rootConcept) {

		return getHierarchy(rootConcept.getConceptId());
	}

	private Hierarchy getHierarchy(EntityId rootConceptId) {

		return model.getHierarchy(rootConceptId);
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