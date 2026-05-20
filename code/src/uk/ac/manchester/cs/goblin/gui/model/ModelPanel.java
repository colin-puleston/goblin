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

package uk.ac.manchester.cs.goblin.gui.model;

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class ModelPanel extends JPanel {

	static private final long serialVersionUID = -1;

	private Model model;
	private MultiSectionPanel multiSectionPanel;

	private Map<ModelSection, ModelSectionPanel> sectionPanels
					= new HashMap<ModelSection, ModelSectionPanel>();

	private class MultiSectionPanel extends ArrayPanel<ModelSection> {

		static private final long serialVersionUID = -1;

		protected List<ModelSection> getSources() {

			return ModelPanel.this.model.getSections();
		}

		protected String getTitle(ModelSection section) {

			return section.getLabel();
		}

		protected JComponent createComponent(ModelSection section) {

			return createSectionPanel(section);
		}

		MultiSectionPanel() {

			super(JTabbedPane.TOP);

			setFont(GFonts.toLarge(getFont()));

			populate();
		}
	}

	ModelPanel(Model model) {

		super(new BorderLayout());

		this.model = model;

		add(createMainPanel(), BorderLayout.CENTER);
	}

	void makeEditVisible(ModelEditLocation location) {

		for (ModelSection section : model.getSections()) {

			ModelSectionPanel panel = sectionPanels.get(section);

			if (panel.checkMakeEditVisible(location)) {

				if (multiSectionPanel != null) {

					multiSectionPanel.makeSourceVisible(section);
				}

				break;
			}
		}
	}

	private JComponent createMainPanel() {

		List<ModelSection> sections = model.getSections();

		if (sections.size() == 1) {

			return createSectionPanel(sections.get(0));
		}

		multiSectionPanel = new MultiSectionPanel();

		return multiSectionPanel;
	}

	protected ModelSectionPanel createSectionPanel(ModelSection section) {

		ModelSectionPanel panel = new ModelSectionPanel(section);

		sectionPanels.put(section, panel);

		return panel;
	}
}
