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

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class ConstraintTargetsTree extends ConceptTree {

	static private final long serialVersionUID = -1;

	private class SelectionsPruner extends GSelectionListener<GNode> {

		protected void onSelected(GNode node) {

			pruneSelections(extractConcept(node));
		}

		protected void onDeselected(GNode node) {
		}

		SelectionsPruner() {

			addNodeSelectionListener(this);
		}

		private void pruneSelections(Concept latest) {

			List<Concept> prePruning = getAllSelectedConcepts();
			List<Concept> postPruning = new ArrayList<Concept>(prePruning);

			for (Concept selection : prePruning) {

				if (latest != selection && conflict(latest, selection)) {

					postPruning.remove(selection);
				}
			}

			if (postPruning.size() != prePruning.size()) {

				selectConcepts(postPruning);
			}
		}

		private boolean conflict(Concept concept1, Concept concept2) {

			return concept1.descendantOf(concept2) || concept2.descendantOf(concept1);
		}
	}

	ConstraintTargetsTree(boolean multiSelect) {

		super(multiSelect);

		new SelectionsPruner();
	}

	void initialise(Constraint constraint) {

		initialise(constraint.getTargetValues());
	}

	void onConstraintChange() {
	}
}