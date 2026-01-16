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

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchyTreePanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String NO_EDIT_MESSAGE_LABEL = "External hierarchy / non-editable";

	static private final String NO_CONSTRAINTS_LABEL = "Hide ALL constraints";
	static private final String ALL_OUT_CONSTRAINTS_LABEL = "Show ALL => constraints";
	static private final String CURRENT_OUT_CONSTRAINTS_LABEL = "Show selected attribute => constraints";
	static private final String ALL_IN_CONSTRAINTS_LABEL = "Show ALL <= constraints";

	static private final String RESET_ID_LABEL = "Id...";
	static private final String ADD_LABEL = "Add...";
	static private final String REMOVE_LABEL = "Del";
	static private final String CUT_LABEL = "Mv-";
	static private final String STOP_CUT_LABEL = "Mv!";
	static private final String PASTE_LABEL = "Mv+";

	static private final int RESET_ID_TRIGGER_KEY = KeyEvent.VK_I;
	static private final int ADD_TRIGGER_KEY = KeyEvent.VK_ADD;
	static private final int REMOVE_TRIGGER_KEY = KeyEvent.VK_DELETE;
	static private final int CUT_TRIGGER_KEY = KeyEvent.VK_X;
	static private final int STOP_CUT_TRIGGER_KEY = KeyEvent.VK_ESCAPE;
	static private final int PASTE_TRIGGER_KEY = KeyEvent.VK_V;

	private Hierarchy hierarchy;
	private HierarchyTree tree;

	private ConceptMover conceptMover = new ConceptMover();

	private class DisplayModeSelector extends GSelectorBox<ConstraintsDisplayMode> {

		static private final long serialVersionUID = -1;

		protected void onSelection(ConstraintsDisplayMode mode) {

			tree.setConstraintsDisplayMode(mode);
		}

		DisplayModeSelector() {

			if (hierarchy.hasPotentialConstraintTypes()) {

				addOption(ALL_OUT_CONSTRAINTS_LABEL, ConstraintsDisplayMode.ALL_OUTWARDS);
				addOption(CURRENT_OUT_CONSTRAINTS_LABEL, ConstraintsDisplayMode.CURRENT_OUTWARDS);
			}

			if (hierarchy.hasInwardCoreConstraintTypes()) {

				addOption(ALL_IN_CONSTRAINTS_LABEL, ConstraintsDisplayMode.ALL_INWARDS);
			}

			addOption(NO_CONSTRAINTS_LABEL, ConstraintsDisplayMode.NONE);

			activate();
		}
	}

	private class TreeSelectorPanel extends ConceptTreeSelectorPanel {

		static private final long serialVersionUID = -1;

		TreeSelectorPanel() {

			super(tree);
		}

		GCellDisplay getSelectorsCellDisplay(Concept concept, boolean highlight) {

			return tree.getGoblinCellDisplay(concept).forConcept(concept, highlight);
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
		}

		EditButton(String label, int triggerKey) {

			super(label, tree);

			new TriggerKeyListener(triggerKey);

			setToolTipText(getToolTipText(triggerKey));
		}

		String getToolTipText(int triggerKey) {

			return "CTRL-" + getCtrlPrefixedToolTipText(triggerKey);
		}

		String getCtrlPrefixedToolTipText(int triggerKey) {

			return "" + (char)toUpper(triggerKey);
		}

		boolean enableOnActiveSelections(List<Concept> selections) {

			return moveInProgressStatusMatch() && canEdit(selections);
		}

		boolean moveInProgressAction() {

			return false;
		}

		abstract boolean canEdit(List<Concept> selections);

		private boolean moveInProgressStatusMatch() {

			return moveInProgressAction() == conceptMover.moveInProgress();
		}

		private int toUpper(int key) {

			return (int)Character.toUpperCase((char)key);
		}

		private int toLower(int key) {

			return (int)Character.toLowerCase((char)key);
		}
	}

	private abstract class SingleSelectEditButton extends EditButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			Concept selected = tree.getSelectedConcept();

			if (selected != null) {

				doConceptEdit(selected);
			}
		}

		SingleSelectEditButton(String label, int triggerKey) {

			super(label, triggerKey);
		}

		boolean canEdit(List<Concept> selections) {

			return selections.size() == 1 && canEdit(selections.get(0));
		}

		abstract boolean canEdit(Concept selection);

		abstract void doConceptEdit(Concept concept);
	}

	private abstract class MultiSelectEditButton extends EditButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			doConceptEdits(tree.getAllSelectedConcepts());
		}

		MultiSelectEditButton(String label, int triggerKey) {

			super(label, triggerKey);
		}

		abstract void doConceptEdits(List<Concept> concepts);
	}

	private class ResetIdButton extends SingleSelectEditButton {

		static private final long serialVersionUID = -1;

		ResetIdButton() {

			super(RESET_ID_LABEL, RESET_ID_TRIGGER_KEY);
		}

		boolean canEdit(Concept selection) {

			return selection.canResetId();
		}

		void doConceptEdit(Concept concept) {

			if (checkResetConceptId(concept)) {

				tree.update();
			}
		}
	}

	private class AddButton extends SingleSelectEditButton {

		static private final long serialVersionUID = -1;

		AddButton() {

			super(ADD_LABEL, ADD_TRIGGER_KEY);
		}

		boolean canEdit(Concept selection) {

			return true;
		}

		void doConceptEdit(Concept concept) {

			checkAddConcept(concept);
		}
	}

	private class RemoveButton extends MultiSelectEditButton {

		static private final long serialVersionUID = -1;

		RemoveButton() {

			super(REMOVE_LABEL, REMOVE_TRIGGER_KEY);
		}

		String getCtrlPrefixedToolTipText(int triggerKey) {

			return "Del";
		}

		boolean canEdit(List<Concept> selections) {

			return !containsImmovableConcept(selections);
		}

		void doConceptEdits(List<Concept> concepts) {

			checkRemoveConcepts(concepts);
		}
	}

	private class CutButton extends MultiSelectEditButton {

		static private final long serialVersionUID = -1;

		CutButton() {

			super(CUT_LABEL, CUT_TRIGGER_KEY);
		}

		boolean canEdit(List<Concept> selections) {

			return !containsImmovableConcept(selections);
		}

		void doConceptEdits(List<Concept> concepts) {

			conceptMover.startMove(concepts);

			tree.update();
		}
	}

	private class StopCutButton extends MultiSelectEditButton {

		static private final long serialVersionUID = -1;

		StopCutButton() {

			super(STOP_CUT_LABEL, STOP_CUT_TRIGGER_KEY);
		}

		String getToolTipText(int triggerKey) {

			return "Esc";
		}

		boolean enableOnNoActiveSelections() {

			return conceptMover.moveInProgress();
		}

		boolean moveInProgressAction() {

			return true;
		}

		boolean canEdit(List<Concept> selections) {

			return true;
		}

		void doConceptEdits(List<Concept> concepts) {

			conceptMover.abortMove();

			tree.update();
		}
	}

	private class PasteButton extends SingleSelectEditButton {

		static private final long serialVersionUID = -1;

		PasteButton() {

			super(PASTE_LABEL, PASTE_TRIGGER_KEY);
		}

		boolean moveInProgressAction() {

			return true;
		}

		boolean canEdit(Concept selection) {

			return conceptMover.newParentCandidate(selection);
		}

		void doConceptEdit(Concept concept) {

			conceptMover.completeMove(concept);

			tree.update();
		}
	}

	HierarchyTreePanel(Hierarchy hierarchy) {

		super(new BorderLayout());

		this.hierarchy = hierarchy;

		tree = new HierarchyTree(hierarchy, conceptMover);

		add(createUpperComponent(hierarchy), BorderLayout.NORTH);
		add(new JScrollPane(tree), BorderLayout.CENTER);
		add(createLowerComponent(hierarchy), BorderLayout.SOUTH);
	}

	HierarchyTree getTree() {

		return tree;
	}

	private JComponent createUpperComponent(Hierarchy hierarchy) {

		if (hierarchy.hasPotentialConstraintTypes() || hierarchy.hasInwardCoreConstraintTypes()) {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(new DisplayModeSelector(), BorderLayout.WEST);
			panel.add(new TreeSelectorPanel(), BorderLayout.EAST);

			return panel;
		}

		return new TreeSelectorPanel();
	}

	private JComponent createLowerComponent(Hierarchy hierarchy) {

		return hierarchy.referenceOnly()
					? createNoEditMessageLabel()
					: createEditButtonsPanel();
	}

	private JComponent createEditButtonsPanel() {

		return ControlsPanel.horizontal(
					new ResetIdButton(),
					new AddButton(),
					new RemoveButton(),
					new CutButton(),
					new PasteButton(),
					new StopCutButton());
	}

	private JLabel createNoEditMessageLabel() {

		JLabel label = new JLabel(NO_EDIT_MESSAGE_LABEL);

		GFonts.setMedium(label);

		return label;
	}

	private void checkAddConcept(Concept parent) {

		EntityId id = checkObtainConceptId(null);

		if (id != null) {

			if (hierarchy.getModel().containsConcept(id)) {

				showConceptAlreadyExistsMessage(id);
			}
			else {

				parent.addChild(id);
			}
		}
	}

	private boolean checkResetConceptId(Concept concept) {

		EntityId currentId = concept.getConceptId();
		EntityId newId = checkObtainConceptId(currentId);

		if (newId != null) {

			if (concept.resetId(newId)) {

				return true;
			}

			showConceptAlreadyExistsMessage(newId);
		}

		return false;
	}

	private void checkRemoveConcepts(List<Concept> concepts) {

		if (obtainConceptRemovalsConfirmation(concepts)) {

			new ConceptGroup(concepts).removeAll();
		}
	}

	private EntityId checkObtainConceptId(EntityId currentId) {

		return new ConceptIdSelector(this, currentId).getSelection();
	}

	private void showConceptAlreadyExistsMessage(EntityId id) {

		InfoDisplay.inform("Concept already exists: " + id.getName());
	}

	private boolean obtainConceptRemovalsConfirmation(List<Concept> concepts) {

		return InfoDisplay.checkContinue(createConceptRemovalsMessage(concepts));
	}

	private String createConceptRemovalsMessage(List<Concept> concepts) {

		StringBuilder msg = new StringBuilder();

		msg.append("Removing concept(s):");
		msg.append("\n\n");

		for (Concept concept : concepts) {

			msg.append("  " + concept.getConceptId());
			msg.append('\n');
		}

		msg.append('\n');
		msg.append("Plus any descendant-concepts and associated constraints");
		msg.append('\n');

		return msg.toString();
	}

	private boolean containsImmovableConcept(List<Concept> concepts) {

		for (Concept concept : concepts) {

			if (!concept.canMove()) {

				return true;
			}
		}

		return false;
	}
}
