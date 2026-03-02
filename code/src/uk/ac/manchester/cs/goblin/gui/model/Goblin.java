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

package uk.ac.manchester.cs.goblin.gui.model;

import java.io.*;
import javax.swing.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.io.model.*;
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
public class Goblin extends GoblinApp {

	static private final long serialVersionUID = -1;

	static final String TITLE = "Goblin OWL Editor";

	static private final int FRAME_WIDTH = 1200;
	static private final int FRAME_HEIGHT = 700;

	static public void main(String[] args) {

		new Goblin(getTitleSuffix(args));
	}

	static private String getTitleSuffix(String[] args) {

		return args.length == 1 ? (": " + args[0]) : "";
	}

	static private AppInfoDisplay createInfoDisplay() {

		return new AppInfoDisplay(Goblin.TITLE, "model");
	}

	private ModelSerialiser serialiser;
	private Model model;

	private int editCount = 0;
	private int undoCount = 0;

	private ModelPanel modelPanel;

	private class EditRelayer implements ModelEditListener {

		public void onEdit() {

			editCount++;

			Goblin.this.onEdit();
		}

		EditRelayer() {

			model.addEditListener(this);
		}
	}

	public Goblin() {

		this("");
	}

	public Goblin(String titleSuffix) {

		super(TITLE + titleSuffix, FRAME_WIDTH, FRAME_HEIGHT, createInfoDisplay());

		serialiser = loadOrExit();
		model = serialiser.getModel();
		modelPanel = new ModelPanel(model);

		model.setConfirmations(new UserConfirmations());
		model.setModelLoaded();

		new EditRelayer();

		display();
	}

	protected JComponent getMainApplicationComponent() {

		return modelPanel;
	}

	protected boolean unsavedEdits() {

		System.out.println("UNSAVED:EDITS: " + editCount);
		System.out.println("UNSAVED:UNDOS: " + undoCount);
		return editCount != undoCount;
	}

	protected void save() {

		serialiser.save();

		editCount = 0;
		undoCount = 0;
	}

	protected boolean enableUndoRedoActions() {

		return true;
	}

	protected boolean canUndo() {

		return model.canUndo();
	}

	protected boolean canRedo() {

		return model.canRedo();
	}

	protected void performUndoAction() {

		undoCount++;
		editCount--;

		modelPanel.makeEditVisible(model.undo());
	}

	protected void performRedoAction() {

		undoCount--;
		editCount--;

		modelPanel.makeEditVisible(model.redo());
	}

	protected File getEditFile() {

		return serialiser.getDynamicFile();
	}

	private ModelSerialiser loadOrExit() {

		try {

			return new ModelSerialiser();
		}
		catch (BadConfigFileException e) {

			exitOnStartupError(e);

			return null;
		}
		catch (BadDynamicOntologyException e) {

			exitOnStartupError(e);

			return null;
		}
	}

	private void exitOnStartupError(Exception e) {

		getInfoDisplay().informStartupError(e);

		System.exit(0);
	}
}
