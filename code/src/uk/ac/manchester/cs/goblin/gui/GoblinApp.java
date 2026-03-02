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

import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
public abstract class GoblinApp extends GFrame {

	static private final long serialVersionUID = -1;

	static private final String SAVE_BUTTON_LABEL = "Save";
	static private final String EXIT_BUTTON_LABEL = "Exit";

	static private final String UNDO_BUTTON_LABEL = "Undo";
	static private final String REDO_BUTTON_LABEL = "Redo";

	private AppInfoDisplay infoDisplay;

	private List<EditsEnabledButton> editsEnabledButtons = new ArrayList<EditsEnabledButton>();

	private abstract class EditsEnabledButton extends GButton {

		static private final long serialVersionUID = -1;

		EditsEnabledButton(String label) {

			super(label);

			setEnabled(false);

			editsEnabledButtons.add(this);
		}

		void onEdit() {

			setEnabled(canDoButtonThing());
		}

		abstract boolean canDoButtonThing();
	}

	private class SaveButton extends EditsEnabledButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			if (performSaveAction()) {

				setEnabled(false);
			}
		}

		SaveButton() {

			super(SAVE_BUTTON_LABEL);
		}

		boolean canDoButtonThing() {

			return unsavedEdits();
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

		protected void doButtonThing() {

			performUndoAction();
		}

		UndoButton() {

			super(UNDO_BUTTON_LABEL);
		}

		boolean canDoButtonThing() {

			return canUndo();
		}
	}

	private class RedoButton extends EditsEnabledButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			performRedoAction();
		}

		RedoButton() {

			super(REDO_BUTTON_LABEL);
		}

		boolean canDoButtonThing() {

			return canRedo();
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

	protected void display() {

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowCloseListener());

		display(createMainPanel());
	}

	protected abstract JComponent getMainApplicationComponent();

	protected abstract boolean unsavedEdits();

	protected abstract void save();

	protected boolean enableUndoRedoActions() {

		return false;
	}

	protected boolean canUndo() {

		throw new UnsupportedOperationException();
	}

	protected boolean canRedo() {

		throw new UnsupportedOperationException();
	}

	protected void performUndoAction() {

		throw new UnsupportedOperationException();
	}

	protected void performRedoAction() {

		throw new UnsupportedOperationException();
	}

	protected abstract File getEditFile();

	protected void onEdit() {

		for (EditsEnabledButton button : editsEnabledButtons) {

			button.onEdit();
		}
	}

	protected AppInfoDisplay getInfoDisplay() {

		return infoDisplay;
	}

	private boolean performSaveAction() {

		if (unsavedEdits() && confirmOverwriteFile()) {

			save();

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

	private JComponent createMainPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(getMainApplicationComponent(), BorderLayout.CENTER);
		panel.add(createExternalActionsPanel(), BorderLayout.NORTH);

		return panel;
	}

	private JComponent createExternalActionsPanel() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBorder(LineBorder.createGrayLineBorder());

		if (enableUndoRedoActions()) {

			panel.add(createUndoRedoActionButtons(), BorderLayout.WEST);
		}

		panel.add(createExternalActionButtons(), BorderLayout.EAST);

		return panel;
	}

	private JComponent createUndoRedoActionButtons() {

		return ControlsPanel.horizontal(new UndoButton(), new RedoButton());
	}

	private JComponent createExternalActionButtons() {

		return ControlsPanel.horizontal(new SaveButton(), new ExitButton());
	}

	private boolean confirmOverwriteFile() {

		return infoDisplay.confirmOverwriteFile(getEditFile());
	}

	private Confirmation confirmOverwriteFileAndExit() {

		return infoDisplay.confirmOverwriteFileAndExit(getEditFile());
	}
}
