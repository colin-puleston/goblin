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
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.*;
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
public class GoblinConfig extends GoblinApp<ConfigEditLocation> {

	static private final long serialVersionUID = -1;

	static private final String APP_TITLE = "Goblin Configuration Editor";
	static private final String EDIT_SUBJECT = "configuration";

	static private final int FRAME_WIDTH = 1200;
	static private final int FRAME_HEIGHT = 800;

	static private final String SINGLE_SECTION_MODEL = "Single-section model";
	static private final String MULTI_SECTION_MODEL = "Multi-section model";

	static public void main(String[] args) {

		new GoblinConfig(args);
	}

	private ProjectDir projectDir;
	private ConfigSerialiser serialiser;

	private EditManager editManager;
	private ModelConfigPanel modelConfigPanel;

	private class ModelModeSelector extends GStringSelectorBox {

		static private final long serialVersionUID = -1;

		private boolean resettingSelection = false;

		protected void onSelection(String selection) {

			if (!resettingSelection) {

				boolean toMultiSections = selection == MULTI_SECTION_MODEL;

				if (toMultiSections == singleSectionMode()) {

					if (!modelConfigPanel.setMultiSections(toMultiSections)) {

						resettingSelection = true;
						setSelectedItem(getMode(toMultiSections));
						resettingSelection = false;
					}
				}
			}
		}

		ModelModeSelector() {

			setFont(GFonts.toMedium(getFont()));

			addOption(SINGLE_SECTION_MODEL);
			addOption(MULTI_SECTION_MODEL);

			setSelectedItem(getMode(singleSectionMode()));

			activate();
		}

		private String getMode(boolean singleSection) {

			return singleSection ? SINGLE_SECTION_MODEL : MULTI_SECTION_MODEL;
		}

		private boolean singleSectionMode() {

			return editManager.getModelConfig().singleSectionMode();
		}
	}

	protected JComponent getMainAppComponent() {

		return modelConfigPanel;
	}

	protected JComponent getAppSpecificControlsOrNull() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBorder(LineBorder.createGrayLineBorder());
		panel.add(new ModelModeSelector(), BorderLayout.WEST);

		return panel;
	}

	protected void save() {

		serialiser.save();
	}

	protected File getEditFile() {

		return projectDir.getConfigFile();
	}

	protected ConfigEditActions getEditActions() {

		return editManager.getEditActions();
	}

	protected void makeEditVisible(ConfigEditLocation editLocation) {

		modelConfigPanel.checkMakeEditVisible(editLocation);
	}

	private GoblinConfig(String[] args) {

		super(APP_TITLE, EDIT_SUBJECT, FRAME_WIDTH, FRAME_HEIGHT);

		projectDir = getProjectDir(args);

		serialiser = loadConfigOrExit();
		editManager = createEditManager();
		modelConfigPanel = new ModelConfigPanel(editManager);

		start(serialiser.getProjectName());
	}

	private ConfigSerialiser loadConfigOrExit() {

		try {

			return new ConfigSerialiser(projectDir);
		}
		catch (BadStartupException e) {

			exitOnStartupError(e);

			return null;
		}
	}

	private EditManager createEditManager() {

		return new EditManager(serialiser.getModelConfig(), serialiser.getConfigOntology());
	}
}
