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
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class ValuesPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String EDIT_INVOKE_BUTTON_LABEL = "...";

	static private final int GAP_SIZE = 10;

	static private final Color DEFAULT_TEXT_CLR = Color.BLACK;
	static private final Color ENUM_TEXT_CLR = Color.GREEN.darker().darker();
	static private final Color CONCEPT_TEXT_CLR = Color.BLUE;
	static private final Color PROPERTY_TEXT_CLR = Color.ORANGE.darker();

	static private final Color DEFAULT_BACKGROUND_CLR = Color.WHITE;
	static private final Color INFO_BACKGROUND_CLR = Color.LIGHT_GRAY;

	private ConfigOntology ontology;

	private List<Value<?>> values = new ArrayList<Value<?>>();
	private List<ValuesPanelListener> listeners = new ArrayList<ValuesPanelListener>();

	abstract class Value<V> {

		Value() {

			values.add(this);
		}

		abstract boolean viewOnly();

		abstract String getTitle();

		abstract boolean isSet();

		abstract V get();

		String getText() {

			return isSet() ? get().toString() : "";
		}

		Color getTextColour() {

			return DEFAULT_TEXT_CLR;
		}

		Color getBackgroundColour() {

			return DEFAULT_BACKGROUND_CLR;
		}
	}

	abstract class InfoValue extends Value<String> {

		boolean viewOnly() {

			return true;
		}

		boolean isSet() {

			return true;
		}

		Color getBackgroundColour() {

			return INFO_BACKGROUND_CLR;
		}
	}

	abstract class EditableValue<V> extends Value<V> {

		private V value = null;

		boolean viewOnly() {

			return false;
		}

		boolean isSet() {

			return value != null;
		}

		void set(V value) {

			this.value = value;
		}

		V get() {

			if (!isSet()) {

				throw new Error("Value not set!");
			}

			return value;
		}

		boolean perfomInputOp() {

			V value = checkInput();

			if (value != null) {

				set(value);

				return true;
			}

			return false;
		}

		abstract V checkInput();
	}

	abstract class LabelValue extends EditableValue<String> {

		String getTitle() {

			return "Label";
		}

		String checkInput() {

			return new StringInputter(ValuesPanel.this, getTitle()).getInput();
		}
	}

	abstract class EnumValue<E extends Enum<?>> extends EditableValue<E> {

		Color getTextColour() {

			return ENUM_TEXT_CLR;
		}

		E checkInput() {

			return null;
		}
	}

	abstract class EntityIdValue extends EditableValue<EntityId> {

		EntityId checkInput() {

			return createSelectorDialog().getSelectionIdOrNull();
		}

		abstract ConfigEntitySelectorDialog createSelectorDialog();
	}

	abstract class ConceptIdValue extends EntityIdValue {

		Color getTextColour() {

			return CONCEPT_TEXT_CLR;
		}

		ConfigEntitySelectorDialog createSelectorDialog() {

			return new ConceptSelectorDialog(ValuesPanel.this, ontology);
		}
	}

	abstract class PropertyIdValue extends EntityIdValue {

		Color getTextColour() {

			return PROPERTY_TEXT_CLR;
		}

		ConfigEntitySelectorDialog createSelectorDialog() {

			return new PropertySelectorDialog(ValuesPanel.this, ontology);
		}
	}

	private class EditInvokeButton extends GButton {

		static private final long serialVersionUID = -1;

		private EditableValue<?> value;

		protected void doButtonThing() {

			if (value.perfomInputOp()) {

				reinitialisePostEdit();
				pollListenersForEdit();
			}
		}

		EditInvokeButton(EditableValue<?> value) {

			super(EDIT_INVOKE_BUTTON_LABEL);

			this.value = value;
		}
	}

	private abstract class Initialser {

		Initialser() {

			add(createInnerPanel(), BorderLayout.NORTH);
		}

		abstract JComponent createValueComponent(Value<?> value);

		private JPanel createInnerPanel() {

			JPanel panel = new JPanel();

			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			populateInnerPanel(panel);

			return panel;
		}

		private void populateInnerPanel(JPanel panel) {

			boolean first = true;

			for (Value<?> value : values) {

				if (requiredValue(value)) {

					if (first) {

						first = false;
					}
					else {

						panel.add(Box.createVerticalStrut(GAP_SIZE));
					}

					panel.add(createValuePanel(value));
				}
			}
		}

		abstract boolean requiredValue(Value<?> value);

		private JComponent createValuePanel(Value<?> value) {

			JPanel panel = new JPanel(new GridLayout(1, 1));

			TitledPanels.setTitle(panel, value.getTitle());
			panel.add(createValueComponent(value));

			return panel;
		}
	}

	private class ViewInitialser extends Initialser {

		boolean requiredValue(Value<?> value) {

			return true;
		}

		JComponent createValueComponent(Value<?> value) {

			return createViewComponent(value);
		}
	}

	private class EditInitialser extends Initialser {

		boolean requiredValue(Value<?> value) {

			return !value.viewOnly();
		}

		JComponent createValueComponent(Value<?> value) {

			return createEditComponent((EditableValue<?>)value);
		}
	}

	ValuesPanel(ConfigOntology ontology) {

		super(new BorderLayout());

		this.ontology = ontology;

		setBorderGap(GAP_SIZE);
	}

	void initialse(boolean forEdit) {

		if (forEdit) {

			new EditInitialser();
		}
		else {

			new ViewInitialser();
		}
	}

	void addListener(ValuesPanelListener listener) {

		listeners.add(listener);
	}

	boolean allSet() {

		for (Value<?> value : values) {

			if (!value.isSet()) {

				return false;
			}
		}

		return true;
	}

	private void reinitialisePostEdit() {

		removeAll();

		new EditInitialser();

		revalidate();
	}

	private void pollListenersForEdit() {

		for (ValuesPanelListener listener : listeners) {

			listener.onValueEdit();
		}
	}

	private JComponent createViewComponent(Value<?> value) {

		JTextField field = new JTextField(value.getText());

		GFonts.setLarge(field);

		field.setForeground(value.getTextColour());
		field.setBackground(value.getBackgroundColour());
		field.setEditable(false);

		return field;
	}

	private JComponent createEditComponent(EditableValue<?> value) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createViewComponent(value), BorderLayout.CENTER);
		panel.add(new EditInvokeButton(value), BorderLayout.EAST);

		return panel;
	}

	private void setBorderGap(int size) {

		setBorder(new EmptyBorder(size, size, size, size));
	}
}
