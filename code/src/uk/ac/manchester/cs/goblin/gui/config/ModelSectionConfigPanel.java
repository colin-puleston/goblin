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
class ModelSectionConfigPanel extends MultiTabPanelWithEditControls<CoreHierarchyConfig> {

	static private final long serialVersionUID = -1;

	static private final String ATTRIBUTES_TITLE = "Hierarchy attributes";

	private ValueOptions valueOptions;
	private ModelSectionConfig section;

	private HierarchyEditor hierarchyEditor = new HierarchyEditor();

	private class HierarchyEditor
					extends
						ValuesEditor
							<CoreHierarchyConfig,
							HierarchyConfigValuesPanel> {

		HierarchyConfigValuesPanel checkCreateEmptyValues() {

			return new HierarchyConfigValuesPanel(valueOptions);
		}

		HierarchyConfigValuesPanel createValues(CoreHierarchyConfig currentSource) {

			return new HierarchyConfigValuesPanel(valueOptions, currentSource);
		}

		CoreHierarchyConfig createSource(HierarchyConfigValuesPanel values) {

			return values.createConfig();
		}

		void addNewSource(CoreHierarchyConfig newSource) {

			section.addHierarchy(newSource);
		}

		void replaceSource(CoreHierarchyConfig oldSource, CoreHierarchyConfig newSource) {

			section.replaceHierarchy(oldSource, newSource);
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

	protected boolean checkNewSource() {

		return hierarchyEditor.checkNewSource();
	}

	protected boolean checkRelabelSource(CoreHierarchyConfig hierarchy) {

		return false;
	}

	protected boolean checkDeleteSource(CoreHierarchyConfig hierarchy) {

		return false;
	}

	ModelSectionConfigPanel(ValueOptions valueOptions, ModelSectionConfig section) {

		super(JTabbedPane.LEFT);

		this.valueOptions = valueOptions;
		this.section = section;

		populate();
	}

	String getTitle() {

		return section.getLabel();
	}

	private JComponent createHierarchyComponent(CoreHierarchyConfig hierarchy) {

		return hierarchyEditor.checkSourceEdits(hierarchy);
	}

	private JComponent createAttributesComponent(CoreHierarchyConfig hierarchy) {

		return TitledPanels.create(
					new AttributesGonfigPanel(valueOptions, hierarchy),
					ATTRIBUTES_TITLE);
	}
}
