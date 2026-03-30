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

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class ModelSectionConfigPanel extends ConfigEditPanel<CoreHierarchyConfig> {

	static private final long serialVersionUID = -1;

	static private final String ATTRIBUTES_TITLE = "Hierarchy attributes";

	private EditManager editManager;
	private ModelSectionConfig section;

	private HierarchyEditor hierarchyEditor;

	private class HierarchyEditor
					extends
						ValuesEditor
							<CoreHierarchyConfig,
							HierarchyConfigValuesPanel> {

		HierarchyEditor() {

			super(editManager);
		}

		HierarchyConfigValuesPanel checkCreateEmptyValues() {

			return new HierarchyConfigValuesPanel(editManager);
		}

		HierarchyConfigValuesPanel createValues(CoreHierarchyConfig source) {

			return new HierarchyConfigValuesPanel(editManager, source);
		}

		void addNewSource(HierarchyConfigValuesPanel values) {

			section.addHierarchy(values.createConfig());
		}

		void updateSource(CoreHierarchyConfig source, HierarchyConfigValuesPanel values) {

			values.updateConfig(source);
		}

		String getSourceTypeName() {

			return "hierarchy";
		}
	}

	protected List<CoreHierarchyConfig> getSources() {

		return section.getHierarchies();
	}

	protected String getTitle(CoreHierarchyConfig hierarchy) {

		return hierarchy.getLabel();
	}

	protected JComponent createComponent(CoreHierarchyConfig hierarchy) {

		GSplitPane panel = new GSplitPane();

		panel.setLeftComponent(createHierarchyComponent(hierarchy));
		panel.setRightComponent(createAttributesComponent(hierarchy));

		return panel;
	}

	boolean checkNewSource() {

		return hierarchyEditor.checkNewSource();
	}

	void deleteSource(CoreHierarchyConfig hierarchy) {

		section.removeHierarchy(hierarchy);
		getTargetHierarchyMonitor().onCoreHierarchyRemoved(hierarchy);
	}

	String getSourceTypeName() {

		return "hierarchy";
	}

	void onSourceRelabelled(CoreHierarchyConfig hierarchy) {

		getTargetHierarchyMonitor().onCoreHierarchyRelabelled(hierarchy);
	}

	ModelSectionConfigPanel(EditManager editManager, ModelSectionConfig section) {

		super(editManager, JTabbedPane.LEFT);

		this.editManager = editManager;
		this.section = section;

		hierarchyEditor = new HierarchyEditor();

		populate();
	}

	String getTitle() {

		return section.getLabel();
	}

	private JComponent createHierarchyComponent(CoreHierarchyConfig hierarchy) {

		return hierarchyEditor.setupValueEdits(hierarchy);
	}

	private JComponent createAttributesComponent(CoreHierarchyConfig hierarchy) {

		return TitledPanels.create(
					new AttributesConfigPanel(editManager, hierarchy),
					ATTRIBUTES_TITLE);
	}

	private TargetHierarchyMonitor getTargetHierarchyMonitor() {

		return editManager.getTargetHierarchyMonitor();
	}
}
