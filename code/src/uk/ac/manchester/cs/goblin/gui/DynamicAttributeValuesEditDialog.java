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
class DynamicAttributeValuesEditDialog extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Values hierarchy";
	static private final String DONE_LABEL = "Done";

	static private final Dimension WINDOW_SIZE = new Dimension(500, 600);

	private HierarchyTree hierarchyTree;

	private class DoneButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			hierarchyTree.clearConceptListeners();

			dispose();
		}

		DoneButton() {

			super(DONE_LABEL);
		}
	}

	private class WindowCloseListener extends WindowAdapter {

		public void windowClosing(WindowEvent e) {

			hierarchyTree.clearConceptListeners();
		}
	}

	DynamicAttributeValuesEditDialog(DynamicAttribute attribute) {

		super(TITLE, true);

		hierarchyTree = createHierarchyTree(attribute);

		setPreferredSize(WINDOW_SIZE);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		display(createMainComponent());
	}

	private JComponent createMainComponent() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new HierarchyTreePanel(hierarchyTree), BorderLayout.CENTER);
		panel.add(new DoneButton(), BorderLayout.SOUTH);

		return panel;
	}

	private HierarchyTree createHierarchyTree(DynamicAttribute attribute) {

		return new HierarchyTree(attribute.getRootTargetConcept().getHierarchy());
	}
}
