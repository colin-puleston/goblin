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

import javax.swing.tree.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class DynamicConceptTree extends ConceptTree {

	static private final long serialVersionUID = -1;

	class DynamicConceptNode extends ConceptNode {

		private class ModelUpdateTracker implements ConceptListener {

			public void onIdReset(Concept concept) {

				((DefaultTreeModel)getModel()).nodeChanged(DynamicConceptNode.this);
			}

			public void onChildAdded(Concept child, boolean replacement) {

				addChildFor(child);

				expand();
			}

			public void onConstraintAdded(Constraint constraint, boolean inward) {

				onConstraintsUpdated();
			}

			public void onConstraintRemoved(Constraint constraint, boolean inward) {

				onConstraintsUpdated();
			}

			public void onConceptRemoved(Concept concept, boolean replacing) {

				remove();
			}

			ModelUpdateTracker() {

				concept.addListener(this);

				onAddedConceptListener(concept, this);
			}
		}

		DynamicConceptNode(Concept concept) {

			super(concept);

			new ModelUpdateTracker();
		}

		void onConstraintsUpdated() {
		}
	}

	DynamicConceptTree(boolean multiSelect) {

		super(multiSelect);
	}

	ConceptNode createConceptNode(Concept concept) {

		return new DynamicConceptNode(concept);
	}

	void onAddedConceptListener(Concept concept, ConceptListener listener) {
	}
}
