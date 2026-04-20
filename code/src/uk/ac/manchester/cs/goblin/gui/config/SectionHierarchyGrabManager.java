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

import uk.ac.manchester.cs.goblin.config.*;

/**
 * @author Colin Puleston
 */
class SectionHierarchyGrabManager {

	private ModelConfig modelConfig;
	private ModelSectionConfig grabberSection;

	private GrabEnabler grabEnabler = new GrabEnabler();

	private class SectionHierarchySelector extends HierarchySelector {

		static private final long serialVersionUID = -1;

		SectionHierarchySelector() {

			super(getGrabbableHierarchies());
		}

		String getOptionDisplayLabel(CoreHierarchyConfig option) {

			return getHierarchySection(option).getLabel() + " / " + option.getLabel();
		}
	}

	private abstract class GrabbableHierarchiesProcessor {

		boolean processAll() {

			for (ModelSectionConfig section : modelConfig.getSections()) {

				if (section != grabberSection && process(section)) {

					return true;
				}
			}

			return false;
		}

		abstract boolean process(ModelSectionConfig section);
	}

	private class GrabEnabler extends GrabbableHierarchiesProcessor {

		boolean process(ModelSectionConfig section) {

			return section.hasHierarchies();
		}
	}

	private class GrabbableHierarchiesFinder extends GrabbableHierarchiesProcessor {

		final List<CoreHierarchyConfig> grabbables = new ArrayList<CoreHierarchyConfig>();

		GrabbableHierarchiesFinder() {

			processAll();
		}

		boolean process(ModelSectionConfig section) {

			grabbables.addAll(section.getHierarchies());

			return false;
		}
	}

	private class GrabbableHierarchySectionFinder extends GrabbableHierarchiesProcessor {

		private ModelSectionConfig section = null;
		private CoreHierarchyConfig hierarchy;

		GrabbableHierarchySectionFinder(CoreHierarchyConfig hierarchy) {

			this.hierarchy = hierarchy;
		}

		ModelSectionConfig findSection() {

			processAll();

			if (section != null) {

				return section;
			}

			throw new Error("Cannot find section for hierarchy: " + hierarchy.getRootConceptId());
		}

		boolean process(ModelSectionConfig section) {

			if (section.hasHierarchy(hierarchy)) {

				this.section = section;

				return true;
			}

			return false;
		}
	}

	SectionHierarchyGrabManager(ModelConfig modelConfig, ModelSectionConfig grabberSection) {

		this.modelConfig = modelConfig;
		this.grabberSection = grabberSection;
	}

	boolean enableGrab() {

		return grabEnabler.processAll();
	}

	boolean performGrab() {

		CoreHierarchyConfig hierarchy = getHierarchySelection();

		if (hierarchy != null) {

			grabberSection.grabHierarchy(getHierarchySection(hierarchy), hierarchy);

			return true;
		}

		return false;
	}

	private CoreHierarchyConfig getHierarchySelection() {

		return new SectionHierarchySelector().getSelectionOrNull();
	}

	private List<CoreHierarchyConfig> getGrabbableHierarchies() {

		return new GrabbableHierarchiesFinder().grabbables;
	}

	private ModelSectionConfig getHierarchySection(CoreHierarchyConfig hierarchy) {

		return new GrabbableHierarchySectionFinder(hierarchy).findSection();
	}
}
