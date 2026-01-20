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
class ModelSectionPanel extends ConceptTreesPanel<Hierarchy> {

	static private final long serialVersionUID = -1;

	private ModelSection section;

	ModelSectionPanel(ModelSection section) {

		super(JTabbedPane.LEFT);

		this.section = section;

		populate();
	}

	String getTitle() {

		return section.getLabel();
	}

	List<Hierarchy> getSources() {

		return section.getAllHierarchies();
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

		int hierarchyIdx = checkMakeHierarchyVisible(location);

		if (hierarchyIdx == -1) {

			return false;
		}

		if (location.constraintEdit()) {

			Constraint constraint = location.getEditedConstraint();

			getCoreHierarchyPanel(hierarchyIdx).makeConstraintVisible(constraint);
		}

		return true;
	}

	private int checkMakeHierarchyVisible(EditLocation location) {

		return checkMakeSourceVisible(location.getEditedConcept().getHierarchy().getRootConcept());
	}

	private CoreHierarchyPanel getCoreHierarchyPanel(int hierarchyIdx) {

		return (CoreHierarchyPanel)getComponentAt(hierarchyIdx);
	}

	private String getTitleSuffix(Hierarchy hierarchy) {

		String suffix = "";

		if (hierarchy.hasInwardCoreAttributes()) {

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
