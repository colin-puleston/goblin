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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class LabelSelector extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "Enter %s label";

	static private final String OK_BUTTON_LABEL = "Ok";
	static private final String CANCEL_BUTTON_LABEL = "Cancel";

	static private final Dimension WINDOW_SIZE = new Dimension(300, 120);

	private OkButton okButton = new OkButton();
	private String selection = null;

	private class LabelField extends GTextField {

		static private final long serialVersionUID = -1;

		protected void onCharEntered(char enteredChar) {

			String text = getText();
			boolean isText = !text.isEmpty();

			selection = isText ? text : null;

			okButton.setEnabled(isText);
		}

		protected void onTextEntered(String text) {

			dispose();
		}
	}

	private class OkButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			dispose();
		}

		OkButton() {

			super(OK_BUTTON_LABEL);

			setEnabled(false);
		}
	}

	private class CancelButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			selection = null;

			dispose();
		}

		CancelButton() {

			super(CANCEL_BUTTON_LABEL);
		}
	}

	private class WindowCloseListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			selection = null;
		}
	}

	LabelSelector(JComponent parent, String entityType) {

		super(parent, String.format(TITLE_FORMAT, entityType), true);

		setPreferredSize(WINDOW_SIZE);
		addWindowListener(new WindowCloseListener());

		display(createDisplay());
	}

	String getSelectionOrNull() {

		return selection;
	}

	private JComponent createDisplay() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new LabelField(), BorderLayout.CENTER);
		panel.add(createButtonsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	private JPanel createButtonsPanel() {

		return ControlsPanel.horizontal(okButton, new CancelButton());
	}
}


