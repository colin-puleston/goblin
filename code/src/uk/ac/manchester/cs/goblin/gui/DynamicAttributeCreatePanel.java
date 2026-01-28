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

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class DynamicAttributeCreatePanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Create dynamic attribute...";

	static private final String BUTTON_LABEL_FORMAT = "...with %s values...";
	static private final String NEW_VALUES_DESCRIPTOR = "new";
	static private final String LINKED_VALUES_DESCRIPTOR = "linked";

	static private final String NEW_ROOT_TARGET_CONCEPT_NAME_FORMAT = "%s-%s-value";

	private HierarchyTree hierarchyTree;
	private ButtonEnabler buttonEnabler;

	private class ButtonEnabler extends GSelectionListener<GNode> {

		final List<CreateButton> buttons = new ArrayList<CreateButton>();

		protected void onSelected(GNode node) {

			updateEnabling();
		}

		protected void onDeselected(GNode node) {

			updateEnabling();
		}

		ButtonEnabler() {

			hierarchyTree.addNodeSelectionListener(this);
		}

		void updateEnabling() {

			for (CreateButton button : buttons) {

				button.updateEnabling();
			}
		}
	}

	private abstract class CreateButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			EntityId attrId = checkObtainAttributeId();

			if (attrId != null) {

				Concept source = getSelectedConcept();

				if (source.applicableDynamicAttribute(attrId)) {

					showAttributeAlreadyExistsMessage(source);
				}
				else {

					checkCreateAttribute(source, attrId);

					buttonEnabler.updateEnabling();
				}
			}
		}

		CreateButton(String valuesSourceDescriptor) {

			super(String.format(BUTTON_LABEL_FORMAT, valuesSourceDescriptor));

			updateEnabling();

			buttonEnabler.buttons.add(this);
		}

		void updateEnabling() {

			setEnabled(enabled());
		}

		boolean enabled() {

			return getSelectedConcept() != null;
		}

		abstract void checkCreateAttribute(Concept source, EntityId attrId);

		private EntityId checkObtainAttributeId() {

			return new AttributeIdSelector(this, null).getSelection();
		}

		private void showAttributeAlreadyExistsMessage(Concept source) {

			String sourceName = source.getConceptId().getName();

			InfoDisplay.inform("Attribute already exists for concept: " + sourceName);
		}
	}

	private class CreateWithNewValuesButton extends CreateButton {

		static private final long serialVersionUID = -1;

		CreateWithNewValuesButton() {

			super(NEW_VALUES_DESCRIPTOR);
		}

		void checkCreateAttribute(Concept source, EntityId attrId) {

			EntityId rootTargetId = createRootTargetConceptId(attrId, source);
			DynamicAttribute attribute = source.addDynamicAttribute(attrId, rootTargetId);

			if (attribute != null) {

				new DynamicAttributeValuesEditDialog(attribute);
			}
		}

		private EntityId createRootTargetConceptId(EntityId attrId, Concept source) {

			String name = createRootTargetConceptName(attrId, source);
			EntityId id = new DynamicId(name);

			int suffix = 0;

			while (conceptExists(id)) {

				id = new DynamicId(name + "-" + suffix++);
			}

			return id;
		}

		private String createRootTargetConceptName(EntityId attrId, Concept source) {

			return String.format(
					NEW_ROOT_TARGET_CONCEPT_NAME_FORMAT,
					source.getConceptId().getName(),
					attrId.getName());
		}
	}

	private class CreateWithLinkedValuesButton extends CreateButton {

		static private final long serialVersionUID = -1;

		CreateWithLinkedValuesButton() {

			super(LINKED_VALUES_DESCRIPTOR);
		}

		boolean enabled() {

			return super.enabled() && existingDynamicAttributes();
		}

		void checkCreateAttribute(Concept source, EntityId attrId) {

			Hierarchy valuesHierarchy = getLinkedValuesHierarchy(source);

			if (valuesHierarchy != null) {

				source.addDynamicAttribute(attrId, valuesHierarchy.getRootConcept());
			}
		}

		private Hierarchy getLinkedValuesHierarchy(Concept source) {

			return new DynamicAttributeLinkedValuesSelector(source).getSelectedValuesHierarchy();
		}

		private boolean existingDynamicAttributes() {

			for (Hierarchy hierarchy : getHierarchy().getModel().getCoreHierarchies()) {

				if (hierarchy.hasDynamicAttributes()) {

					return true;
				}
			}

			return false;
		}
	}

	DynamicAttributeCreatePanel(HierarchyTree hierarchyTree) {

		super(new BorderLayout());

		this.hierarchyTree = hierarchyTree;

		buttonEnabler = new ButtonEnabler();

		TitledPanels.setTitle(this, TITLE);

		add(createButtonsPanel(), BorderLayout.CENTER);
	}

	private JPanel createButtonsPanel() {

		return ControlsPanel.horizontal(
					new CreateWithNewValuesButton(),
					new CreateWithLinkedValuesButton());
	}

	private Concept getSelectedConcept() {

		return hierarchyTree.getSelectedConcept();
	}

	private boolean conceptExists(EntityId id) {

		return getHierarchy().getModel().containsConcept(id);
	}

	private Hierarchy getHierarchy() {

		return hierarchyTree.getHierarchy();
	}
}
