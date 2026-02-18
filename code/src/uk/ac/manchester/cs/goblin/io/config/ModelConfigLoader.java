package uk.ac.manchester.cs.goblin.io.config;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.config.*;

import uk.ac.manchester.cs.goblin.model.*;

import uk.ac.manchester.cs.goblin.io.ontology.*;
import uk.ac.manchester.cs.goblin.io.attribute.*;

/**
 * @author Colin Puleston
 */
class ModelConfigLoader extends ConfigFileVocab {

	private Ontology ontology;

	private ModelConfig model = new ModelConfig();

	private abstract class CoreAttributesLoader {

		void loadAll(ModelSectionConfig section, KConfigNode node) {

			Iterator<HierarchyConfig> hierarchies = section.getHierarchies().iterator();

			for (KConfigNode hierarchyNode : node.getChildren(HIERARCHY_TAG)) {

				loadHierarchyAttributes(hierarchyNode, hierarchies.next());
			}
		}

		abstract String getAttributeTag();

		abstract AttributeConfig loadAttribute(
									KConfigNode node,
									String label,
									EntityId rootSrcId,
									EntityId rootTgtId);

		private void loadHierarchyAttributes(KConfigNode hierarchyNode, HierarchyConfig hierarchy) {

			for (KConfigNode attributeNode : hierarchyNode.getChildren(getAttributeTag())) {

				hierarchy.addCoreAttribute(loadAttribute(attributeNode, hierarchy));
			}
		}

		private AttributeConfig loadAttribute(KConfigNode node, HierarchyConfig hierarchy) {

			String label = getEntityLabel(node);
			EntityId rootSrcId = hierarchy.getRootConceptId();
			EntityId rootTgtId = getRootTargetConceptId(node);

			return loadAttribute(node, label, rootSrcId, rootTgtId);
		}
	}

	private class SimpleAttributesLoader extends CoreAttributesLoader {

		SimpleAttributesLoader(ModelSectionConfig section, KConfigNode node) {

			loadAll(section, node);
		}

		String getAttributeTag() {

			return SIMPLE_CONSTRAINT_TYPE_TAG;
		}

		AttributeConfig loadAttribute(
							KConfigNode node,
							String label,
							EntityId rootSrcId,
							EntityId rootTgtId) {

			EntityId lnkProp = getPropertyId(node, LINKING_PROPERTY_ATTR);
			ConstraintsOption constraintsOpt = getCoreConstraintsOption(node);

			return new SimpleAttributeConfig(label, lnkProp, rootSrcId, rootTgtId, constraintsOpt);
		}
	}

	private class AnchoredAttributesLoader extends CoreAttributesLoader {

		AnchoredAttributesLoader(ModelSectionConfig section, KConfigNode node) {

			loadAll(section, node);
		}

		String getAttributeTag() {

			return ANCHORED_CONSTRAINT_TYPE_TAG;
		}

		AttributeConfig loadAttribute(
							KConfigNode node,
							String label,
							EntityId rootSrcId,
							EntityId rootTgtId) {

			EntityId anchor = getConceptId(node, ANCHOR_CONCEPT_ATTR);

			EntityId srcProp = getPropertyId(node, SOURCE_PROPERTY_ATTR);
			EntityId tgtProp = getPropertyId(node, TARGET_PROPERTY_ATTR);

			ConstraintsOption constraintsOpt = getCoreConstraintsOption(node);

			return new AnchoredAttributeConfig(label, anchor, srcProp, tgtProp, rootSrcId, rootTgtId, constraintsOpt);
		}
	}

	private class HierarchicalAttributesLoader extends CoreAttributesLoader {

		private KListMap<HierarchyConfig, Link> linksBySource
						= new KListMap<HierarchyConfig, Link>();

		private KSetMap<HierarchyConfig, EntityId> targetConstraintProps
						= new KSetMap<HierarchyConfig, EntityId>();

		private class Link {

			private HierarchyConfig source;
			private HierarchyConfig target;

			private Set<EntityId> sourceConstraintProps;

			Link(HierarchyConfig source, HierarchyConfig target) {

				this.source = source;
				this.target = target;

				sourceConstraintProps = getConstraintProps(source);

				checkHierarchyOrder();
			}

			void recursivelyCheckNoPropertyConstraintLoops() {

				recursivelyCheckNoPropertyConstraintLoops(target);
			}

			private void recursivelyCheckNoPropertyConstraintLoops(HierarchyConfig testTarget) {

				checkNoPropertyConstraintLoops(testTarget);

				for (Link link : linksBySource.getList(testTarget)) {

					recursivelyCheckNoPropertyConstraintLoops(link.target);
				}
			}

			private void checkNoPropertyConstraintLoops(HierarchyConfig testTarget) {

				Set<EntityId> commonProps = findCommonPropConstraintsWithSource(testTarget);

				if (!commonProps.isEmpty()) {

					throwException(
						"Potential conflicting constraints on properties: "
						+ commonProps);
				}
			}

			private void checkHierarchyOrder() {

				List<HierarchyConfig> all = model.getHierarchies();

				if (all.indexOf(source) > all.indexOf(target)) {

					throwException(
						"Source-hierarchy \"" + source.getLabel()
						+ " should be defined before target-hierarchy \"" + target.getLabel()
						+ " in config file");
				}
			}

			private Set<EntityId> findCommonPropConstraintsWithSource(HierarchyConfig testTarget) {

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

			private String getRootLabel(HierarchyConfig hierarchy) {

				return hierarchy.getRootConceptId().getLabel();
			}
		}

		HierarchicalAttributesLoader(ModelSectionConfig section, KConfigNode node) {

			loadAll(section, node);

			for (HierarchyConfig source : linksBySource.keySet()) {

				for (Link link : linksBySource.getList(source)) {

					link.recursivelyCheckNoPropertyConstraintLoops();
				}
			}
		}

		AttributeConfig loadAttribute(
							KConfigNode node,
							String label,
							EntityId rootSrcId,
							EntityId rootTgtId) {

			addLink(findHierarchy(rootSrcId), findHierarchy(rootTgtId));

			HierarchicalLinksOption linksOpt = getHierarchicalLinksOption(node);

			return new HierarchicalAttributeConfig(label, rootSrcId, rootTgtId, linksOpt);
		}

		String getAttributeTag() {

			return HIERARCHICAL_CONSTRAINT_TYPE_TAG;
		}

		private HierarchyConfig findHierarchy(EntityId rootConceptId) {

			for (HierarchyConfig hierarchy : model.getHierarchies()) {

				if (hierarchy.getRootConceptId().equals(rootConceptId)) {

					return hierarchy;
				}
			}

			throw new Error("Hierarchy not found with root concept: " + rootConceptId);
		}

		private void addLink(HierarchyConfig source, HierarchyConfig target) {

			linksBySource.add(source, new Link(source, target));
			targetConstraintProps.addAll(target, getConstraintProps(target));
		}

		private Set<EntityId> getConstraintProps(HierarchyConfig hierarchy) {

			Set<EntityId> props = new HashSet<EntityId>();

			for (AttributeConfig attribute : hierarchy.getCoreAttributes()) {

				if (attribute instanceof SimpleAttributeConfig) {

					props.add(((SimpleAttributeConfig)attribute).getLinkingPropertyId());
				}
				else if (attribute instanceof AnchoredAttributeConfig) {

					props.add(((AnchoredAttributeConfig)attribute).getTargetPropertyId());
				}
			}

			return props;
		}
	}

	ModelConfigLoader(Ontology ontology) {

		this.ontology = ontology;
	}

	ModelConfig load(KConfigNode rootNode) {

		loadHierarchies(rootNode);
		loadAttributes(rootNode);

		return model;
	}

	private void loadHierarchies(KConfigNode rootNode) {

		for (KConfigNode sectionNode : rootNode.getChildren(MODEL_SECTION_TAG)) {

			loadHierarchies(addSection(sectionNode), sectionNode);
		}
	}

	private void loadHierarchies(ModelSectionConfig section, KConfigNode node) {

		for (KConfigNode hierarchyNode : node.getChildren(HIERARCHY_TAG)) {

			loadHierarchy(section, hierarchyNode);
		}
	}

	private void loadHierarchy(ModelSectionConfig section, KConfigNode node) {

		EntityId rootConceptId = getRootConceptId(node);
		boolean refOnly = referenceOnlyHierarchy(node);
		String label = getEntityLabelOrNull(node);
		ConstraintsOption dynamicConstsOpt = getDynamicConstraintsOptionOrNull(node);

		HierarchyConfig hierarchy = section.addHierarchy(rootConceptId, refOnly);

		if (label != null) {

			hierarchy.setLabel(label);
		}

		if (dynamicConstsOpt != null) {

			hierarchy.enableDynamicAttributes(dynamicConstsOpt);
		}
	}

	private void loadAttributes(KConfigNode rootNode) {

		Iterator<ModelSectionConfig> sections = model.getSections().iterator();

		for (KConfigNode sectionNode : rootNode.getChildren(MODEL_SECTION_TAG)) {

			ModelSectionConfig section = sections.next();

			new SimpleAttributesLoader(section, sectionNode);
			new AnchoredAttributesLoader(section, sectionNode);
			new HierarchicalAttributesLoader(section, sectionNode);
		}
	}

	private ModelSectionConfig addSection(KConfigNode node) {

		String label = getEntityLabelOrNull(node);

		return label != null ? model.addSection(label) : model.addSection();
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

	private EntityId getRootTargetConceptId(KConfigNode node) {

		return getConceptId(node, ROOT_TARGET_CONCEPT_ATTR);
	}

	private ConstraintsOption getCoreConstraintsOption(KConfigNode node) {

		return node.getEnum(CORE_CONSTRAINTS_OPTION_ATTR, ConstraintsOption.class);
	}

	private ConstraintsOption getDynamicConstraintsOptionOrNull(KConfigNode node) {

		return node.getEnum(DYNAMIC_CONSTRAINTS_OPTION_ATTR, ConstraintsOption.class, null);
	}

	private HierarchicalLinksOption getHierarchicalLinksOption(KConfigNode node) {

		return node.getEnum(HIERARCHICAL_LINKS_OPTION_ATTR, HierarchicalLinksOption.class);
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