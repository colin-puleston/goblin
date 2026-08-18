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
import uk.ac.manchester.cs.goblin.edit.*;
import uk.ac.manchester.cs.goblin.io.*;
import uk.ac.manchester.cs.goblin.io.model.*;
import uk.ac.manchester.cs.goblin.gui.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
public class Goblin extends GoblinApp<ModelEditLocation> {

	static private final long serialVersionUID = -1;

	static private final String APP_TITLE = "Goblin OWL Editor";
	static private final String EDIT_SUBJECT = "model";

	static private final int FRAME_WIDTH = 1200;
	static private final int FRAME_HEIGHT = 700;

	static public void main(String[] args) {

		new Goblin(args);
	}

	private ModelSerialiser serialiser;
	private Model model;

	private ModelPanel modelPanel;

	protected JComponent getMainAppComponent() {

		return modelPanel;
	}

	protected void save() {

		serialiser.save();
	}

	protected File getEditFile() {

		return serialiser.getDynamicOntologyFile();
	}

	protected ModelEditActions getEditActions() {

		return model.getEditActions();
	}

	protected void makeEditVisible(ModelEditLocation editLocation) {

		modelPanel.makeEditVisible(editLocation);
	}

	private Goblin(String[] args) {

		super(APP_TITLE, EDIT_SUBJECT, FRAME_WIDTH, FRAME_HEIGHT);

		serialiser = loadModelOrExit(getProjectDir(args));
		model = serialiser.getModel();
		modelPanel = new ModelPanel(model);

		model.setConfirmations(new UserConfirmations());

		start(serialiser.getProjectName());
	}

	private ModelSerialiser loadModelOrExit(ProjectDir projectDir) {

		try {

			return new ModelSerialiser(projectDir);
		}
		catch (BadStartupException e) {

			exitOnStartupError(e);

			return null;
		}
	}
}
