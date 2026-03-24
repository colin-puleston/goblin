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
import javax.swing.event.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
abstract class ConfigEditPanel<S> extends MultiTabPanel<S> {

	static private final long serialVersionUID = -1;

	static private final String ADD_TAB_LABEL = "Add...";
	static private final String RELABEL_BUTTON_LABEL = "Relabel...";
	static private final String DELETE_BUTTON_LABEL = "Delete";

	static private final Color CONTROL_LABEL_COLOUR = Color.RED.darker();

	private EditManager editManager;
	private boolean additionHandlingEnabled = true;

	private class AdditionHandler implements ChangeListener {

		public void stateChanged(ChangeEvent e) {

			if (additionHandlingEnabled && getSelectedIndex() == additionTabIndex()) {

				if (checkRegisterEdit(checkNewSource())) {

					repopulate();

					setSelectedIndex(additionTabIndex() - 1);
				}
				else {

					setSelectedIndex(0);
				}
			}
		}

		AdditionHandler() {

			addChangeListener(this);
		}
	}

	private abstract class SourceActionButton extends GButton {

		static private final long serialVersionUID = -1;

		private S source;

		protected void doButtonThing() {

			if (performSourceAction(source)) {

				repopulate();
			}
		}

		SourceActionButton(String label, S source) {

			super(label);

			this.source = source;

			setForeground(CONTROL_LABEL_COLOUR);
		}

		abstract boolean performSourceAction(S source);
	}

	private class SourceRelabelButton extends SourceActionButton {

		static private final long serialVersionUID = -1;

		SourceRelabelButton(S source) {

			super(RELABEL_BUTTON_LABEL, source);
		}

		boolean performSourceAction(S source) {

			return checkRegisterEdit(checkRelabelSource(source));
		}
	}

	private class SourceDeleteButton extends SourceActionButton {

		static private final long serialVersionUID = -1;

		SourceDeleteButton(S source) {

			super(DELETE_BUTTON_LABEL, source);
		}

		boolean performSourceAction(S source) {

			return checkRegisterEdit(checkDeleteSource(source));
		}
	}

	public void populate() {

		super.populate();

		addAdditionTab();

		new AdditionHandler();
	}

	public void repopulate() {

		additionHandlingEnabled = false;

		removeAdditionTab();
		super.repopulate();
		addAdditionTab();

		additionHandlingEnabled = true;
	}

	protected JComponent checkWrapComponent(S source, JComponent comp) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(comp, BorderLayout.CENTER);
		panel.add(createControlsComponent(source), BorderLayout.SOUTH);

		return super.checkWrapComponent(source, panel);
	}

	ConfigEditPanel(EditManager editManager, int tabPlacement) {

		super(tabPlacement);

		this.editManager = editManager;
	}

	abstract boolean checkNewSource();

	abstract boolean checkRelabelSource(S source);

	abstract boolean checkDeleteSource(S source);

	private JComponent createControlsComponent(S source) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createButtonsComponent(source), BorderLayout.WEST);

		return panel;
	}

	private JComponent createButtonsComponent(S source) {

		return ControlsPanel.horizontal(
					new SourceRelabelButton(source),
					new SourceDeleteButton(source));
	}

	private void addAdditionTab() {

		addTab(null, new JPanel());

		setTabComponentAt(additionTabIndex(), createAdditionTabLabel());
	}

	private JLabel createAdditionTabLabel() {

		JLabel label = new JLabel(ADD_TAB_LABEL);

		label.setFont(GFonts.toMedium(label.getFont()));
		label.setForeground(CONTROL_LABEL_COLOUR);

		return label;
	}

	private void removeAdditionTab() {

		removeTabAt(additionTabIndex());
	}

	private int additionTabIndex() {

		return getTabCount() - 1;
	}

	private boolean checkRegisterEdit(boolean wasEdit) {

		if (wasEdit) {

			editManager.registerEdit();
		}

		return wasEdit;
	}
}
