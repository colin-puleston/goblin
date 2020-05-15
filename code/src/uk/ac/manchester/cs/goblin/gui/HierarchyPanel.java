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

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchyPanel extends GSplitPane {

	static private final long serialVersionUID = -1;

	static private final String TREE_PANEL_TITLE = "Selected hierarchy";
	static private final String CONSTRAINTS_PANEL_TITLE = "Constraints editor";

	static private final String CONSTRAINT_PANEL_TITLE_FORMAT = "%s constraints";

	private Hierarchy hierarchy;

	private HierarchyTree hierarchyTree;
	private ConstraintsPanel constraintsPanel;

	private class ConstraintTypeSelectionRelayer implements ChangeListener {

		public void stateChanged(ChangeEvent e) {

			relaySelection(constraintsPanel.getSelectedIndex());
		}

		ConstraintTypeSelectionRelayer() {

			constraintsPanel.addChangeListener(this);

			relaySelection(0);
		}

		private void relaySelection(int index) {

			hierarchyTree.setConstraintTypeSelection(getConstraintType(index));
		}
	}

	private class ConstraintsPanel extends ConceptTreesPanel<ConstraintType> {

		static private final long serialVersionUID = -1;

		ConstraintsPanel() {

			super(JTabbedPane.LEFT);

			setFont(GFonts.toMedium(getFont()));

			populate();
		}

		List<ConstraintType> getSources() {

			return hierarchy.getConstraintTypes();
		}

		String getTitle(ConstraintType type) {

			return type.getName();
		}

		Concept getRootConcept(ConstraintType type) {

			return type.getRootTargetConcept();
		}

		JComponent createComponent(ConstraintType type) {

			JTabbedPane tabs = new JTabbedPane();

			String validsTitle = checkPanelTitle(type, ConstraintSemantics.VALID_VALUES);
			String impliedsTitle = checkPanelTitle(type, ConstraintSemantics.IMPLIED_VALUE);

			if (validsTitle != null) {

				tabs.addTab(validsTitle, createValidValuesPanel(type));
			}

			if (impliedsTitle != null) {

				tabs.addTab(impliedsTitle, createImpliedValuesPanel(type));
			}

			return tabs;
		}

		private String checkPanelTitle(ConstraintType type, ConstraintSemantics semantics) {

			if (!type.semanticsOption(semantics)) {

				return null;
			}

			return String.format(CONSTRAINT_PANEL_TITLE_FORMAT, semantics.getDisplayLabel());
		}

		private ConstraintGroupPanel createValidValuesPanel(ConstraintType type) {

			return createGroupPanel(type, ConstraintSemantics.VALID_VALUES);
		}

		private ConstraintGroupPanel createImpliedValuesPanel(ConstraintType type) {

			return createGroupPanel(type, ConstraintSemantics.IMPLIED_VALUE);
		}

		private ConstraintGroupPanel createGroupPanel(
										ConstraintType type,
										ConstraintSemantics semantics) {

			return new ConstraintGroupPanel(type, semantics, hierarchyTree);
		}
	}

	HierarchyPanel(Hierarchy hierarchy) {

		this.hierarchy = hierarchy;

		HierarchyTreePanel treePanel = new HierarchyTreePanel(hierarchy);

		hierarchyTree = treePanel.getTree();
		constraintsPanel = new ConstraintsPanel();

		if (hierarchy.isConstrained()) {

			new ConstraintTypeSelectionRelayer();
		}

		setLeftComponent(createTreeComponent(treePanel));
		setRightComponent(createConstraintsComponent());
	}

	void makeConstraintVisible(Constraint constraint) {

		hierarchyTree.selectConcept(constraint.getSourceValue());
		constraintsPanel.makeSourceVisible(constraint.getType());
	}

	private JComponent createTreeComponent(HierarchyTreePanel treePanel) {

		return TitledPanels.create(treePanel, TREE_PANEL_TITLE);
	}

	private JComponent createConstraintsComponent() {

		return TitledPanels.create(constraintsPanel, CONSTRAINTS_PANEL_TITLE);
	}

	private ConstraintType getConstraintType(int index) {

		return hierarchy.getConstraintTypes().get(index);
	}
}
