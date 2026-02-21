package uk.ac.manchester.cs.goblin.io.config;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.ontology.*;

/**
 * @author Colin Puleston
 */
class ModelConfigLoader extends ConfigFileSerialiser {

	private Ontology ontology;

	private ModelConfig model = new ModelConfig();

	private abstract class CoreAttributesLoader {

		void loadAll(ModelSectionConfig section, XNode node) {

			Iterator<CoreHierarchyConfig> hierarchies = section.getHierarchies().iterator();

			for (XNode hierarchyNode : node.getChildren(HIERARCHY_TAG)) {

				loadHierarchyAttributes(hierarchyNode, hierarchies.next());
			}
		}

		abstract String getAttributeTag();

		abstract CoreAttributeConfig loadAttribute(
										XNode node,
										String label,
										EntityId rootSrcId,
										EntityId rootTgtId);

		private void loadHierarchyAttributes(XNode hierarchyNode, CoreHierarchyConfig hierarchy) {

			for (XNode attributeNode : hierarchyNode.getChildren(getAttributeTag())) {

				hierarchy.addCoreAttribute(loadAttribute(attributeNode, hierarchy));
			}
		}

		private CoreAttributeConfig loadAttribute(XNode node, CoreHierarchyConfig hierarchy) {

			String label = getEntityLabel(node);
			EntityId rootSrcId = hierarchy.getRootConceptId();
			EntityId rootTgtId = getRootTargetConceptId(node);

			return loadAttribute(node, label, rootSrcId, rootTgtId);
		}
	}

	private class SimpleAttributesLoader extends CoreAttributesLoader {

		SimpleAttributesLoader(ModelSectionConfig section, XNode node) {

			loadAll(section, node);
		}

		String getAttributeTag() {

			return SIMPLE_ATTRIBUTE_TAG;
		}

		CoreAttributeConfig loadAttribute(
								XNode node,
								String label,
								EntityId rootSrcId,
								EntityId rootTgtId) {

			EntityId lnkProp = getPropertyId(node, LINKING_PROPERTY_ATTR);
			ConstraintsOption constraintsOpt = getPropertyAttributeConstraintsOption(node);

			return new SimpleAttributeConfig(label, lnkProp, rootSrcId, rootTgtId, constraintsOpt);
		}
	}

	private class AnchoredAttributesLoader extends CoreAttributesLoader {

		AnchoredAttributesLoader(ModelSectionConfig section, XNode node) {

			loadAll(section, node);
		}

		String getAttributeTag() {

			return ANCHORED_ATTRIBUTE_TAG;
		}

		CoreAttributeConfig loadAttribute(
								XNode node,
								String label,
								EntityId rootSrcId,
								EntityId rootTgtId) {

			EntityId anchor = getConceptId(node, ANCHOR_CONCEPT_ATTR);

			EntityId srcProp = getPropertyId(node, SOURCE_PROPERTY_ATTR);
			EntityId tgtProp = getPropertyId(node, TARGET_PROPERTY_ATTR);

			ConstraintsOption constraintsOpt = getPropertyAttributeConstraintsOption(node);

			return new AnchoredAttributeConfig(label, anchor, srcProp, tgtProp, rootSrcId, rootTgtId, constraintsOpt);
		}
	}

	private class HierarchicalAttributesLoader extends CoreAttributesLoader {

		private KListMap<CoreHierarchyConfig, Link> linksBySource
						= new KListMap<CoreHierarchyConfig, Link>();

		private KSetMap<CoreHierarchyConfig, EntityId> targetConstraintProperties
						= new KSetMap<CoreHierarchyConfig, EntityId>();

		private class Link {

			private CoreHierarchyConfig source;
			private CoreHierarchyConfig target;

			private Set<EntityId> sourceConstraintProperties;

			Link(CoreHierarchyConfig source, CoreHierarchyConfig target) {

				this.source = source;
				this.target = target;

				sourceConstraintProperties = getAllConstraintProperties(source);

				checkHierarchyOrder();
			}

			void recursivelyCheckNoPropertyConstraintLoops() {

				recursivelyCheckNoPropertyConstraintLoops(target);
			}

			private void recursivelyCheckNoPropertyConstraintLoops(CoreHierarchyConfig testTarget) {

				checkNoPropertyConstraintLoops(testTarget);

				for (Link link : linksBySource.getList(testTarget)) {

					recursivelyCheckNoPropertyConstraintLoops(link.target);
				}
			}

			private void checkNoPropertyConstraintLoops(CoreHierarchyConfig testTarget) {

				Set<EntityId> commonProps = findCommonPropConstraintsWithSource(testTarget);

				if (!commonProps.isEmpty()) {

					throwException(
						"Potential conflicting constraints on properties: "
						+ commonProps);
				}
			}

			private void checkHierarchyOrder() {

				List<CoreHierarchyConfig> all = model.getHierarchies();

				if (all.indexOf(source) > all.indexOf(target)) {

					throwException(
						"Source-hierarchy \"" + source.getLabel()
						+ " should be defined before target-hierarchy \"" + target.getLabel()
						+ " in config file");
				}
			}

			private Set<EntityId> findCommonPropConstraintsWithSource(CoreHierarchyConfig testTarget) {

				Set<EntityId> tgtConstProps = targetConstraintProperties.getSet(testTarget);
				Set<EntityId> commonProps = new HashSet<EntityId>(tgtConstProps);

				commonProps.retainAll(sourceConstraintProperties);

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

			private String getRootLabel(CoreHierarchyConfig hierarchy) {

				return hierarchy.getRootConceptId().getLabel();
			}
		}

		private class ConstraintPropertyCollector extends CoreAttributeConfigVisitor {

			final Set<EntityId> properties = new HashSet<EntityId>();

			public void visit(SimpleAttributeConfig config) {

				properties.add(config.getLinkingPropertyId());
			}

			public void visit(AnchoredAttributeConfig config) {

				properties.add(config.getTargetPropertyId());
			}

			public void visit(HierarchicalAttributeConfig config) {
			}

			ConstraintPropertyCollector(CoreHierarchyConfig hierarchy) {

				for (CoreAttributeConfig attribute : hierarchy.getCoreAttributes()) {

					visit(attribute);
				}
			}
		}

		HierarchicalAttributesLoader(ModelSectionConfig section, XNode node) {

			loadAll(section, node);

			for (CoreHierarchyConfig source : linksBySource.keySet()) {

				for (Link link : linksBySource.getList(source)) {

					link.recursivelyCheckNoPropertyConstraintLoops();
				}
			}
		}

		CoreAttributeConfig loadAttribute(
								XNode node,
								String label,
								EntityId rootSrcId,
								EntityId rootTgtId) {

			addLink(findHierarchy(rootSrcId), findHierarchy(rootTgtId));

			HierarchicalLinksOption linksOpt = getHierarchicalLinksOption(node);

			return new HierarchicalAttributeConfig(label, rootSrcId, rootTgtId, linksOpt);
		}

		String getAttributeTag() {

			return HIERARCHICAL_ATTRIBUTE_TAG;
		}

		private CoreHierarchyConfig findHierarchy(EntityId rootConceptId) {

			for (CoreHierarchyConfig hierarchy : model.getHierarchies()) {

				if (hierarchy.getRootConceptId().equals(rootConceptId)) {

					return hierarchy;
				}
			}

			throw new Error("Hierarchy not found with root concept: " + rootConceptId);
		}

		private void addLink(CoreHierarchyConfig source, CoreHierarchyConfig target) {

			linksBySource.add(source, new Link(source, target));
			targetConstraintProperties.addAll(target, getAllConstraintProperties(target));
		}

		private Set<EntityId> getAllConstraintProperties(CoreHierarchyConfig hierarchy) {

			return new ConstraintPropertyCollector(hierarchy).properties;
		}
	}

	ModelConfigLoader(Ontology ontology) {

		this.ontology = ontology;
	}

	ModelConfig load(XNode rootNode) {

		loadHierarchies(rootNode);
		loadAttributes(rootNode);

		return model;
	}

	private void loadHierarchies(XNode rootNode) {

		for (XNode sectionNode : rootNode.getChildren(SECTION_TAG)) {

			loadHierarchies(addSection(sectionNode), sectionNode);
		}
	}

	private void loadHierarchies(ModelSectionConfig section, XNode node) {

		for (XNode hierarchyNode : node.getChildren(HIERARCHY_TAG)) {

			loadHierarchy(section, hierarchyNode);
		}
	}

	private void loadHierarchy(ModelSectionConfig section, XNode node) {

		EntityId rootConceptId = getRootConceptId(node);
		String label = getEntityLabelOrNull(node);

		CoreHierarchyConfig hierarchy = section.addHierarchy(rootConceptId);

		if (label != null) {

			hierarchy.setLabel(label);
		}

		hierarchy.setFixedStructure(fixedHierarchyStructure(node));
		hierarchy.setDynamicAttributeConstraints(getDynamicAttributeConstraintsOption(node));
	}

	private void loadAttributes(XNode rootNode) {

		Iterator<ModelSectionConfig> sections = model.getSections().iterator();

		for (XNode sectionNode : rootNode.getChildren(SECTION_TAG)) {

			ModelSectionConfig section = sections.next();

			new SimpleAttributesLoader(section, sectionNode);
			new AnchoredAttributesLoader(section, sectionNode);
			new HierarchicalAttributesLoader(section, sectionNode);
		}
	}

	private ModelSectionConfig addSection(XNode node) {

		String label = getEntityLabelOrNull(node);

		return label != null ? model.addSection(label) : model.addSection();
	}

	private String getEntityLabel(XNode node) {

		return node.getString(ENTITY_LABEL_ATTR);
	}

	private String getEntityLabelOrNull(XNode node) {

		return node.getString(ENTITY_LABEL_ATTR, null);
	}

	private EntityId getRootConceptId(XNode node) {

		return getPropertyId(node, ROOT_CONCEPT_ATTR);
	}

	private boolean fixedHierarchyStructure(XNode node) {

		return node.getBoolean(FIXED_HIERARCHY_STRUCTURE_ATTR, false);
	}

	private EntityId getRootTargetConceptId(XNode node) {

		return getConceptId(node, ROOT_TARGET_CONCEPT_ATTR);
	}

	private ConstraintsOption getPropertyAttributeConstraintsOption(XNode node) {

		return node.getEnum(
					PROPERTY_ATTR_CONSTRAINTS_OPT_ATTR,
					ConstraintsOption.class);
	}

	private ConstraintsOption getDynamicAttributeConstraintsOption(XNode node) {

		return node.getEnum(
					DYNAMIC_ATTR_CONSTRAINTS_OPT_ATTR,
					ConstraintsOption.class,
					ConstraintsOption.NONE);
	}

	private HierarchicalLinksOption getHierarchicalLinksOption(XNode node) {

		return node.getEnum(HIERARCHICAL_LINKS_OPT_ATTR, HierarchicalLinksOption.class);
	}

	private EntityId getConceptId(XNode node, String tag) {

		return getCoreId(ontology.getClass(getIRI(node, tag)));
	}

	private EntityId getPropertyId(XNode node, String tag) {

		return getCoreId(ontology.getObjectProperty(getIRI(node, tag)));
	}

	private EntityId getCoreId(OWLEntity entity) {

		return new CoreId(entity.getIRI(), ontology.lookForLabel(entity));
	}

	private IRI getIRI(XNode node, String tag) {

		return IRI.create(node.getURI(tag));
	}
}