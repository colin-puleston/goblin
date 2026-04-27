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
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class EntityReorderDialog<E extends LabelledConfigObject> extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "Modify %s order";

	static private final String OK_LABEL = "Ok";
	static private final String CANCEL_LABEL = "Cancel";

	static private final String UP_LABEL = "Up";
	static private final String DOWN_LABEL = "Down";

	static private final Dimension WINDOW_SIZE = new Dimension(400, 400);

	private List<E> initialOrder;
	private List<E> currentOrder = new ArrayList<E>();

	private E currentSelection = null;

	private JPanel reorderPanel = new JPanel(new BorderLayout());

	private OkButton okButton = new OkButton();

	private UpButton upButton = new UpButton();
	private DownButton downButton = new DownButton();

	private class OkButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			dispose();
		}

		OkButton() {

			super(OK_LABEL);

			setEnabled(false);
		}

		void updateEnabling() {

			setEnabled(reordered());
		}
	}

	private class CancelButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			currentOrder = initialOrder;

			dispose();
		}

		CancelButton() {

			super(CANCEL_LABEL);
		}
	}

	private abstract class NavigationButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			int fromIndex = currentOrder.indexOf(currentSelection);
			int toIndex = fromIndex + directionShiftValue();

			currentOrder.remove(fromIndex);
			currentOrder.add(toIndex, currentSelection);

			setCurrentDisplayList(toIndex);

			reorderPanel.revalidate();
		}

		NavigationButton(String label) {

			super(label);

			setEnabled(false);
		}

		JComponent createButtonComponent() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(this, getButtonWithinComponentLocation());

			return panel;
		}

		void updateEnabling() {

			setEnabled(canMoveInDirection());
		}

		abstract int directionFinalIndex();

		abstract int directionShiftValue();

		abstract String getButtonWithinComponentLocation();

		private boolean canMoveInDirection() {

			return currentSelection != null && finalDirectionEntitySelected();
		}

		private boolean finalDirectionEntitySelected() {

			return currentSelection != currentOrder.get(directionFinalIndex());
		}
	}

	private class UpButton extends NavigationButton {

		static private final long serialVersionUID = -1;

		UpButton() {

			super(UP_LABEL);
		}

		int directionFinalIndex() {

			return 0;
		}

		int directionShiftValue() {

			return -1;
		}

		String getButtonWithinComponentLocation() {

			return BorderLayout.SOUTH;
		}
	}

	private class DownButton extends NavigationButton {

		static private final long serialVersionUID = -1;

		DownButton() {

			super(DOWN_LABEL);
		}

		int directionFinalIndex() {

			return currentOrder.size() - 1;
		}

		int directionShiftValue() {

			return 1;
		}

		String getButtonWithinComponentLocation() {

			return BorderLayout.NORTH;
		}
	}

	private class DisplayList extends GList<E> {

		static private final long serialVersionUID = -1;

		private class CurrentSelectionListener extends GSelectionListener<E> {

			protected void onSelected(E selected) {

				currentSelection = selected;

				upButton.updateEnabling();
				downButton.updateEnabling();

				okButton.updateEnabling();
			}

			protected void onDeselected(E selected) {
			}

			CurrentSelectionListener() {

				addSelectionListener(this);
			}
		}

		DisplayList(int selectIndex) {

			super(false, false);

			for (E entity : currentOrder) {

				addEntity(entity);
			}

			new CurrentSelectionListener();

			setSelectedIndex(selectIndex);
		}
	}

	EntityReorderDialog(List<E> initialOrder, String typeName) {

		super(String.format(TITLE_FORMAT, typeName), true);

		this.initialOrder = initialOrder;

		currentOrder.addAll(initialOrder);

		setPreferredSize(WINDOW_SIZE);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		initialseReorderPanel();

		display(createMainComponent());
	}

	boolean reordered() {

		return !currentOrder.equals(initialOrder);
	}

	List<E> getCurrentOrder() {

		return currentOrder;
	}

	private void initialseReorderPanel() {

		setCurrentDisplayList(-1);

		reorderPanel.add(createNavigationButtonsComponent(), BorderLayout.EAST);
	}

	private void setCurrentDisplayList(int selectIndex) {

		GList<E> displayList = new DisplayList(selectIndex);

		reorderPanel.add(new JScrollPane(displayList), BorderLayout.CENTER);
	}

	private JComponent createMainComponent() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(reorderPanel, BorderLayout.CENTER);
		panel.add(createExitButtonsComponent(), BorderLayout.SOUTH);

		return panel;
	}

	private JComponent createExitButtonsComponent() {

		return ControlsPanel.horizontal(okButton, new CancelButton());
	}

	private JComponent createNavigationButtonsComponent() {

		return ControlsPanel.vertical(
				upButton.createButtonComponent(),
				downButton.createButtonComponent());
	}
}
