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
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class Goblin extends GFrame {

	static private final long serialVersionUID = -1;

	static final String SIMPLE_TITLE = "GOBLIN OWL-Editor";

	static private final String COMPOUND_TITLE_FORMAT = SIMPLE_TITLE + ": %s";

	static private final String LOAD_BUTTON_LABEL = "Load...";
	static private final String SAVE_BUTTON_LABEL = "Save";
	static private final String SAVE_AS_BUTTON_LABEL = "Save As...";
	static private final String EXIT_BUTTON_LABEL = "Exit";

	static private final String UNDO_BUTTON_LABEL = "Undo";
	static private final String REDO_BUTTON_LABEL = "Redo";

	static private final int FRAME_WIDTH = 1300;
	static private final int FRAME_HEIGHT = 700;

	static public void main(String[] args) {

		new Goblin(getTitle(args));
	}

	static private String getTitle(String[] args) {

		return args.length == 1 ? createCompoundTitle(args[0]) : SIMPLE_TITLE;
	}

	static private String createCompoundTitle(String subTitle) {

		return String.format(COMPOUND_TITLE_FORMAT, subTitle);
	}

	private ModelHandler modelHandler;
	private ModelEditPanel modelEditPanel;

	private class ModelEditPanel extends ConceptTreesPanel<Hierarchy> {

		static private final long serialVersionUID = -1;

		ModelEditPanel() {

			super(JTabbedPane.LEFT);

			setFont(GFonts.toMedium(getFont()));

			populate();
		}

		List<Hierarchy> getSources() {

			return getCurrentModel().getHierarchies();
		}

		String getTitle(Hierarchy hierarchy) {

			return hierarchy.getRootConcept().getConceptId().getLabel();
		}

		Concept getRootConcept(Hierarchy hierarchy) {

			return hierarchy.getRootConcept();
		}

		JComponent createComponent(Hierarchy hierarchy) {

			return new HierarchyPanel(hierarchy);
		}

		void makeEditVisible(EditLocation location) {

			int hierarchyIdx = makeHierarchyVisible(location);

			if (location.constraintEdit()) {

				Constraint constraint = location.getEditedConstraint();

				getHierarchyPanel(hierarchyIdx).makeConstraintVisible(constraint);
			}
		}

		private int makeHierarchyVisible(EditLocation location) {

			return makeSourceVisible(location.getPrimaryEditHierarchy().getRootConcept());
		}

		private HierarchyPanel getHierarchyPanel(int hierarchyIdx) {

			return (HierarchyPanel)getComponentAt(hierarchyIdx);
		}
	}

	private abstract class EditsEnabledButton extends GButton {

		static private final long serialVersionUID = -1;

		private class Enabler implements ModelEditListener {

			public void onEdit() {

				setEnabled(canDoButtonThing());
			}

			Enabler() {

				modelHandler.addEditListener(this);
			}
		}

		EditsEnabledButton(String label) {

			super(label);

			setEnabled(false);

			new Enabler();
		}

		abstract boolean canDoButtonThing();
	}

	private class LoadButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			modelHandler.loadNew();

			displayNewModel();
		}

		LoadButton() {

			super(LOAD_BUTTON_LABEL);
		}
	}

	private class SaveButton extends EditsEnabledButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			if (modelHandler.save()) {

				setEnabled(false);
			}
		}

		SaveButton() {

			super(SAVE_BUTTON_LABEL);
		}

		boolean canDoButtonThing() {

			return modelHandler.unsavedEdits();
		}
	}

	private class SaveAsButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			modelHandler.saveAs();
		}

		SaveAsButton() {

			super(SAVE_AS_BUTTON_LABEL);
		}
	}

	private class ExitButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			if (modelHandler.checkExit()) {

				dispose();
			}
		}

		ExitButton() {

			super(EXIT_BUTTON_LABEL);
		}
	}

	private class UndoButton extends EditsEnabledButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			modelEditPanel.makeEditVisible(modelHandler.undo());
		}

		UndoButton() {

			super(UNDO_BUTTON_LABEL);
		}

		boolean canDoButtonThing() {

			return getCurrentModel().canUndo();
		}
	}

	private class RedoButton extends EditsEnabledButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			modelEditPanel.makeEditVisible(modelHandler.redo());
		}

		RedoButton() {

			super(REDO_BUTTON_LABEL);
		}

		boolean canDoButtonThing() {

			return getCurrentModel().canRedo();
		}
	}

	private class WindowCloseListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			if (modelHandler.checkExit()) {

				dispose();
			}
		}
	}

	public Goblin(String title) {

		super(title, FRAME_WIDTH, FRAME_HEIGHT);

		modelHandler = new ModelHandler(this);
		modelEditPanel = new ModelEditPanel();

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowCloseListener());

		display(createMainPanel());
	}

	private void displayNewModel() {

		modelEditPanel.repopulate();
	}

	private JComponent createMainPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(modelEditPanel, BorderLayout.CENTER);
		panel.add(createExternalActionsPanel(), BorderLayout.NORTH);

		return panel;
	}

	private JComponent createExternalActionsPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBorder(LineBorder.createGrayLineBorder());
		panel.add(createReEditActionButtons(), BorderLayout.WEST);
		panel.add(createExternalActionButtons(), BorderLayout.EAST);

		return panel;
	}

	private JComponent createReEditActionButtons() {

		return ControlsPanel.horizontal(new UndoButton(), new RedoButton());
	}

	private JComponent createExternalActionButtons() {

		return ControlsPanel.horizontal(
					new LoadButton(),
					new SaveButton(),
					new SaveAsButton(),
					new ExitButton());
	}

	private Model getCurrentModel() {

		return modelHandler.getModel();
	}
}
