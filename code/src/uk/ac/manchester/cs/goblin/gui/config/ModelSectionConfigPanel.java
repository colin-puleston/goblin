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
	private SectionHierarchyGrabManager hierarchyGrabManager;

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

			values.createHierarchy(section);
		}

		void updateSource(CoreHierarchyConfig source, HierarchyConfigValuesPanel values) {

			values.updateHierarchy(source);
		}

		String getSourceTypeName() {

			return "hierarchy";
		}
	}

	private class HierarchyGrabVictimRepopulator implements HierarchyGrabListener {

		public void onHierarchyGrabbed() {

			repopulate();
		}

		HierarchyGrabVictimRepopulator() {

			section.addHierarchyGrabListener(this);
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

	boolean enableGrab() {

		return hierarchyGrabManager.enableGrab();
	}

	boolean checkGrabSource() {

		return hierarchyGrabManager.performGrab();
	}

	void deleteSource(CoreHierarchyConfig hierarchy) {

		section.removeHierarchy(hierarchy);
	}

	void reorderSources(List<CoreHierarchyConfig> newOrderedHierarchies) {

		section.reorderHierarchies(newOrderedHierarchies);
	}

	String getSourceTypeName() {

		return "hierarchy";
	}

	ModelSectionConfigPanel(EditManager editManager, ModelSectionConfig section) {

		super(editManager, JTabbedPane.LEFT);

		this.editManager = editManager;
		this.section = section;

		hierarchyEditor = new HierarchyEditor();
		hierarchyGrabManager = createHierarchyGrabManager();

		populate();

		new HierarchyGrabVictimRepopulator();
	}

	String getTitle() {

		return section.getLabel();
	}

	private JComponent createHierarchyComponent(CoreHierarchyConfig hierarchy) {

		return hierarchyEditor.setupValueEdits(hierarchy);
	}

	private JComponent createAttributesComponent(CoreHierarchyConfig hierarchy) {

		return createAttributesPanel(hierarchy).createFullEditComponent(ATTRIBUTES_TITLE);
	}

	private AttributesConfigPanel createAttributesPanel(CoreHierarchyConfig hierarchy) {

		return new AttributesConfigPanel(editManager, hierarchy);
	}

	private SectionHierarchyGrabManager createHierarchyGrabManager() {

		return new SectionHierarchyGrabManager(editManager.getModelConfig(), section);
	}
}
