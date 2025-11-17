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

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchyTree extends ConceptTree {

	static private final long serialVersionUID = -1;

	private ConceptMover conceptMover;

	private ConstraintsDisplayMode constraintsDisplayMode = ConstraintsDisplayMode.NONE;
	private ConstraintType constraintTypeSelection = null;

	private class ConstraintEditDrivenUpdater extends ConstraintsListener {

		void onConstraintChange() {

			redisplayForConstraintsEdit();
		}
	}

	HierarchyTree(Hierarchy hierarchy, ConceptMover conceptMover) {

		super(true);

		this.conceptMover = conceptMover;

		initialise(hierarchy.getRootConcept());
		setConstraintsListener(new ConstraintEditDrivenUpdater());
	}

	GCellDisplay getConceptDisplay(Concept concept) {

		return getGoblinCellDisplay(concept).forConcept(concept);
	}

	void setConstraintsDisplayMode(ConstraintsDisplayMode mode) {

		if (mode != constraintsDisplayMode) {

			constraintsDisplayMode = mode;

			redisplayForConstraintsDisplayModeChange();
		}
	}

	void setConstraintTypeSelection(ConstraintType selection) {

		constraintTypeSelection = selection;

		if (constraintsDisplayMode == ConstraintsDisplayMode.CURRENT_OUTWARDS) {

			redisplayForConstraintsDisplayModeChange();
		}
	}

	boolean showAnyOutwardConstraints() {

		return constraintsDisplayMode.anyOutwards();
	}

	boolean showTypeOutwardConstraints(ConstraintType type) {

		switch (constraintsDisplayMode) {

			case ALL_OUTWARDS:
				return true;

			case CURRENT_OUTWARDS:
				return type == constraintTypeSelection;
		}

		throw new Error("Unexpected constraints display-mode: " + constraintsDisplayMode);
	}

	boolean showInwardConstraints() {

		return constraintsDisplayMode == ConstraintsDisplayMode.ALL_INWARDS;
	}

	void update() {

		reselectSelected();
		updateAllNodeDisplays();
	}

	GoblinCellDisplay getGoblinCellDisplay(Concept concept) {

		if (concept.isFixed()) {

			return GoblinCellDisplay.CONCEPTS_FIXED;
		}

		if (conceptMover.movingConcept(concept)) {

			return GoblinCellDisplay.CONCEPTS_MOVE_SUBJECT;
		}

		return GoblinCellDisplay.CONCEPTS_DEFAULT;
	}
}