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

	private EditManager editManager;
	private ModelConfig modelConfig;

	private class MultiSectionPanel extends ConfigEditPanel<ModelSectionConfig> {

		static private final long serialVersionUID = -1;

		protected List<ModelSectionConfig> getSources() {

			return getSections();
		}

		protected String getTitle(ModelSectionConfig section) {

			return section.getLabel();
		}

		protected JComponent createComponent(ModelSectionConfig section) {

			return createSectionComponent(section);
		}

		MultiSectionPanel() {

			super(editManager, JTabbedPane.LEFT);

			setFont(GFonts.toLarge(getFont()));

			populate();
		}

		boolean checkNewSource() {

			String label = checkInputSourceLabel();

			if (label != null) {

				modelConfig.addSection(label);

				return true;
			}

			return false;
		}

		boolean enableDelete() {

			return !singleSection();
		}

		void deleteSource(ModelSectionConfig section) {

			modelConfig.removeSection(section);
		}

		void reorderSources(List<ModelSectionConfig> newOrderedSections) {

			modelConfig.reorderSections(newOrderedSections);
		}

		String getSourceTypeName() {

			return "section";
		}
	}

	ModelConfigPanel(EditManager editManager) {

		super(new BorderLayout());

		this.editManager = editManager;

		modelConfig = editManager.getModelConfig();

		populate();
	}

	boolean setMultiSections(boolean multiSections) {

		if (multiSections ? toMultiSectionMode() : toSingleSectionMode()) {

			repopulate();

			editManager.registerEdit();

			return true;
		}

		return false;
	}

	private void populate() {

		add(createMainPanel(), BorderLayout.CENTER);
	}

	private void repopulate() {

		removeAll();
		populate();
		revalidate();
	}

	private JComponent createMainPanel() {

		if (modelConfig.singleSectionMode()) {

			return createSectionComponent(getSingleSection());
		}

		return createMultiSectionComponent();
	}

	private JComponent createMultiSectionComponent() {

		return new MultiSectionPanel().createFullEditComponent(SECTIONS_TITLE);
	}

	private JComponent createSectionComponent(ModelSectionConfig section) {

		return createSectionPanel(section).createFullEditComponent(HIERARCHIES_TITLE);
	}

	private ModelSectionConfigPanel createSectionPanel(ModelSectionConfig section) {

		return new ModelSectionConfigPanel(editManager, section);
	}

	private boolean toMultiSectionMode() {

		String label = checkInputInitialSectionLabel();

		if (label != null) {

			getSingleSection().resetLabel(label);

			return true;
		}

		return false;
	}

	private boolean toSingleSectionMode() {

		if (singleSection() || checkConfirmSectionMerging()) {

			modelConfig.toSingleSectionMode();

			return true;
		}

		return false;
	}

	private boolean checkConfirmSectionMerging() {

		return InfoDisplay.checkContinue(
					"All current sections will be merged to form initial section");
	}

	private String checkInputInitialSectionLabel() {

		return new LabelSelector(this, "initial section").getSelectionOrNull();
	}

	private boolean singleSection() {

		return getSections().size() == 1;
	}

	private ModelSectionConfig getSingleSection() {

		return getSections().get(0);
	}

	private List<ModelSectionConfig> getSections() {

		return modelConfig.getSections();
	}
}
