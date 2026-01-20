package uk.ac.manchester.cs.goblin.io;

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

	private abstract class CoreAttributesLoader {

		void loadAll(ModelSection section, KConfigNode node) {

			Iterator<Hierarchy> hierarchies = section.getCoreHierarchies().iterator();

			for (KConfigNode hierarchyNode : node.getChildren(HIERARCHY_TAG)) {

				Hierarchy hierarchy = hierarchies.next();

				if (!hierarchy.referenceOnly()) {

					loadHierarchyAttributes(hierarchyNode, hierarchy);
				}
			}
		}

		abstract String getAttributeTag();

		abstract Attribute loadAttribute(
								KConfigNode node,
								String label,
								Concept rootSrc,
								Concept rootTgt);

		private void loadHierarchyAttributes(KConfigNode hierarchyNode, Hierarchy hierarchy) {

			for (KConfigNode attributeNode : hierarchyNode.getChildren(getAttributeTag())) {

				hierarchy.addCoreAttribute(loadAttribute(attributeNode, hierarchy));
			}
		}

		private Attribute loadAttribute(KConfigNode node, Hierarchy hierarchy) {

			String label = getEntityLabel(node);
			Concept rootSrc = hierarchy.getRootConcept();
			Concept rootTgt = getRootTargetConcept(node);

			return loadAttribute(node, label, rootSrc, rootTgt);
		}
	}

	private abstract class PropertyAttributesLoader extends CoreAttributesLoader {

		Attribute loadAttribute(KConfigNode node, String label, Concept rootSrc, Concept rootTgt) {

			PropertyAttribute attribute = loadPropertyAttribute(node, label, rootSrc, rootTgt);

			Set<ConstraintSemantics> semanticsOpts = getSemanticsOptions(node);

			if (!semanticsOpts.isEmpty()) {

				attribute.setSemanticsOptions(semanticsOpts);
			}

			attribute.setSingleImpliedValues(getSingleImpliedValues(node));

			return attribute;
		}

		abstract PropertyAttribute loadPropertyAttribute(
										KConfigNode node,
										String label,
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

	private class SimpleAttributesLoader extends PropertyAttributesLoader {

		SimpleAttributesLoader(ModelSection section, KConfigNode node) {

			loadAll(section, node);
		}

		String getAttributeTag() {

			return SIMPLE_CONSTRAINT_TYPE_TAG;
		}

		PropertyAttribute loadPropertyAttribute(
								KConfigNode node,
								String label,
								Concept rootSrc,
								Concept rootTgt) {

			EntityId lnkProp = getPropertyId(node, LINKING_PROPERTY_ATTR);

			return new SimpleAttribute(label, lnkProp, rootSrc, rootTgt);
		}
	}

	private class AnchoredAttributesLoader extends PropertyAttributesLoader {

		AnchoredAttributesLoader(ModelSection section, KConfigNode node) {

			loadAll(section, node);
		}

		String getAttributeTag() {

			return ANCHORED_CONSTRAINT_TYPE_TAG;
		}

		PropertyAttribute loadPropertyAttribute(
								KConfigNode node,
								String label,
								Concept rootSrc,
								Concept rootTgt) {

			EntityId anchor = getConceptId(node, ANCHOR_CONCEPT_ATTR);

			EntityId srcProp = getPropertyId(node, SOURCE_PROPERTY_ATTR);
			EntityId tgtProp = getPropertyId(node, TARGET_PROPERTY_ATTR);

			return new AnchoredAttribute(label, anchor, srcProp, tgtProp, rootSrc, rootTgt);
		}
	}

	private class HierarchicalAttributesLoader extends CoreAttributesLoader {

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
							"Cannot create hierarchical attribute: "
							+ describeAttribute()
							+ ": " + specificMsg);
			}

			private String describeAttribute() {

				return "[" + getRootLabel(source) + " -> " + getRootLabel(target) + "]";
			}

			private String getRootLabel(Hierarchy hierarchy) {

				return hierarchy.getRootConcept().getConceptId().getLabel();
			}
		}

		HierarchicalAttributesLoader(ModelSection section, KConfigNode node) {

			loadAll(section, node);

			for (Hierarchy source : linksBySource.keySet()) {

				for (Link link : linksBySource.getList(source)) {

					link.recursivelyCheckNoPropertyConstraintLoops();
				}
			}
		}

		Attribute loadAttribute(KConfigNode node, String label, Concept rootSrc, Concept rootTgt) {

			addLink(getHierarchy(rootSrc), getHierarchy(rootTgt));

			return new HierarchicalAttribute(label, rootSrc, rootTgt);
		}

		String getAttributeTag() {

			return HIERARCHICAL_CONSTRAINT_TYPE_TAG;
		}

		private void addLink(Hierarchy source, Hierarchy target) {

			linksBySource.add(source, new Link(source, target));
			targetConstraintProps.addAll(target, getConstraintProps(target));
		}

		private Set<EntityId> getConstraintProps(Hierarchy hierarchy) {

			Set<EntityId> props = new HashSet<EntityId>();

			for (Attribute attribute : hierarchy.getCoreAttributes()) {

				if (attribute instanceof PropertyAttribute) {

					props.add(((PropertyAttribute)attribute).getTargetPropertyId());
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

		String label = getEntityLabelOrNull(node);

		return label != null ? model.addSection(label) : model.addSection();
	}

	private void loadSection(ModelSection section, KConfigNode node) {

		loadHierarchies(section, node);

		new SimpleAttributesLoader(section, node);
		new AnchoredAttributesLoader(section, node);
		new HierarchicalAttributesLoader(section, node);
	}

	private void loadHierarchies(ModelSection section, KConfigNode node) {

		for (KConfigNode hierarchyNode : node.getChildren(HIERARCHY_TAG)) {

			loadHierarchy(section, hierarchyNode);
		}
	}

	private void loadHierarchy(ModelSection section, KConfigNode node) {

		EntityId rootConceptId = getRootConceptId(node);
		boolean refOnly = referenceOnlyHierarchy(node);
		String label = getEntityLabelOrNull(node);

		Hierarchy hierarchy = section.addCoreHierarchy(rootConceptId, refOnly);

		if (label != null) {

			hierarchy.setLabel(label);
		}
	}

	private String getEntityLabel(KConfigNode node) {

		return node.getString(ENTITY_LABEL_ATTR);
	}

	private String getEntityLabelOrNull(KConfigNode node) {

		return node.getString(ENTITY_LABEL_ATTR, null);
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

		return getCoreId(ontology.getClass(getIRI(node, tag)));
	}

	private EntityId getPropertyId(KConfigNode node, String tag) {

		return getCoreId(ontology.getObjectProperty(getIRI(node, tag)));
	}

	private EntityId getCoreId(OWLEntity entity) {

		return new CoreId(entity.getIRI(), ontology.lookForLabel(entity));
	}

	private IRI getIRI(KConfigNode node, String tag) {

		return IRI.create(node.getURI(tag));
	}
}