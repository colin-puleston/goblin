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

package uk.ac.manchester.cs.goblin.gui.util;

import java.io.*;

/**
 * @author Colin Puleston
 */
public class AppInfoDisplay extends InfoDisplay {

	private String application;
	private String editSubject;

	public AppInfoDisplay(String application, String editSubject) {

		this.application = application;
		this.editSubject = editSubject;
	}

	public void informStartupError(Exception e) {

		System.err.println(createCannotStartMessage(e.getMessage()));
	}

	public Confirmation confirmOverwriteFileAndExit(File editFile) {

		return checkConfirmOrCancel(
					"Save unsaved " + editSubject + "?",
					createOverwriteFileMessage(editFile));
	}

	public boolean confirmOverwriteFile(File editFile) {

		return checkContinue(createOverwriteFileMessage(editFile));
	}

	private String createCannotStartMessage(String specificMsg) {

		return "Cannot start " + application + ": " + specificMsg;
	}

	private String createOverwriteFileMessage(File editFile) {

		return "Save " + editSubject + " to \"" + editFile + "\": Overwrite current file?";
	}
}
