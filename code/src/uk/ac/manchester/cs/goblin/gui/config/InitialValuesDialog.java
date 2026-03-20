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
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class InitialValuesDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "Create %s configuration";

	static private final String OK_LABEL = "Ok";
	static private final String CANCEL_LABEL = "Cancel";

	static private final Dimension WINDOW_SIZE = new Dimension(500, 600);

	private ValuesPanel values;
	private boolean okSelected = false;

	private class OkButton extends GButton {

		static private final long serialVersionUID = -1;

		private class Enabler extends ValuesPanelListener {

			void onValueEdit() {

				setEnabled(values.allValuesSet());
			}
		}

		protected void doButtonThing() {

			okSelected |= true;

			dispose();
		}

		OkButton() {

			super(OK_LABEL);

			setEnabled(false);

			values.addListener(new Enabler());
		}
	}

	private class CancelButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			dispose();
		}

		CancelButton() {

			super(CANCEL_LABEL);
		}
	}

	InitialValuesDialog(ValuesPanel values, String editedEntity) {

		super(String.format(TITLE_FORMAT, editedEntity), true);

		this.values = values;

		setPreferredSize(WINDOW_SIZE);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		display(createMainComponent());
	}

	boolean okSelected() {

		return okSelected;
	}

	private JComponent createMainComponent() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new JScrollPane(values), BorderLayout.CENTER);
		panel.add(createButtonsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createButtonsPanel() {

		return ControlsPanel.horizontal(new OkButton(), new CancelButton());
	}
}
