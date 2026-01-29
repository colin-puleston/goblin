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

package uk.ac.manchester.cs.goblin.gui;

import java.awt.BorderLayout;

import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class CoreHierarchyPanel extends GSplitPane {

	static private final long serialVersionUID = -1;

	static private final String TREE_TITLE = "Core hierarchy";
	static private final String ATTRIBUTES_TITLE = "Attributes";

	private HierarchyTree hierarchyTree;
	private AttributesEditPanel attributesEditPanel;

	CoreHierarchyPanel(Hierarchy hierarchy) {

		HierarchyTreePanel treePanel = new HierarchyTreePanel(hierarchy);

		hierarchyTree = treePanel.getTree();
		attributesEditPanel = new AttributesEditPanel(hierarchyTree);

		setLeftComponent(createTreeComponent(treePanel));
		setRightComponent(createAttributesComponent());
	}

	void selectConcept(Concept concept) {

		hierarchyTree.clearSelection();
		hierarchyTree.selectConcept(concept);
	}

	void selectAttribute(Attribute attribute) {

		attributesEditPanel.makeSourceVisible(attribute);
	}

	private JComponent createTreeComponent(HierarchyTreePanel treePanel) {

		return TitledPanels.create(treePanel, TREE_TITLE);
	}

	private JComponent createAttributesComponent() {

		JPanel panel = new JPanel(new BorderLayout());

		TitledPanels.setTitle(panel, ATTRIBUTES_TITLE);

		panel.add(attributesEditPanel, BorderLayout.CENTER);

		if (hierarchyTree.getHierarchy().dynamicAttributesEnabled()) {

			panel.add(new DynamicAttributeCreatePanel(hierarchyTree), BorderLayout.SOUTH);
		}

		return panel;
	}
}
