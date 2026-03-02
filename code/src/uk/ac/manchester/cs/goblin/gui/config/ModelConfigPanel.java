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

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.config.*;

/**
 * @author Colin Puleston
 */
class ModelConfigPanel extends JPanel {

	static private final long serialVersionUID = -1;

	private ModelConfig modelConfig;
	private List<ModelSectionConfigPanel> sectionPanels = new ArrayList<ModelSectionConfigPanel>();

	ModelConfigPanel(ModelConfig modelConfig) {

		super(new BorderLayout());

		this.modelConfig = modelConfig;

		populate();
	}

	private void populate() {

		for (ModelSectionConfig section : modelConfig.getSections()) {

			sectionPanels.add(new ModelSectionConfigPanel(section));
		}

		add(createMainPanel(), BorderLayout.CENTER);
	}

	private JComponent createMainPanel() {

		return sectionPanels.size() == 1 ? sectionPanels.get(0) : createMultiSectionPanel();
	}

	private JComponent createMultiSectionPanel() {

		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);

		tabs.setFont(GFonts.toLarge(tabs.getFont()));

		for (ModelSectionConfigPanel sectionPanel : sectionPanels) {

			tabs.addTab(sectionPanel.getTitle(), sectionPanel);
		}

		return tabs;
	}
}
