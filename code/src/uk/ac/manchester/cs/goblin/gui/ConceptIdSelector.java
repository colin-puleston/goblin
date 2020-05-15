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

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConceptIdSelector extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Enter Concept Identity";

	static private final String OK_LABEL = "Ok";
	static private final String CANCEL_LABEL = "Cancel";

	static private final Dimension WINDOW_SIZE = new Dimension(250, 150);

	private DynamicId selection = null;
	private ControlButton okButton = new ControlButton(OK_LABEL);

	private abstract class InputField extends GTextField {

		static private final long serialVersionUID = -1;

		private InputField otherField = null;
		private boolean userEdited = false;

		protected void onCharEntered(char enteredChar) {

			selection = resolveSelection();
			userEdited = anyText();

			updateForSelection();
			otherField.updateForSelection();

			okButton.setEnabled(userEdited);
		}

		protected void onTextEntered(String text) {

			dispose();
		}

		InputField() {

			userEdited = previousIndependentUserEdits();

			updateForSelection();
		}

		void setOtherField(InputField otherField) {

			this.otherField = otherField;
		}

		JPanel createPanel(String title) {

			return TitledPanels.create(this, title);
		}

		abstract DynamicId resolveSelection(String text);

		abstract DynamicId resolveSelection(String text, String otherText);

		abstract String getTextFromSelection();

		private boolean previousIndependentUserEdits() {

			return selection != null && selection.independentNameAndLabel();
		}

		private DynamicId resolveSelection() {

			String otherText = otherField.checkForUserText();

			if (anyText()) {

				String text = getText();

				return otherText != null
						? resolveSelection(text, otherText)
						: resolveSelection(text);
			}

			return otherText != null ? otherField.resolveSelection(otherText) : null;
		}

		private boolean anyText() {

			return !getText().isEmpty();
		}

		private String checkForUserText() {

			return userEdited ? getText() : null;
		}

		private void updateForSelection() {

			setText(selection != null ? getTextFromSelection() : "");
		}
	}

	private class NameField extends InputField {

		static private final long serialVersionUID = -1;

		protected void onCharEntered(char enteredChar) {

			String text = getText();

			if (text.length() == 1) {

				setText(text.toUpperCase());
			}

			super.onCharEntered(enteredChar);
		}

		DynamicId resolveSelection(String text) {

			return DynamicId.fromName(text);
		}

		DynamicId resolveSelection(String text, String otherText) {

			return new DynamicId(text, otherText);
		}

		String getTextFromSelection() {

			return selection.getName();
		}
	}

	private class LabelField extends InputField {

		static private final long serialVersionUID = -1;

		DynamicId resolveSelection(String text) {

			return DynamicId.fromLabel(text);
		}

		DynamicId resolveSelection(String text, String otherText) {

			return new DynamicId(otherText, text);
		}

		String getTextFromSelection() {

			return selection.getLabel();
		}
	}

	private class ControlButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			if (getText().equals(CANCEL_LABEL)) {

				selection = null;
			}

			dispose();
		}

		ControlButton(String label) {

			super(label);
		}
	}

	private class WindowCloseListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			selection = null;
		}
	}

	ConceptIdSelector(JComponent parent, DynamicId initialSelection) {

		super(parent, TITLE, true);

		selection = initialSelection;

		okButton.setEnabled(false);
		addWindowListener(new WindowCloseListener());

		display(createDisplay());
	}

	DynamicId getSelection() {

		return selection;
	}

	private JComponent createDisplay() {

		JPanel panel = new JPanel();

		InputField nameField = new NameField();
		InputField labelField = new LabelField();

		nameField.setOtherField(labelField);
		labelField.setOtherField(nameField);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPreferredSize(WINDOW_SIZE);
		panel.add(nameField.createPanel("Name"));
		panel.add(labelField.createPanel("Label"));
		panel.add(createButtonsPanel());

		return panel;
	}

	private JPanel createButtonsPanel() {

		return ControlsPanel.horizontal(okButton, new ControlButton(CANCEL_LABEL));
	}
}


