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

import java.io.*;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.edit.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
public abstract class GoblinApp<L extends EditLocation> extends GFrame {

	static private final long serialVersionUID = -1;

	static private final String SAVE_BUTTON_LABEL = "Save";
	static private final String EXIT_BUTTON_LABEL = "Exit";
	static private final String UNDO_BUTTON_LABEL = "Undo";
	static private final String REDO_BUTTON_LABEL = "Redo";

	private AppInfoDisplay infoDisplay;

	private int editCount = 0;
	private int undoCount = 0;

	private List<EditsEnabledButton> editsEnabledButtons = new ArrayList<EditsEnabledButton>();

	private abstract class EditsEnabledButton extends GButton {

		static private final long serialVersionUID = -1;

		protected EditsEnabledButton(String label) {

			super(label);

			setEnabled(false);

			editsEnabledButtons.add(this);
		}

		protected abstract boolean canDoButtonThing();

		void onEdit() {

			setEnabled(canDoButtonThing());
		}
	}

	private class SaveButton extends EditsEnabledButton {

		static private final long serialVersionUID = -1;

		protected boolean canDoButtonThing() {

			return unsavedEdits();
		}

		protected void doButtonThing() {

			if (performSaveAction()) {

				setEnabled(false);
			}
		}

		SaveButton() {

			super(SAVE_BUTTON_LABEL);
		}
	}

	private class ExitButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			if (performExitAction()) {

				dispose();
			}
		}

		ExitButton() {

			super(EXIT_BUTTON_LABEL);
		}
	}

	private class UndoButton extends EditsEnabledButton {

		static private final long serialVersionUID = -1;

		protected boolean canDoButtonThing() {

			return getEditActions().canUndo();
		}

		protected void doButtonThing() {

			performUndoAction();
		}

		UndoButton() {

			super(UNDO_BUTTON_LABEL);
		}
	}

	private class RedoButton extends EditsEnabledButton {

		static private final long serialVersionUID = -1;

		protected boolean canDoButtonThing() {

			return getEditActions().canRedo();
		}

		protected void doButtonThing() {

			performRedoAction();
		}

		RedoButton() {

			super(REDO_BUTTON_LABEL);
		}
	}

	private class EditRelayer implements EditListener {

		public void onEdit() {

			editCount++;

			for (EditsEnabledButton button : editsEnabledButtons) {

				button.onEdit();
			}
		}

		EditRelayer() {

			getEditActions().addListener(this);
		}
	}

	private class WindowCloseListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			if (performExitAction()) {

				dispose();
			}
		}
	}

	protected GoblinApp(String title, int width, int height, AppInfoDisplay infoDisplay) {

		super(title, width, height);

		this.infoDisplay = infoDisplay;
	}

	protected void start() {

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowCloseListener());

		getEditActions().startTracking();

		new EditRelayer();

		display(createMainPanel());
	}

	protected abstract JComponent getMainAppComponent();

	protected JComponent getAppSpecificControlsOrNull() {

		return null;
	}

	protected abstract void save();

	protected abstract File getEditFile();

	protected abstract EditActions<L> getEditActions();

	protected abstract void makeEditVisible(L editLocation);

	protected AppInfoDisplay getInfoDisplay() {

		return infoDisplay;
	}

	private boolean performSaveAction() {

		if (unsavedEdits() && confirmOverwriteFile()) {

			save();

			editCount = 0;
			undoCount = 0;

			return true;
		}

		return false;
	}

	private boolean performExitAction() {

		if (unsavedEdits()) {

			Confirmation confirm = confirmOverwriteFileAndExit();

			if (confirm.cancel()) {

				return false;
			}

			if (confirm.yes()) {

				save();
			}
		}

		return true;
	}

	private void performUndoAction() {

		undoCount++;
		editCount--;

		makeEditVisible(getEditActions().undo());
	}

	private void performRedoAction() {

		undoCount--;
		editCount--;

		makeEditVisible(getEditActions().redo());
	}

	private boolean unsavedEdits() {

		return editCount != undoCount;
	}

	private JComponent createMainPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createButtonsPanel(), BorderLayout.NORTH);
		panel.add(getMainAppComponent(), BorderLayout.CENTER);

		JComponent extraControls = getAppSpecificControlsOrNull();

		if (extraControls != null) {

			panel.add(extraControls, BorderLayout.SOUTH);
		}

		return panel;
	}

	private JComponent createButtonsPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBorder(LineBorder.createGrayLineBorder());

		panel.add(createExternalActionButtons(), BorderLayout.EAST);
		panel.add(createEditActionButtons(), BorderLayout.WEST);

		return panel;
	}

	private JComponent createExternalActionButtons() {

		return ControlsPanel.horizontal(new SaveButton(), new ExitButton());
	}

	private JComponent createEditActionButtons() {

		return ControlsPanel.horizontal(new UndoButton(), new RedoButton());
	}

	private boolean confirmOverwriteFile() {

		return infoDisplay.confirmOverwriteFile(getEditFile());
	}

	private Confirmation confirmOverwriteFileAndExit() {

		return infoDisplay.confirmOverwriteFileAndExit(getEditFile());
	}
}
