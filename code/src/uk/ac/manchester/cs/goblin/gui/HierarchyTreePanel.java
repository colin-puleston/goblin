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
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchyTreePanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String NO_CONSTRAINTS_LABEL = "Hide constraints";
	static private final String EDIT_TYPE_CONSTRAINTS_LABEL = "Show constraints: Current edit type";
	static private final String ALL_CONSTRAINTS_LABEL = "Show constraints: All";

	static private final String ADD_LABEL = "Add...";
	static private final String REMOVE_LABEL = "Del";
	static private final String CUT_LABEL = "Mv-";
	static private final String STOP_CUT_LABEL = "Mv!";
	static private final String PASTE_LABEL = "Mv+";
	static private final String RESET_ID_LABEL = "Id...";

	static private final int ADD_TRIGGER_KEY = KeyEvent.VK_ADD;
	static private final int REMOVE_TRIGGER_KEY = KeyEvent.VK_DELETE;
	static private final int CUT_TRIGGER_KEY = KeyEvent.VK_X;
	static private final int STOP_CUT_TRIGGER_KEY = KeyEvent.VK_ESCAPE;
	static private final int PASTE_TRIGGER_KEY = KeyEvent.VK_V;
	static private final int RESET_ID_TRIGGER_KEY = KeyEvent.VK_I;

	private Model model;
	private HierarchyTree tree;

	private ConceptMover conceptMover = new ConceptMover();

	private class DisplayModeSelector extends GSelectorBox<ConstraintsDisplayMode> {

		static private final long serialVersionUID = -1;

		protected void onSelection(ConstraintsDisplayMode mode) {

			tree.setConstraintsDisplayMode(mode);
		}

		DisplayModeSelector() {

			addOption(NO_CONSTRAINTS_LABEL, ConstraintsDisplayMode.NONE);
			addOption(EDIT_TYPE_CONSTRAINTS_LABEL, ConstraintsDisplayMode.EDIT_TYPE_ONLY);
			addOption(ALL_CONSTRAINTS_LABEL, ConstraintsDisplayMode.ALL);

			activate();
		}
	}

	private class TreeSelectorPanel extends ConceptTreeSelectorPanel {

		static private final long serialVersionUID = -1;

		TreeSelectorPanel() {

			super(tree);
		}

		GCellDisplay getSelectorsCellDisplay(Concept concept, boolean highlight) {

			return GoblinCellDisplay.CONCEPTS_DEFAULT.forConcept(concept, highlight);
		}
	}

	private abstract class EditButton extends ConceptTreeSelectionDependentButton {

		static private final long serialVersionUID = -1;

		private class TriggerKeyListener extends KeyAdapter {

			private int triggerKey;
			private boolean ctrlDown = false;

			public void keyPressed(KeyEvent event) {

				if (event.getKeyCode() == KeyEvent.VK_CONTROL) {

					ctrlDown = true;
				}
			}

			public void keyReleased(KeyEvent event) {

				int key = event.getKeyCode();

				if (key == KeyEvent.VK_CONTROL) {

					ctrlDown = false;
				}
				else {

					if (isEnabled() && editTriggerable(key) && matchesTriggerKey(key)) {

						doButtonThing();
					}
				}
			}

			TriggerKeyListener(int triggerKey) {

				this.triggerKey = triggerKey;

				tree.addKeyListener(this);
			}

			private boolean editTriggerable(int key) {

				return ctrlDown || key == KeyEvent.VK_ESCAPE;
			}

			private boolean matchesTriggerKey(int key) {

				return toUpper(key) == triggerKey || toLower(key) == triggerKey;
			}

			private int toUpper(int key) {

				return (int)Character.toUpperCase((char)key);
			}

			private int toLower(int key) {

				return (int)Character.toLowerCase((char)key);
			}
		}

		protected void doButtonThing() {

			Concept selected = tree.getSelectedConcept();

			if (selected != null) {

				doConceptEdit(selected);
			}
		}

		EditButton(String label, int triggerKey) {

			super(label, tree);

			new TriggerKeyListener(triggerKey);
		}

		boolean enableOnSelectedConcept(Concept selection) {

			return enableForRootSelection(selection) && enableForMoveInProgress(selection);
		}

		boolean enableIfRootSelected() {

			return true;
		}

		boolean enableForMoveInProgress(Concept selection) {

			return !conceptMover.moveInProgress();
		}

		abstract void doConceptEdit(Concept concept);

		private boolean enableForRootSelection(Concept selection) {

			return enableIfRootSelected() || !selection.isRoot();
		}
	}

	private class AddButton extends EditButton {

		static private final long serialVersionUID = -1;

		AddButton() {

			super(ADD_LABEL, ADD_TRIGGER_KEY);
		}

		void doConceptEdit(Concept concept) {

			checkAddConcept(concept);
		}
	}

	private class RemoveButton extends EditButton {

		static private final long serialVersionUID = -1;

		RemoveButton() {

			super(REMOVE_LABEL, REMOVE_TRIGGER_KEY);
		}

		boolean enableIfRootSelected() {

			return false;
		}

		void doConceptEdit(Concept concept) {

			checkRemoveConcept(concept);
		}
	}

	private class CutButton extends EditButton {

		static private final long serialVersionUID = -1;

		CutButton() {

			super(CUT_LABEL, CUT_TRIGGER_KEY);
		}

		boolean enableIfRootSelected() {

			return false;
		}

		void doConceptEdit(Concept concept) {

			conceptMover.startMove(concept);

			tree.update();
		}
	}

	private class StopCutButton extends EditButton {

		static private final long serialVersionUID = -1;

		StopCutButton() {

			super(STOP_CUT_LABEL, STOP_CUT_TRIGGER_KEY);
		}

		boolean enableOnNoSelection() {

			return conceptMover.moveInProgress();
		}

		boolean enableForMoveInProgress(Concept selection) {

			return conceptMover.moveInProgress();
		}

		void doConceptEdit(Concept concept) {

			conceptMover.abortMove();

			tree.update();
		}
	}

	private class PasteButton extends EditButton {

		static private final long serialVersionUID = -1;

		PasteButton() {

			super(PASTE_LABEL, PASTE_TRIGGER_KEY);
		}

		boolean enableForMoveInProgress(Concept selection) {

			return conceptMover.newParentCandidate(selection);
		}

		void doConceptEdit(Concept concept) {

			conceptMover.completeMove(concept);

			tree.update();
		}
	}

	private class ResetIdButton extends EditButton {

		static private final long serialVersionUID = -1;

		ResetIdButton() {

			super(RESET_ID_LABEL, RESET_ID_TRIGGER_KEY);
		}

		boolean enableIfRootSelected() {

			return false;
		}

		void doConceptEdit(Concept concept) {

			if (checkResetConceptId(concept)) {

				tree.update();
			}
		}
	}

	HierarchyTreePanel(Hierarchy hierarchy) {

		super(new BorderLayout());

		model = hierarchy.getModel();
		tree = new HierarchyTree(hierarchy, conceptMover);

		add(createUpperPanel(hierarchy), BorderLayout.NORTH);
		add(new JScrollPane(tree), BorderLayout.CENTER);
		add(createButtonsPanel(), BorderLayout.SOUTH);
	}

	HierarchyTree getTree() {

		return tree;
	}

	private JComponent createUpperPanel(Hierarchy hierarchy) {

		if (hierarchy.isConstrained()) {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(new DisplayModeSelector(), BorderLayout.WEST);
			panel.add(new TreeSelectorPanel(), BorderLayout.EAST);

			return panel;
		}

		return new TreeSelectorPanel();
	}

	private JComponent createButtonsPanel() {

		return ControlsPanel.horizontal(
					new AddButton(),
					new RemoveButton(),
					new CutButton(),
					new PasteButton(),
					new StopCutButton(),
					new ResetIdButton());
	}

	private void checkAddConcept(Concept parent) {

		DynamicId id = checkObtainConceptId(null);

		if (id != null) {

			if (model.dynamicConceptExists(id)) {

				showConceptAlreadyExistsMessage(id);
			}
			else {

				parent.addChild(id);
			}
		}
	}

	private boolean checkResetConceptId(Concept concept) {

		DynamicId currentId = concept.getConceptId().toDynamicId();
		DynamicId newId = checkObtainConceptId(currentId);

		if (newId != null) {

			if (concept.resetId(newId)) {

				return true;
			}

			showConceptAlreadyExistsMessage(newId);
		}

		return false;
	}

	private void checkRemoveConcept(Concept concept) {

		if (obtainConceptRemovalConfirmation(concept)) {

			concept.remove();
		}
	}

	private DynamicId checkObtainConceptId(DynamicId currentId) {

		return new ConceptIdSelector(this, currentId).getSelection();
	}

	private void showConceptAlreadyExistsMessage(DynamicId id) {

		InfoDisplay.inform("Concept already exists: " + id.getName());
	}

	private boolean obtainConceptRemovalConfirmation(Concept concept) {

		String msg = "Removing concept";

		if (!concept.isLeaf()) {

			msg += " and all descendant-concepts";
		}

		return InfoDisplay.checkContinue(msg + ": " + concept.getConceptId());
	}
}
