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
class AttributesConfigPanel extends ConfigEditPanel<CoreAttributeConfig> {

	static private final long serialVersionUID = -1;

	private EditManager editManager;
	private CoreHierarchyConfig hierarchy;

	private AttributeEditor attributeEditor;

	private class TargetHierarchyUpdateProcessor extends TargetHierarchyListener {

		TargetHierarchyUpdateProcessor() {

			getTargetHierarchyMonitor().addListener(this);
		}

		void onHierarchyRelabelled(CoreAttributeConfig refingAttribute) {

			attributeEditor.checkReinitialiseValueEdits(refingAttribute);
		}

		void onHierarchyRemoved(CoreAttributeConfig refingAttribute) {

			if (checkDeleteAttribute(refingAttribute)) {

				repopulate();
			}
		}
	}

	private class AttributeEditor
					extends
						ValuesEditor
							<CoreAttributeConfig,
							AttributeConfigValuesPanel> {

		AttributeEditor() {

			super(editManager);
		}

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

			CoreAttributeConfig attribute = createSource(values);

			hierarchy.addCoreAttribute(attribute);
			getTargetHierarchyMonitor().onCoreAttributeAdded(attribute);
		}

		void updateSource(CoreAttributeConfig source, AttributeConfigValuesPanel values) {

			values.updateConfig(source);
		}

		String getSourceTypeName() {

			return "attribute";
		}

		private CoreAttributeConfig createSource(AttributeConfigValuesPanel values) {

			return values.createConfig(hierarchy.getRootConceptId());
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

	protected JComponent createComponent(CoreAttributeConfig attribute) {

		return attributeEditor.setupValueEdits(attribute);
	}

	boolean checkNewSource() {

		return attributeEditor.checkNewSource();
	}

	void deleteSource(CoreAttributeConfig attribute) {

		checkDeleteAttribute(attribute);
	}

	void reorderSources(List<CoreAttributeConfig> newOrderedAttributes) {

		hierarchy.reorderCoreAttributes(newOrderedAttributes);
	}

	String getSourceTypeName() {

		return "attribute";
	}

	AttributesConfigPanel(EditManager editManager, CoreHierarchyConfig hierarchy) {

		super(editManager, JTabbedPane.LEFT);

		this.editManager = editManager;
		this.hierarchy = hierarchy;

		attributeEditor = new AttributeEditor();

		populate();

		new TargetHierarchyUpdateProcessor();
	}

	private boolean checkDeleteAttribute(CoreAttributeConfig attribute) {

		if (attributeEditor.checkEndValueEdits(attribute)) {

			hierarchy.removeCoreAttribute(attribute);

			getTargetHierarchyMonitor().onCoreAttributeRemoved(attribute);

			return true;
		}

		return false;
	}

	private TargetHierarchyMonitor getTargetHierarchyMonitor() {

		return editManager.getTargetHierarchyMonitor();
	}
}
