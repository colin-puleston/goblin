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
import javax.swing.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class AttributesConfigPanel extends ConfigArrayPanel<CoreAttributeConfig> {

	static private final long serialVersionUID = -1;

	private EditManager editManager;
	private CoreHierarchyConfig hierarchy;

	private AttributeEditor attributeEditor;

	private abstract class TargetHierarchyListener implements ConfigUpdateListener {

		private CoreAttributeConfig attribute;

		public void onUpdate() {

			onTargetHierarchyUpdate(attribute);
		}

		TargetHierarchyListener(CoreAttributeConfig attribute) {

			this.attribute = attribute;

			addToTargetHierarchy(findTargetHierarchy());
		}

		abstract void addToTargetHierarchy(CoreHierarchyConfig targetHierarchy);

		abstract void onTargetHierarchyUpdate(CoreAttributeConfig attribute);

		private CoreHierarchyConfig findTargetHierarchy() {

			EntityId rootConceptId = attribute.getRootTargetConceptId();

			return editManager.getModelConfig().findHierarchy(rootConceptId);
		}
	}

	private class TargetHierarchyRemovalListener extends TargetHierarchyListener {

		TargetHierarchyRemovalListener(CoreAttributeConfig attribute) {

			super(attribute);
		}

		void addToTargetHierarchy(CoreHierarchyConfig targetHierarchy) {

			targetHierarchy.addDataArrayUpdateListener(this);
		}

		void onTargetHierarchyUpdate(CoreAttributeConfig attribute) {

			if (!hierarchy.hasCoreAttribute(attribute)) {

				attributeEditor.endValueEdits(attribute);
			}
		}
	}

	private class TargetHierarchyLabelUpdateListener extends TargetHierarchyListener {

		TargetHierarchyLabelUpdateListener(CoreAttributeConfig attribute) {

			super(attribute);
		}

		void addToTargetHierarchy(CoreHierarchyConfig targetHierarchy) {

			targetHierarchy.addLabelUpdateListener(this);
		}

		void onTargetHierarchyUpdate(CoreAttributeConfig attribute) {

			attributeEditor.getValueEdits(attribute).repopulate();
		}
	}

	private class AttributeEditor
					extends
						ValuesEditor
							<CoreAttributeConfig,
							AttributeConfigValuesPanel> {

		AttributeConfigValuesPanel checkCreateEmptyValues() {

			AttributeType type = getAttributeTypeSelection();

			if (type == null) {

				return null;
			}

			return new AttributeConfigValuesPanel(editManager, hierarchy, type);
		}

		AttributeConfigValuesPanel createValues(CoreAttributeConfig source) {

			return new AttributeConfigValuesPanel(editManager, hierarchy, source);
		}

		void addNewSource(AttributeConfigValuesPanel values) {

			values.createAttribute();
		}

		void updateSource(CoreAttributeConfig source, AttributeConfigValuesPanel values) {

			values.updateAttribute(source);
		}

		String getSourceTypeName() {

			return "attribute";
		}

		private AttributeType getAttributeTypeSelection() {

			return createAttributeTypeSelector().getSelectionOrNull();
		}

		private EnumValueSelector<AttributeType> createAttributeTypeSelector() {

			AttributeType[] options = AttributeType.values();

			return new EnumValueSelector<AttributeType>(options, "attribute type");
		}
	}

	protected List<CoreAttributeConfig> getSources() {

		return hierarchy.getCoreAttributes();
	}

	protected String getTitle(CoreAttributeConfig attribute) {

		return attribute.getLabel();
	}

	protected JComponent createDataComponent(CoreAttributeConfig attribute) {

		new TargetHierarchyRemovalListener(attribute);
		new TargetHierarchyLabelUpdateListener(attribute);

		return attributeEditor.setupValueEdits(attribute);
	}

	boolean checkNewSource() {

		return attributeEditor.checkNewSource();
	}

	void deleteSource(CoreAttributeConfig attribute) {

		attributeEditor.endValueEdits(attribute);

		hierarchy.removeCoreAttribute(attribute);
	}

	void reorderSources(List<CoreAttributeConfig> reorderedAttributes) {

		hierarchy.reorderCoreAttributes(reorderedAttributes);
	}

	String getSourceTypeName() {

		return "attribute";
	}

	AttributesConfigPanel(EditManager editManager, CoreHierarchyConfig hierarchy) {

		super(hierarchy, JTabbedPane.LEFT);

		this.editManager = editManager;
		this.hierarchy = hierarchy;

		attributeEditor = new AttributeEditor();

		populate();
	}
}
