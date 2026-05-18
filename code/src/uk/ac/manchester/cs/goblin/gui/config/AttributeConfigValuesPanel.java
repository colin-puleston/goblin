/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.goblin.gui.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class AttributeConfigValuesPanel extends ValuesPanel {

	static private final long serialVersionUID = -1;

	private EditManager editManager;
	private CoreHierarchyConfig sourceHierarchy;

	private TypeValues values;

	private abstract class TypeValues {

		private TargetHierarchy targetHierarchy;

		private class AttributeTypeInfo extends InfoValue {

			String getTitle() {

				return "Attribute type";
			}

			String get() {

				return getAttributeType().toString();
			}
		}

		private class TargetHierarchy extends HierarchyValue {

			String getTitle() {

				return "Target hierarchy";
			}

			void set(CoreAttributeConfig attribute) {

				set(attribute.getRootTargetConceptId());
			}

			List<CoreHierarchyConfig> getOptions() {

				List<CoreHierarchyConfig> options = super.getOptions();

				checkDoctorTargetHierarchyOptions(options);

				return options;
			}
		}

		TypeValues() {

			values = this;

			new AttributeTypeInfo();

			targetHierarchy = new TargetHierarchy();
		}

		void setAll(CoreAttributeConfig attribute) {

			targetHierarchy.set(attribute);
		}

		void checkDoctorTargetHierarchyOptions(List<CoreHierarchyConfig> options) {
		}

		CoreAttributeConfig createAttribute() {

			return createAttribute(getRootTargetConceptId());
		}

		abstract CoreAttributeConfig createAttribute(EntityId rootTargetConceptId);

		void updateAttribute(CoreAttributeConfig config) {

			config.resetRootTargetConceptId(getRootTargetConceptId());
		}

		abstract AttributeType getAttributeType();

		private EntityId getRootTargetConceptId() {

			return targetHierarchy.get().getRootConceptId();
		}
	}

	private abstract class PropertyAttributeValues extends TypeValues {

		abstract class TargetLinkPropertyId extends PropertyIdValue {

			ConfigEntitySelectorDialog createSelectorDialog(ConfigOntology ontology) {

				ConfigEntitySelectorDialog dialog = super.createSelectorDialog(ontology);

				dialog.setExclusionSeedEntityIds(getCurrentSiblingAttributeLinkProperties());

				return dialog;
			}

			private List<EntityId> getCurrentSiblingAttributeLinkProperties() {

				List<EntityId> props = new ArrayList<EntityId>();

				for (CoreAttributeConfig attr : sourceHierarchy.getCoreAttributes()) {

					if (attr instanceof SimpleAttributeConfig) {

						props.add(((SimpleAttributeConfig)attr).getLinkingPropertyId());
					}
					else if (attr instanceof AnchoredAttributeConfig) {

						props.add(((AnchoredAttributeConfig)attr).getTargetPropertyId());
					}
				}

				return props;
			}
		}

		class ConstraintsOptionValue extends EnumValue<ConstraintsOption> {

			String getTitle() {

				return "Constraints option";
			}

			void set(PropertyAttributeConfig attribute) {

				set(attribute.getConstraintsOption());
			}

			ConstraintsOption[] getOptions() {

				return ConstraintsOption.coreAttributeOptions();
			}
		}
	}

	private class SimpleAttributeValues extends PropertyAttributeValues {

		private LinkingPropertyId linkingPropertyId;
		private ConstraintsOptionValue constraintsOption;

		private class LinkingPropertyId extends TargetLinkPropertyId {

			String getTitle() {

				return "Linking property";
			}

			void set(SimpleAttributeConfig attribute) {

				set(attribute.getLinkingPropertyId());
			}
		}

		SimpleAttributeValues() {

			linkingPropertyId = new LinkingPropertyId();
			constraintsOption = new ConstraintsOptionValue();
		}

		void setAll(SimpleAttributeConfig attribute) {

			super.setAll(attribute);

			linkingPropertyId.set(attribute);
			constraintsOption.set(attribute);
		}

		CoreAttributeConfig createAttribute(EntityId rootTargetConceptId) {

			return sourceHierarchy
					.addSimpleAttribute(
						linkingPropertyId.get(),
						rootTargetConceptId,
						constraintsOption.get());
		}

		void updateAttribute(CoreAttributeConfig config) {

			SimpleAttributeConfig saConfig = (SimpleAttributeConfig)config;

			super.updateAttribute(saConfig);

			saConfig.resetLinkingPropertyId(linkingPropertyId.get());
			saConfig.resetConstraintsOption(constraintsOption.get());
		}

		AttributeType getAttributeType() {

			return AttributeType.SIMPLE;
		}
	}

	private class AnchoredAttributeValues extends PropertyAttributeValues {

		private AnchorConceptId anchorConceptId;
		private SourcePropertyId sourcePropertyId;
		private TargetPropertyId targetPropertyId;
		private ConstraintsOptionValue constraintsOption;

		private class AnchorConceptId extends ConceptIdValue {

			String getTitle() {

				return "Anchor concept";
			}

			void set(AnchoredAttributeConfig attribute) {

				set(attribute.getAnchorConceptId());
			}
		}

		private class SourcePropertyId extends PropertyIdValue {

			String getTitle() {

				return "Source property";
			}

			void set(AnchoredAttributeConfig attribute) {

				set(attribute.getSourcePropertyId());
			}
		}

		private class TargetPropertyId extends TargetLinkPropertyId {

			String getTitle() {

				return "Target property";
			}

			void set(AnchoredAttributeConfig attribute) {

				set(attribute.getTargetPropertyId());
			}
		}

		AnchoredAttributeValues() {

			anchorConceptId = new AnchorConceptId();
			sourcePropertyId = new SourcePropertyId();
			targetPropertyId = new TargetPropertyId();
			constraintsOption = new ConstraintsOptionValue();
		}

		void setAll(AnchoredAttributeConfig attribute) {

			super.setAll(attribute);

			anchorConceptId.set(attribute);
			sourcePropertyId.set(attribute);
			targetPropertyId.set(attribute);
			constraintsOption.set(attribute);
		}

		CoreAttributeConfig createAttribute(EntityId rootTargetConceptId) {

			return sourceHierarchy
					.addAnchoredAttribute(
						anchorConceptId.get(),
						sourcePropertyId.get(),
						targetPropertyId.get(),
						rootTargetConceptId,
						constraintsOption.get());
		}

		void updateAttribute(CoreAttributeConfig config) {

			AnchoredAttributeConfig aaConfig = (AnchoredAttributeConfig)config;

			super.updateAttribute(aaConfig);

			aaConfig.resetAnchorConceptId(anchorConceptId.get());
			aaConfig.resetSourcePropertyId(sourcePropertyId.get());
			aaConfig.resetTargetPropertyId(targetPropertyId.get());
			aaConfig.resetConstraintsOption(constraintsOption.get());
		}

		AttributeType getAttributeType() {

			return AttributeType.ANCHORED;
		}
	}

	private class HierarchicalAttributeValues extends TypeValues {

		private LinksOption linksOption;

		private class LinksOption extends EnumValue<HierarchicalLinksOption> {

			String getTitle() {

				return "Hierarchical links option";
			}

			void set(HierarchicalAttributeConfig attribute) {

				set(attribute.getLinksOption());
			}

			HierarchicalLinksOption[] getOptions() {

				return HierarchicalLinksOption.values();
			}
		}

		HierarchicalAttributeValues() {

			linksOption = new LinksOption();
		}

		void setAll(HierarchicalAttributeConfig attribute) {

			super.setAll(attribute);

			linksOption.set(attribute);
		}

		void checkDoctorTargetHierarchyOptions(List<CoreHierarchyConfig> options) {

			options.remove(sourceHierarchy);
		}

		CoreAttributeConfig createAttribute(EntityId rootTargetConceptId) {

			return sourceHierarchy
					.addHierarchicalAttribute(
						rootTargetConceptId,
						linksOption.get());
		}

		void updateAttribute(CoreAttributeConfig config) {

			HierarchicalAttributeConfig haConfig = (HierarchicalAttributeConfig)config;

			super.updateAttribute(haConfig);

			haConfig.resetLinksOption(linksOption.get());
		}

		AttributeType getAttributeType() {

			return AttributeType.HIERARCHICAL;
		}
	}

	private class TypeValuesCreator extends CoreAttributeConfigVisitor {

		public void visit(SimpleAttributeConfig attribute) {

			new SimpleAttributeValues();
		}

		public void visit(AnchoredAttributeConfig attribute) {

			new AnchoredAttributeValues();
		}

		public void visit(HierarchicalAttributeConfig attribute) {

			new HierarchicalAttributeValues();
		}

		TypeValuesCreator(CoreAttributeConfig attribute) {

			visit(attribute);
		}
	}

	private class AttributeValuesInitialiser extends ValuesInitialiser<CoreAttributeConfig> {

		private class TypeValuesInitialiser extends CoreAttributeConfigVisitor {

			public void visit(SimpleAttributeConfig attribute) {

				((SimpleAttributeValues)values).setAll(attribute);
			}

			public void visit(AnchoredAttributeConfig attribute) {

				((AnchoredAttributeValues)values).setAll(attribute);
			}

			public void visit(HierarchicalAttributeConfig attribute) {

				((HierarchicalAttributeValues)values).setAll(attribute);
			}

			TypeValuesInitialiser(CoreAttributeConfig attribute) {

				visit(attribute);
			}
		}

		AttributeValuesInitialiser(CoreAttributeConfig attribute) {

			super(attribute);
		}

		void initialiseValues(CoreAttributeConfig attribute) {

			new TypeValuesInitialiser(attribute);
		}
	}

	AttributeConfigValuesPanel(
		EditManager editManager,
		CoreHierarchyConfig sourceHierarchy,
		AttributeType attributeType) {

		this(editManager, sourceHierarchy);

		switch (attributeType) {

			case SIMPLE:
				new SimpleAttributeValues();
				break;

			case ANCHORED:
				new AnchoredAttributeValues();
				break;

			case HIERARCHICAL:
				new HierarchicalAttributeValues();
				break;
		}

		populate();
	}

	AttributeConfigValuesPanel(
		EditManager editManager,
		CoreHierarchyConfig sourceHierarchy,
		CoreAttributeConfig attribute) {

		this(editManager, sourceHierarchy);

		new TypeValuesCreator(attribute);

		populate(new AttributeValuesInitialiser(attribute));
	}

	CoreAttributeConfig createAttribute() {

		return values.createAttribute();
	}

	void updateAttribute(CoreAttributeConfig config) {

		values.updateAttribute(config);
	}

	private AttributeConfigValuesPanel(
				EditManager editManager,
				CoreHierarchyConfig sourceHierarchy) {

		super(editManager);

		this.editManager = editManager;
		this.sourceHierarchy = sourceHierarchy;
	}
}
