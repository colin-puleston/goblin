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

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ModelEditPanel extends JPanel {

	static private final long serialVersionUID = -1;

	private ModelHandler modelHandler;
	private List<ModelSectionPanel> sectionPanels = new ArrayList<ModelSectionPanel>();

	ModelEditPanel(ModelHandler modelHandler) {

		super(new BorderLayout());

		this.modelHandler = modelHandler;

		populate();
	}

	void repopulate() {

		removeAll();
		populate();
	}

	void makeEditVisible(EditLocation location) {

		for (ModelSectionPanel sectionPanel : sectionPanels) {

			if (sectionPanel.checkMakeEditVisible(location)) {

				break;
			}
		}
	}

	private void populate() {

		sectionPanels.clear();

		for (ModelSection section : modelHandler.getModel().getSections()) {

			sectionPanels.add(new ModelSectionPanel(section));
		}

		add(createMainPanel(), BorderLayout.CENTER);
	}

	private JComponent createMainPanel() {

		return sectionPanels.size() == 1 ? sectionPanels.get(0) : createMultiSectionPanel();
	}

	private JComponent createMultiSectionPanel() {

		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);

		tabs.setFont(GFonts.toLarge(tabs.getFont()));

		for (ModelSectionPanel sectionPanel : sectionPanels) {

			tabs.addTab(sectionPanel.getTitle(), sectionPanel);
		}

		return tabs;
	}
}
