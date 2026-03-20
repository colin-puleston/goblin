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

import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class EnumValueSelector<E extends Enum<?>> extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "Select %s";

	static private final int WINDOW_WIDTH = 400;
	static private final int HEADER_HEIGHT = 40;
	static private final int OPTION_HEIGHT = 60;

	static private Dimension getWindowSize(int optionCount) {

		return new Dimension(WINDOW_WIDTH, getWindowHeight(optionCount));
	}

	static private int getWindowHeight(int optionCount) {

		return HEADER_HEIGHT + (OPTION_HEIGHT * optionCount);
	}

	private E selection = null;

	private class OptionButton extends GButton {

		static private final long serialVersionUID = -1;

		private E option;

		protected void doButtonThing() {

			selection = option;

			dispose();
		}

		OptionButton(E option) {

			super(option.toString());

			this.option = option;

			setForeground(ValueTextColour.GENRAL_VALUE);
		}
	}

	EnumValueSelector(E[] options, String typeName) {

		super(String.format(TITLE_FORMAT, typeName), true);

		setPreferredSize(getWindowSize(options.length));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		display(createOptionsComponent(options));
	}

	E getSelectionOrNull() {

		return selection;
	}

	private JComponent createOptionsComponent(E[] options) {

		ControlsPanel panel = new ControlsPanel(false);

		for (E option : options) {

			panel.addControl(new OptionButton(option));
		}

		return panel;
	}
}
