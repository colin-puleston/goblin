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

import java.util.*;
import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
abstract class ConfigEditPanel<S extends LabelledConfigObject> extends MultiTabPanel<S> {

	static private final long serialVersionUID = -1;

	static private final String ADD_TAB_LABEL = "Add...";
	static private final String GRAB_TAB_LABEL = "Grab...";
	static private final String REORDER_TAB_LABEL = "Order...";
	static private final String RELABEL_BUTTON_LABEL = "Label...";
	static private final String DELETE_BUTTON_LABEL = "Delete";

	static private final Color CONTROL_LABEL_COLOUR = Color.GREEN.darker().darker();

	private EditManager editManager;

	private List<ControlTabHandler> controlTabHandlers = new ArrayList<ControlTabHandler>();
	private Map<S, SourceDeleteButton> sourceDeleteButtons = new HashMap<S, SourceDeleteButton>();

	private abstract class ControlTabHandler {

		private TabListener tabListener;
		private int tabIndex = getTabCount();

		private class TabListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {

				if (activeTabHandler() && getSelectedIndex() == tabIndex) {

					setSelectedIndex(checkPerformControlAction());
				}
			}

			TabListener() {

				addChangeListener(this);
			}
		}

		ControlTabHandler(String label) {

			addControlTab(label);
			controlTabHandlers.add(this);

			tabListener = new TabListener();
		}

		void remove() {

			removeChangeListener(tabListener);

			controlTabHandlers.remove(this);
		}

		abstract int postActionTabSelectionIndex();

		abstract boolean performControlAction();

		private void addControlTab(String label) {

			int currentSel = getSelectedIndex();

			addTab(null, new JPanel());
			setTabComponentAt(tabIndex, createControlTabLabel(label));

			setSelectedIndex(currentSel);
		}

		private int checkPerformControlAction() {

			if (checkRegisterEdit(performControlAction())) {

				repopulate();

				return postActionTabSelectionIndex();
			}

			return -1;
		}

		private boolean activeTabHandler() {

			return controlTabHandlers.contains(this);
		}
	}

	private class AdditionHandler extends ControlTabHandler {

		AdditionHandler() {

			super(ADD_TAB_LABEL);
		}

		int postActionTabSelectionIndex() {

			return lastSourceIndex();
		}

		boolean performControlAction() {

			return checkNewSource();
		}
	}

	private class GrabHandler extends ControlTabHandler {

		GrabHandler() {

			super(GRAB_TAB_LABEL);
		}

		int postActionTabSelectionIndex() {

			return lastSourceIndex();
		}

		boolean performControlAction() {

			return checkGrabSource();
		}
	}

	private class ReorderHandler extends ControlTabHandler {

		ReorderHandler() {

			super(REORDER_TAB_LABEL);
		}

		int postActionTabSelectionIndex() {

			return -1;
		}

		boolean performControlAction() {

			EntityReorderDialog<S> dialog = createReorderDialog();

			if (dialog.reordered()) {

				reorderSources(dialog.getCurrentOrder());

				editManager.registerEdit();

				return true;
			}

			return false;
		}

		private EntityReorderDialog<S> createReorderDialog() {

			return new EntityReorderDialog<S>(getSources(), getSourceTypeName());
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

		private boolean checkRelabelSource(S source) {

			String label = checkInputSourceLabel();

			if (label != null) {

				source.resetLabel(label);
				setTabComponentAt(getSelectedIndex(), createTabLabel(label));

				return true;
			}

			return false;
		}
	}

	private class SourceDeleteButton extends SourceActionButton {

		static private final long serialVersionUID = -1;

		SourceDeleteButton(S source) {

			super(DELETE_BUTTON_LABEL, source);

			updateEnabling();

			sourceDeleteButtons.put(source, this);
		}

		void updateEnabling() {

			setEnabled(enableDelete());
		}

		boolean performSourceAction(S source) {

			if (checkRegisterEdit(checkConfirmDeletion(source))) {

				deleteSource(source);
				sourceDeleteButtons.remove(source);

				return true;
			}

			return false;
		}

		private boolean checkConfirmDeletion(S source) {

			return InfoDisplay.checkContinue("Deleting " + describeSource(source));
		}

		private String describeSource(S source) {

			return getSourceTypeName() + " \"" + source.getLabel() + "\"";
		}
	}

	public void populate() {

		super.populate();
		addControlTabs();
	}

	public void repopulate() {

		removeControlTabs();
		super.repopulate();

		updateSourceDeleteButtons();
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

	JComponent createFullEditComponent(String title) {

		return TitledPanels.create(this, title);
	}

	abstract boolean checkNewSource();

	boolean enableGrab() {

		return false;
	}

	boolean checkGrabSource() {

		throw new Error("Method not enabled!");
	}

	boolean enableDelete() {

		return true;
	}

	abstract void deleteSource(S source);

	abstract void reorderSources(List<S> newOrderedSources);

	abstract String getSourceTypeName();

	String checkInputSourceLabel() {

		return new LabelSelector(this, getSourceTypeName()).getSelectionOrNull();
	}

	private JComponent createControlsComponent(S source) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBorder(LineBorder.createGrayLineBorder());
		panel.add(createButtonsComponent(source), BorderLayout.WEST);

		return panel;
	}

	private JComponent createButtonsComponent(S source) {

		return ControlsPanel.horizontal(
					new SourceRelabelButton(source),
					new SourceDeleteButton(source));
	}

	private void addControlTabs() {

		new AdditionHandler();

		if (enableGrab()) {

			new GrabHandler();
		}

		if (sourceCount() > 1) {

			new ReorderHandler();
		}
	}

	private void removeControlTabs() {

		int preRemoveCount = controlTabHandlers.size();

		int i = preRemoveCount;
		int j = preRemoveCount;

		do {

			controlTabHandlers.get(i - 1).remove();
		}
		while (--i > 0);

		do {

			removeTabAt(lastTabIndex());
		}
		while (--j > 0);
	}

	private JLabel createControlTabLabel(String text) {

		JLabel label = createTabLabel(text);

		label.setForeground(CONTROL_LABEL_COLOUR);

		return label;
	}

	private JLabel createTabLabel(String text) {

		JLabel label = new JLabel(text);

		label.setFont(GFonts.toMedium(label.getFont()));

		return label;
	}

	private void updateSourceDeleteButtons() {

		for (SourceDeleteButton button : sourceDeleteButtons.values()) {

			button.updateEnabling();
		}
	}

	private int sourceCount() {

		return getSources().size();
	}

	private int lastSourceIndex() {

		return sourceCount() - 1;
	}

	private int lastTabIndex() {

		return getTabCount() - 1;
	}

	private boolean checkRegisterEdit(boolean wasEdit) {

		if (wasEdit) {

			editManager.registerEdit();
		}

		return wasEdit;
	}
}
