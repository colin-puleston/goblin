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

import java.util.*;

import javax.swing.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ModelSectionPanel extends MultiTabPanel<Hierarchy> {

	static private final long serialVersionUID = -1;

	private ModelSection section;

	private class LocationDisplay {

		private Hierarchy coreHierarchy;
		private Concept coreConcept;
		private Attribute coreConceptAttribute;

		LocationDisplay(EditLocation location) {

			coreHierarchy = location.getEditedHierarchy();
			coreConcept = location.getEditedConceptOrNull();
			coreConceptAttribute = location.getEditedAttributeOrNull();
		}

		LocationDisplay(Hierarchy coreHierarchy, DynamicAttribute dynamicAttribute) {

			this.coreHierarchy = coreHierarchy;

			coreConcept = dynamicAttribute.getRootSourceConcept();
			coreConceptAttribute = dynamicAttribute;
		}

		void displayLocation() {

			makeSourceVisible(coreHierarchy);

			CoreHierarchyPanel hierarchyPanel = getCoreHierarchyPanel();

			if (coreConcept != null) {

				hierarchyPanel.selectConcept(coreConcept);

				if (coreConceptAttribute != null) {

					hierarchyPanel.selectAttribute(coreConceptAttribute);
				}
			}
		}

		private CoreHierarchyPanel getCoreHierarchyPanel() {

			return (CoreHierarchyPanel)getSourceComponent(coreHierarchy);
		}
	}

	ModelSectionPanel(ModelSection section) {

		super(JTabbedPane.LEFT);

		this.section = section;

		populate();
	}

	String getTitle() {

		return section.getLabel();
	}

	List<Hierarchy> getSources() {

		return section.getCoreHierarchies();
	}

	String getTitle(Hierarchy hierarchy) {

		String title = hierarchy.getLabel();
		String suffix = getTitleSuffix(hierarchy);

		if (!suffix.isEmpty()) {

			title += (" " + suffix);
		}

		return title;
	}

	Concept getRootConcept(Hierarchy hierarchy) {

		return hierarchy.getRootConcept();
	}

	JComponent createComponent(Hierarchy hierarchy) {

		return new CoreHierarchyPanel(hierarchy);
	}

	boolean checkMakeEditVisible(EditLocation location) {

		LocationDisplay locationDisplay = toLocationDisplayOrNull(location);

		if (locationDisplay != null) {

			locationDisplay.displayLocation();

			return true;
		}

		return false;
	}

	private LocationDisplay toLocationDisplayOrNull(EditLocation location) {

		Hierarchy edHierarchy = location.getEditedHierarchy();

		for (Hierarchy coreHierarchy : section.getCoreHierarchies()) {

			if (coreHierarchy.equals(edHierarchy)) {

				return new LocationDisplay(location);
			}

			for (DynamicAttribute attribute : coreHierarchy.getDynamicAttributes()) {

				Hierarchy valuesHierarchy = attribute.getRootTargetConcept().getHierarchy();

				if (valuesHierarchy.equals(edHierarchy)) {

					return new LocationDisplay(coreHierarchy, attribute);
				}
			}
		}

		return null;
	}

	private String getTitleSuffix(Hierarchy hierarchy) {

		String suffix = "";

		if (hierarchy.potentiallyHasInwardAttributes()) {

			suffix += "<=";

			if (hierarchy.potentiallyHasAttributes()) {

				suffix += ">";
			}
		}
		else {

			if (hierarchy.potentiallyHasAttributes()) {

				suffix += "=>";
			}
		}

		return suffix;
	}
}
