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

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class DynamicAttributeEditPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Edit dynamic attribute";
	static private final String VALUES_DIALOG_TITLE = "Values hierarchy";

	static private final String RESET_ID_LABEL = "Id...";
	static private final String VALUES_LABEL = "Values...";
	static private final String REMOVE_LABEL = "Del";

	static private final Dimension VALUES_DIALOG_SIZE = new Dimension(500, 600);

	private DynamicConstraintType type;

	private class ValuesEditDialog extends GDialog {

		static private final long serialVersionUID = -1;

		ValuesEditDialog() {

			super(VALUES_DIALOG_TITLE, true);

			setPreferredSize(VALUES_DIALOG_SIZE);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			display(new HierarchyTreePanel(getValuesHierarchy()));
		}
	}

	private class ResetIdButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			checkResetAttributeId();
		}

		ResetIdButton() {

			super(RESET_ID_LABEL);
		}
	}

	private class ValuesButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			new ValuesEditDialog();
		}

		ValuesButton() {

			super(VALUES_LABEL);
		}
	}

	private class RemoveButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			checkRemoveAttribute();
		}

		RemoveButton() {

			super(REMOVE_LABEL);
		}
	}

	DynamicAttributeEditPanel(ConstraintType type) {

		super(new BorderLayout());

		this.type = (DynamicConstraintType)type;

		TitledPanels.setTitle(this, TITLE);

		add(createEditButtonsPanel(), BorderLayout.WEST);
		add(new RemoveButton(), BorderLayout.EAST);
	}

	private JPanel createValuesPanel() {

		return new HierarchyTreePanel(type.getRootTargetConcept().getHierarchy());
	}

	private JPanel createEditButtonsPanel() {

		return ControlsPanel.horizontal(new ResetIdButton(), new ValuesButton());
	}

	private boolean checkResetAttributeId() {

		EntityId currentId = type.getTargetPropertyId();
		EntityId newId = checkObtainAttributeId(currentId);

		if (newId != null) {

			Concept source = type.getRootSourceConcept();

			if (source.applicableDynamicConstraintType(newId)) {

				showAttributeAlreadyExistsMessage(source);
			}
			else {

				type.resetAttributeId(newId);
			}
		}

		return false;
	}

	private void checkRemoveAttribute() {

		if (obtainAttributeRemovalConfirmation()) {

			type.remove();
		}
	}

	private EntityId checkObtainAttributeId(EntityId currentId) {

		return new AttributeIdSelector(this, currentId).getSelection();
	}

	private void showAttributeAlreadyExistsMessage(Concept source) {

		String sourceName = source.getConceptId().getName();

		InfoDisplay.inform("Attribute already exists for concept: " + sourceName);
	}

	private boolean obtainAttributeRemovalConfirmation() {

		return InfoDisplay.checkContinue(createRemovalConfirmationMessage());
	}

	private String createRemovalConfirmationMessage() {

		StringBuilder msg = new StringBuilder();

		msg.append("Removing attribute: " + type.getTargetPropertyId().getLabel());
		msg.append(" plus any associated constraints");

		return msg.toString();
	}

	private Hierarchy getValuesHierarchy() {

		return type.getRootTargetConcept().getHierarchy();
	}
}
