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
import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class ValuesPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String EDIT_INVOKE_BUTTON_LABEL = "...";

	static private final int GAP_SIZE = 10;

	static private final Color EDIT_BACKGROUND_CLR = Color.WHITE;
	static private final Color INFO_BACKGROUND_CLR = Color.LIGHT_GRAY;

	private EditManager editManager;

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

			return isSet() ? valueToText(get()) : "";
		}

		abstract Color getTextColour();

		Color getBackgroundColour() {

			return EDIT_BACKGROUND_CLR;
		}

		String valueToText(V value) {

			return value.toString();
		}
	}

	abstract class InfoValue extends Value<String> {

		boolean viewOnly() {

			return true;
		}

		boolean isSet() {

			return true;
		}

		Color getTextColour() {

			return ValueTextColour.GENRAL_VALUE;
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

		Color getBackgroundColour() {

			return EDIT_BACKGROUND_CLR;
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

	abstract class EnumValue<E extends Enum<?>> extends EditableValue<E> {

		Color getTextColour() {

			return ValueTextColour.GENRAL_VALUE;
		}

		E checkInput() {

			return createSelector().getSelectionOrNull();
		}

		abstract E[] getOptions();

		private EnumValueSelector<E> createSelector() {

			return new EnumValueSelector<E>(getOptions(), getTitle().toLowerCase());
		}
	}

	abstract class HierarchyValue extends EditableValue<CoreHierarchyConfig> {

		void set(EntityId rootConceptId) {

			set(editManager.findHierarchy(rootConceptId));
		}

		Color getTextColour() {

			return ValueTextColour.CONCEPT_VALUE;
		}

		CoreHierarchyConfig checkInput() {

			return createSelector().getSelectionOrNull();
		}

		String valueToText(CoreHierarchyConfig value) {

			return value.getLabel();
		}

		List<CoreHierarchyConfig> getOptions() {

			return editManager.getHierarchies();
		}

		private HierarchySelector createSelector() {

			return new HierarchySelector(getOptions());
		}
	}

	abstract class EntityIdValue extends EditableValue<EntityId> {

		EntityId checkInput() {

			ConfigOntology ontology = editManager.getOntology();
			ConfigEntitySelectorDialog dialog = createSelectorDialog(ontology);

			dialog.display();

			return dialog.getSelectionIdOrNull();
		}

		abstract ConfigEntitySelectorDialog createSelectorDialog(ConfigOntology ontology);
	}

	abstract class ConceptIdValue extends EntityIdValue {

		Color getTextColour() {

			return ValueTextColour.CONCEPT_VALUE;
		}

		ConfigEntitySelectorDialog createSelectorDialog(ConfigOntology ontology) {

			return new ConceptSelectorDialog(ValuesPanel.this, ontology);
		}
	}

	abstract class PropertyIdValue extends EntityIdValue {

		Color getTextColour() {

			return ValueTextColour.PROPERTY_VALUE;
		}

		ConfigEntitySelectorDialog createSelectorDialog(ConfigOntology ontology) {

			return new PropertySelectorDialog(ValuesPanel.this, ontology);
		}
	}

	private class EditInvokeButton extends GButton {

		static private final long serialVersionUID = -1;

		private EditableValue<?> value;

		protected void doButtonThing() {

			if (value.perfomInputOp()) {

				pollListenersForEdit();
				reinitialise();
			}
		}

		EditInvokeButton(EditableValue<?> value) {

			super(EDIT_INVOKE_BUTTON_LABEL);

			this.value = value;
		}
	}

	ValuesPanel(EditManager editManager) {

		super(new BorderLayout());

		this.editManager = editManager;

		setBorderGap(GAP_SIZE);
	}

	void initialise() {

		add(createInnerPanel(), BorderLayout.NORTH);
	}

	void addListener(ValuesPanelListener listener) {

		listeners.add(listener);
	}

	boolean allValuesSet() {

		for (Value<?> value : values) {

			if (!value.isSet()) {

				return false;
			}
		}

		return true;
	}

	private void reinitialise() {

		removeAll();
		initialise();
		revalidate();
	}

	private void setBorderGap(int size) {

		setBorder(new EmptyBorder(size, size, size, size));
	}

	private JPanel createInnerPanel() {

		JPanel panel = new JPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		populateInnerPanel(panel);

		return panel;
	}

	private void populateInnerPanel(JPanel panel) {

		boolean first = true;

		for (Value<?> value : values) {

			if (first) {

				first = false;
			}
			else {

				panel.add(Box.createVerticalStrut(GAP_SIZE));
			}

			panel.add(createValuePanel(value));
		}
	}

	private JComponent createValuePanel(Value<?> value) {

		JPanel panel = new JPanel(new GridLayout(1, 1));

		TitledPanels.setTitle(panel, value.getTitle());
		panel.add(createValueComponent(value));

		return panel;
	}

	private JComponent createValueComponent(Value<?> value) {

		if (value.viewOnly()) {

			return createViewComponent(value);
		}

		return createEditComponent((EditableValue<?>)value);
	}

	private JComponent createEditComponent(EditableValue<?> value) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(createViewComponent(value), BorderLayout.CENTER);
		panel.add(new EditInvokeButton(value), BorderLayout.EAST);

		return panel;
	}

	private JComponent createViewComponent(Value<?> value) {

		JTextField field = new JTextField(value.getText());

		GFonts.setLarge(field);

		field.setForeground(value.getTextColour());
		field.setBackground(value.getBackgroundColour());
		field.setEditable(false);

		return field;
	}

	private void pollListenersForEdit() {

		for (ValuesPanelListener listener : listeners) {

			listener.onValueEdit();
		}
	}
}
