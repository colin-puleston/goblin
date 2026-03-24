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

package uk.ac.manchester.cs.goblin.gui.config;

import java.io.*;
import javax.swing.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
public class GoblinConfig extends GoblinApp {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Goblin Configuration Tool";

	static private final int FRAME_WIDTH = 1200;
	static private final int FRAME_HEIGHT = 800;

	static public void main(String[] args) {

		new GoblinConfig();
	}

	static private AppInfoDisplay createInfoDisplay() {

		return new AppInfoDisplay(TITLE, "configuration");
	}

	private ConfigSerialiser serialiser;

	private EditManager editManager;
	private ModelConfigPanel modelConfigPanel;

	private class EditRelayManager extends EditManager {

		EditRelayManager() {

			super(serialiser.getModelConfig(), serialiser.getConfigOntology());
		}

		void onEdit() {

			GoblinConfig.this.onEdit();
		}
	}

	public GoblinConfig() {

		super(TITLE, FRAME_WIDTH, FRAME_HEIGHT, createInfoDisplay());

		serialiser = loadConfigFileOrExit();
		editManager = new EditRelayManager();

		display();
	}

	protected JComponent getMainApplicationComponent() {

		return new ModelConfigPanel(editManager);
	}

	protected boolean unsavedEdits() {

		return editManager.unsavedEdits();
	}

	protected void save() {

		serialiser.save();

		editManager.resetEdits();
	}

	protected File getEditFile() {

		return ConfigFileSerialiser.getConfigFile();
	}

	private ConfigSerialiser loadConfigFileOrExit() {

		try {

			return new ConfigSerialiser();
		}
		catch (BadConfigFileException e) {

			getInfoDisplay().informStartupError(e);

			System.exit(0);

			return null;
		}
	}
}
