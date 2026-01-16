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
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class AttributesEditPanel extends ConceptTreesPanel<ConstraintType> {

	static private final long serialVersionUID = -1;

	static private final String CONSTRAINTS_TITLE = "Constraints (selected concept)";

	private HierarchyTree hierarchyTree;

	private boolean updatingConceptSelection = false;

	private class HierarchyTreeUpdater implements ChangeListener {

		public void stateChanged(ChangeEvent e) {

			if (!updatingConceptSelection) {

				updateTree(getSelectedIndex());
			}
		}

		HierarchyTreeUpdater() {

			addChangeListener(this);

			updateTree(0);
		}

		private void updateTree(int index) {

			List<ConstraintType> types = getHierarchy().getAllConstraintTypes();

			if (types.size() > index) {

				hierarchyTree.setConstraintTypeSelection(types.get(index));
			}
		}
	}

	private class ConceptSelectionDrivenRepopulator extends GSelectionListener<GNode> {

		protected void onSelected(GNode node) {

			updatingConceptSelection = true;
			repopulate();
			updatingConceptSelection = false;
		}

		protected void onDeselected(GNode node) {
		}

		ConceptSelectionDrivenRepopulator() {

			hierarchyTree.addNodeSelectionListener(this);
		}
	}

	private class DynamicAttributeOpsDrivenRepopulator implements HierarchyListener {

		private DynamicConstraintType type;

		public void onAddedDynamicConstraintType(DynamicConstraintType type) {

			repopulate();

			setSelectedIndex(getTabCount() - 1);
		}

		public void onRemovedDynamicConstraintType(DynamicConstraintType type) {

			repopulate();
		}

		DynamicAttributeOpsDrivenRepopulator() {

			getHierarchy().addListener(this);
		}
	}

	private class DynamicAttributeLabelUpdater implements DynamicConstraintTypeListener {

		private DynamicConstraintType type;

		public void onAttributeIdReset() {

			resetTabLabel(type);
		}

		DynamicAttributeLabelUpdater(DynamicConstraintType type) {

			this.type = type;

			type.removeTypeListeners(getClass());
			type.addListener(this);
		}
	}

	AttributesEditPanel(HierarchyTree hierarchyTree) {

		super(JTabbedPane.LEFT);

		this.hierarchyTree = hierarchyTree;

		if (getHierarchy().hasPotentialConstraintTypes()) {

			new HierarchyTreeUpdater();

			new ConceptSelectionDrivenRepopulator();
			new DynamicAttributeOpsDrivenRepopulator();
		}

		setFont(GFonts.toMedium(getFont()));

		populate();
	}

	List<ConstraintType> getSources() {

		Concept concept = hierarchyTree.getSelectedConcept();

		return concept != null
				? concept.getApplicableConstraintTypes()
				: getHierarchy().getCoreConstraintTypes();
	}

	String getTitle(ConstraintType type) {

		return type.getName();
	}

	Concept getRootConcept(ConstraintType type) {

		return type.getRootTargetConcept();
	}

	JComponent createComponent(ConstraintType type) {

		JComponent constComp = createConstraintsComponent(type);

		if (type.dynamicConstraintType()) {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(constComp, BorderLayout.CENTER);
			panel.add(new DynamicAttributeEditPanel(type), BorderLayout.SOUTH);

			new DynamicAttributeLabelUpdater((DynamicConstraintType)type);

			return panel;
		}

		return constComp;
	}

	boolean requiresItalicizedLabel(ConstraintType type) {

		return type.dynamicConstraintType();
	}

	private JComponent createConstraintsComponent(ConstraintType type) {

		return TitledPanels.create(createConstraintsPanel(type), CONSTRAINTS_TITLE);
	}

	private JComponent createConstraintsPanel(ConstraintType type) {

		return new ConstraintGroupPanel(type, hierarchyTree);
	}

	private Hierarchy getHierarchy() {

		return hierarchyTree.getHierarchy();
	}
}
