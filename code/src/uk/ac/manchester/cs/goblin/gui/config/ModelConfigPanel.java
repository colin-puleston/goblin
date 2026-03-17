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
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class ModelConfigPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String SECTIONS_TITLE = "Model sections";
	static private final String HIERARCHIES_TITLE = "Core hierarchies";

	private ModelConfig modelConfig;
	private ConfigOntology ontology;

	private class MultiSectionPanel extends MultiTabPanelWithEditControls<ModelSectionConfig> {

		static private final long serialVersionUID = -1;

		protected List<ModelSectionConfig> getSources() {

			return modelConfig.getSections();
		}

		protected String getTitle(ModelSectionConfig section) {

			return section.getLabel();
		}

		protected JComponent createComponent(ModelSectionConfig section) {

			return createSectionComponent(section);
		}

		protected boolean checkNewSource() {

			return false;
		}

		protected boolean checkEditSource(ModelSectionConfig source) {

			return false;
		}

		protected boolean checkDeleteSource(ModelSectionConfig source) {

			return false;
		}

		MultiSectionPanel() {

			super(JTabbedPane.LEFT);

			setFont(GFonts.toLarge(getFont()));

			populate();
		}
	}

	ModelConfigPanel(ModelConfig modelConfig, ConfigOntology ontology) {

		super(new BorderLayout());

		this.modelConfig = modelConfig;
		this.ontology = ontology;

		add(createMainPanel(), BorderLayout.CENTER);
	}

	private JComponent createMainPanel() {

		List<ModelSectionConfig> sections = modelConfig.getSections();

		return sections.size() == 1
				? createSectionComponent(sections.get(0))
				: createMultiSectionComponent();
	}

	private JComponent createMultiSectionComponent() {

		return TitledPanels.create(new MultiSectionPanel(), SECTIONS_TITLE);
	}

	private JComponent createSectionComponent(ModelSectionConfig section) {

		return TitledPanels.create(
					new ModelSectionConfigPanel(section, ontology),
					HIERARCHIES_TITLE);
	}
}
