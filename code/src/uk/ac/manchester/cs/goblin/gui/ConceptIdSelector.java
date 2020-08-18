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
import java.awt.event.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConceptIdSelector extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Enter Concept Identity";

	static private final String NAME_FIELD_TITLE = "Name";
	static private final String LABEL_FIELD_TITLE = "Label";

	static private final String OK_BUTTON_LABEL = "Ok";
	static private final String CANCEL_BUTTON_LABEL = "Cancel";
	static private final String AUTOSET_BUTTON_LABEL = "auto";

	static private final Dimension WINDOW_SIZE = new Dimension(300, 150);

	private DynamicId currentId;
	private DynamicId selectedId = null;

	private ControlButton okButton = new ControlButton(OK_BUTTON_LABEL);

	private abstract class InputPanel extends JPanel {

		static private final long serialVersionUID = -1;

		private String initialValue;
		private String currentValue;

		private InputPanel otherInput = null;

		private ValueField valueField = null;
		private AutoSetButton autoSetButton = new AutoSetButton();

		private class ValueField extends GTextField {

			static private final long serialVersionUID = -1;

			protected void onCharEntered(char enteredChar) {

				String newValue = getText();
				String resolvedValue = resolveNewValue(newValue);

				if (resolvedValue != null) {

					setCurrentValue(resolvedValue);
				}
				else {

					setText(currentValue);
				}
			}

			protected void onTextEntered(String text) {

				dispose();
			}

			ValueField() {

				setText(initialValue);
			}

			void setCurrentValue(String value) {

				if (!value.equals(getText())) {

					setText(value);
				}

				currentValue = value;
				selectedId = resolveSelection();

				okButton.setEnabled(selectedId != null);
				otherInput.autoSetButton.setEnabling();
			}
		}

		private class AutoSetButton extends GButton {

			static private final long serialVersionUID = -1;

			protected void doButtonThing() {

				valueField.setCurrentValue(createAutoValue(otherInput.currentValue));

				setEnabled(false);
			}

			AutoSetButton() {

				super(AUTOSET_BUTTON_LABEL);

				setEnabled(false);
			}

			void setEnabling() {

				setEnabled(canSetAutoValue());
			}
		}

		InputPanel(String title) {

			super(new BorderLayout());

			TitledPanels.setTitle(this, title);

			initialValue = currentId != null ? extractValue(currentId) : "";
			currentValue = initialValue;
		}

		void setOtherInput(InputPanel otherInput) {

			this.otherInput = otherInput;
		}

		void initialise() {

			valueField = new ValueField();
			autoSetButton = new AutoSetButton();

			add(valueField, BorderLayout.CENTER);
			add(autoSetButton, BorderLayout.EAST);
		}

		abstract String extractValue(DynamicId id);

		abstract String resolveNewValue(String newValue);

		abstract String createAutoValue(String otherValue);

		abstract String createAutoValueOrNull(String otherValue);

		abstract DynamicId createSelection(String value, String otherValue);

		private boolean canSetAutoValue() {

			String otherValue = otherInput.currentValue;

			if (otherValue.isEmpty()) {

				return false;
			}

			String autoValue = createAutoValueOrNull(otherValue);

			return autoValue != null && !autoValue.equals(currentValue);
		}

		private DynamicId resolveSelection() {

			if (valueEdited() || otherInput.valueEdited()) {

				return valuePresent() ? otherInput.resolveSelection(currentValue) : null;
			}

			return null;
		}

		private DynamicId resolveSelection(String otherValue) {

			return valuePresent() ? createSelection(currentValue, otherValue) : null;
		}

		private boolean valuePresent() {

			return !currentValue.isEmpty();
		}

		private boolean valueEdited() {

			return !currentValue.equals(initialValue);
		}
	}

	private class NamePanel extends InputPanel {

		static private final long serialVersionUID = -1;

		NamePanel() {

			super(NAME_FIELD_TITLE);
		}

		String extractValue(DynamicId id) {

			return id.getName();
		}

		String resolveNewValue(String newValue) {

			if (newValue.isEmpty()) {

				return newValue;
			}

			if (DynamicId.validName(newValue)) {

				return ensureStartsWithUpperCase(newValue);
			}

			return null;
		}

		String createAutoValue(String otherValue) {

			String value = createAutoValueOrNull(otherValue);

			if (value == null) {

				throw new Error("Should never happen!");
			}

			return value;
		}

		String createAutoValueOrNull(String otherValue) {

			DynamicId id = DynamicId.fromLabelOrNull(otherValue);

			return id != null ? id.getName() : null;
		}

		DynamicId createSelection(String value, String otherValue) {

			return new DynamicId(value, otherValue);
		}

		private String ensureStartsWithUpperCase(String value) {

			char first = value.charAt(0);

			if (Character.isLowerCase(first)) {

				return "" + Character.toUpperCase(first) + value.substring(1);
			}

			return value;
		}
	}

	private class LabelPanel extends InputPanel {

		static private final long serialVersionUID = -1;

		LabelPanel() {

			super(LABEL_FIELD_TITLE);
		}

		String extractValue(DynamicId id) {

			return id.getLabel();
		}

		String resolveNewValue(String newValue) {

			return newValue;
		}

		String createAutoValue(String otherValue) {

			return DynamicId.fromName(otherValue).getLabel();
		}

		String createAutoValueOrNull(String otherValue) {

			return createAutoValue(otherValue);
		}

		DynamicId createSelection(String value, String otherValue) {

			return new DynamicId(otherValue, value);
		}
	}

	private class ControlButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			if (getText().equals(CANCEL_BUTTON_LABEL)) {

				selectedId = null;
			}

			dispose();
		}

		ControlButton(String label) {

			super(label);
		}
	}

	private class WindowCloseListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			selectedId = null;
		}
	}

	ConceptIdSelector(JComponent parent, DynamicId currentId) {

		super(parent, TITLE, true);

		this.currentId = currentId;

		okButton.setEnabled(false);
		addWindowListener(new WindowCloseListener());

		display(createDisplay());
	}

	DynamicId getSelection() {

		return selectedId;
	}

	private JComponent createDisplay() {

		JPanel panel = new JPanel();

		InputPanel namePanel = new NamePanel();
		InputPanel labelPanel = new LabelPanel();

		namePanel.setOtherInput(labelPanel);
		labelPanel.setOtherInput(namePanel);

		namePanel.initialise();
		labelPanel.initialise();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(WINDOW_SIZE);
		panel.add(namePanel);
		panel.add(labelPanel);
		panel.add(createButtonsPanel());

		return panel;
	}

	private JPanel createButtonsPanel() {

		return ControlsPanel.horizontal(okButton, new ControlButton(CANCEL_BUTTON_LABEL));
	}
}


