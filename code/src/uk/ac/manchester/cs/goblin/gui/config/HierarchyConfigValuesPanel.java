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

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class HierarchyConfigValuesPanel extends ValuesPanel {

	static private final long serialVersionUID = -1;

	private EditManager editManager;

	private RootConceptId rootConceptId = new RootConceptId();
	private ExtensibilityOption extensibilityOption = new ExtensibilityOption();
	private DynamicAttributesOption dynamicAttributesOption = new DynamicAttributesOption();

	private enum Extensibility {

		EXTENSIBLE,
		FIXED;

		static Extensibility get(boolean fixedStructure) {

			return fixedStructure ? FIXED : EXTENSIBLE;
		}

		boolean fixedStructure() {

			return this == FIXED;
		}
	}

	private class RootConceptId extends ConceptIdValue {

		String getTitle() {

			return "Root concept";
		}

		void set(CoreHierarchyConfig hierarchy) {

			set(hierarchy.getRootConceptId());
		}

		ConfigEntitySelectorDialog createSelectorDialog(ConfigOntology ontology) {

			ConfigEntitySelectorDialog dialog = super.createSelectorDialog(ontology);

			dialog.setExclusionSeedEntityIds(getCurrentHierarchyRootConceptIds());

			return dialog;
		}

		private List<EntityId> getCurrentHierarchyRootConceptIds() {

			List<EntityId> roots = new ArrayList<EntityId>();

			for (CoreHierarchyConfig hierarchy : editManager.getHierarchies()) {

				roots.add(hierarchy.getRootConceptId());
			}

			return roots;
		}
	}

	private class ExtensibilityOption extends EnumValue<Extensibility> {

		String getTitle() {

			return "Hierarchy extensibility";
		}

		void set(CoreHierarchyConfig hierarchy) {

			set(Extensibility.get(hierarchy.fixedStructure()));
		}

		Extensibility[] getOptions() {

			return Extensibility.values();
		}
	}

	private class DynamicAttributesOption extends EnumValue<ConstraintsOption> {

		String getTitle() {

			return "Dynamic attribute constraints option";
		}

		void set(CoreHierarchyConfig hierarchy) {

			set(hierarchy.getDynamicAttributeConstraintsOption());
		}

		ConstraintsOption[] getOptions() {

			return ConstraintsOption.values();
		}
	}

	HierarchyConfigValuesPanel(EditManager editManager) {

		super(editManager);

		this.editManager = editManager;

		initialise();
	}

	HierarchyConfigValuesPanel(EditManager editManager, CoreHierarchyConfig hierarchy) {

		this(editManager);

		rootConceptId.set(hierarchy);
		extensibilityOption.set(hierarchy);
		dynamicAttributesOption.set(hierarchy);

		initialise();
	}

	void createHierarchy(ModelSectionConfig section) {

		CoreHierarchyConfig config = section.addHierarchy(rootConceptId.get());

		setConfigOptions(config);
	}

	void updateHierarchy(CoreHierarchyConfig config) {

		config.resetRootConceptId(rootConceptId.get());

		setConfigOptions(config);
	}

	private void setConfigOptions(CoreHierarchyConfig config) {

		config.setFixedStructure(extensibilityOption.get().fixedStructure());
		config.setDynamicAttributeConstraints(dynamicAttributesOption.get());
	}
}
