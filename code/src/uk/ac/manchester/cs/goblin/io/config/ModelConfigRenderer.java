package uk.ac.manchester.cs.goblin.io.config;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.ontology.*;

/**
 * @author Colin Puleston
 */
class ModelConfigRenderer extends ConfigFileSerialiser {

	private XNode rootNode;
	private EntityIds entityIds;

	private class AttributeRenderer extends CoreAttributeConfigVisitor {

		private XNode parentNode;

		public void visit(SimpleAttributeConfig config) {

			XNode node = renderCommom(config, SIMPLE_ATTRIBUTE_TAG);

			renderEntityIRI(node, LINKING_PROPERTY_ATTR, config.getLinkingPropertyId());

			renderConstraintsOption(config, node);
		}

		public void visit(AnchoredAttributeConfig config) {

			XNode node = renderCommom(config, ANCHORED_ATTRIBUTE_TAG);

			renderEntityIRI(node, ANCHOR_CONCEPT_ATTR, config.getAnchorConceptId());
			renderEntityIRI(node, SOURCE_PROPERTY_ATTR, config.getSourcePropertyId());
			renderEntityIRI(node, TARGET_PROPERTY_ATTR, config.getTargetPropertyId());

			renderConstraintsOption(config, node);
		}

		public void visit(HierarchicalAttributeConfig config) {

			XNode node = renderCommom(config, HIERARCHICAL_ATTRIBUTE_TAG);

			node.setValue(HIERARCHICAL_LINKS_OPT_ATTR, config.getLinksOption());
		}

		AttributeRenderer(XNode parentNode) {

			this.parentNode = parentNode;
		}

		private XNode renderCommom(CoreAttributeConfig config, String tag) {

			XNode node = parentNode.addChild(tag);

			renderLabel(node, config.getLabel());

			renderEntityIRI(node, ROOT_TARGET_CONCEPT_ATTR, config.getRootTargetConceptId());

			return node;
		}

		private void renderConstraintsOption(PropertyAttributeConfig config, XNode node) {

			node.setValue(PROPERTY_ATTR_CONSTRAINTS_OPT_ATTR, config.getConstraintsOption());
		}
	}

	ModelConfigRenderer(XNode rootNode, DynamicIRIs dynamicIRIs) {

		this.rootNode = rootNode;

		entityIds = new EntityIds(dynamicIRIs);
	}

	void render(ModelConfig model) {

		for (ModelSectionConfig section : model.getSections()) {

			renderSection(section, rootNode.addChild(SECTION_TAG));
		}
	}

	private void renderSection(ModelSectionConfig section, XNode node) {

		renderLabel(node, section.getLabel());

		for (CoreHierarchyConfig hierarchy : section.getHierarchies()) {

			renderHierarchy(hierarchy, node.addChild(HIERARCHY_TAG));
		}
	}

	private void renderHierarchy(CoreHierarchyConfig hierarchy, XNode node) {

		renderLabel(node, hierarchy.getLabel());

		renderEntityIRI(node, ROOT_CONCEPT_ATTR, hierarchy.getRootConceptId());
		node.setValue(FIXED_HIERARCHY_STRUCTURE_ATTR, hierarchy.fixedStructure());
		node.setValue(
				DYNAMIC_ATTR_CONSTRAINTS_OPT_ATTR,
				hierarchy.getDynamicAttributeConstraintsOption());

		for (CoreAttributeConfig attribute : hierarchy.getCoreAttributes()) {

			new AttributeRenderer(node).visit(attribute);
		}
	}

	private void renderLabel(XNode node, String label) {

		node.setValue(ENTITY_LABEL_ATTR, label);
	}

	private void renderEntityIRI(XNode node, String tag, EntityId id) {

		node.setValue(tag, entityIds.toIRI(id));
	}
}