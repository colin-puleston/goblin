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

import java.awt.*;
import javax.swing.*;

/**
 * @author Colin Puleston
 */
class ControlsPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final int STRUT_SIZE = 10;

	static ControlsPanel horizontal(JComponent... control) {

		return new ControlsPanel(true, control);
	}

	static ControlsPanel vertical(JComponent... control) {

		return new ControlsPanel(false, control);
	}

	private boolean horizontal;

	ControlsPanel(boolean horizontal) {

		this.horizontal = horizontal;

		setLayout(new BoxLayout(this, getAxis()));
	}

	ControlsPanel(boolean horizontal, JComponent... controls) {

		this(horizontal);

		for (JComponent control : controls) {

			addControl(control);
		}
	}

	void addControl(JComponent control) {

		if (getComponentCount() != 0) {

			add(createStrut());
		}

		add(createControlPanel(control));
	}

	private int getAxis() {

		return horizontal ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS;
	}

	private Component createStrut() {

		return horizontal
				? Box.createHorizontalStrut(STRUT_SIZE)
				: Box.createVerticalStrut(STRUT_SIZE);
	}

	private JComponent createControlPanel(JComponent control) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(control, BorderLayout.CENTER);

		return panel;
	}
}
