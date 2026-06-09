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
import java.awt.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import uk.ac.manchester.cs.goblin.io.*;

/**
 * @author Colin Puleston
 */
class ProjectDirSelector extends JFileChooser {

	static private final long serialVersionUID = -1;

	static private final String CONFIG_FILENAME = ProjectDir.CONFIG_FILENAME;

	private File selection = null;

	private class ConfigFileFilter extends FileFilter {

		public boolean accept(File file) {

			return file.isDirectory() || file.getName().equals(CONFIG_FILENAME);
		}

		public String getDescription() {

			return "Goblin configuration files (\"" + CONFIG_FILENAME + "\")";
		}
	}

	ProjectDirSelector(Component parent) {

		setFileFilter(new ConfigFileFilter());

		if (showOpenDialog(parent) == APPROVE_OPTION) {

			selection = getSelectedFile().getParentFile();
		}
	}

	File getSelectionOrNull() {

		return selection;
	}
}
