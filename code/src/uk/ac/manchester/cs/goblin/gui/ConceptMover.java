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

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConceptMover {

	private Concept rootMoveConcept = null;

	void startMove(Concept rootMoveConcept) {

		this.rootMoveConcept = rootMoveConcept;
	}

	void completeMove(Concept newParent) {

		rootMoveConcept.move(newParent);

		rootMoveConcept = null;
	}

	void abortMove() {

		rootMoveConcept = null;
	}

	boolean moveInProgress() {

		return rootMoveConcept != null;
	}

	boolean newParentCandidate(Concept concept) {

		return moveInProgress() && !rootMoveParent(concept) && !movingConcept(concept);
	}

	boolean movingConcept(Concept concept) {

		return moveInProgress() && concept.subsumedBy(rootMoveConcept);
	}

	private boolean rootMoveParent(Concept concept) {

		return concept.equals(rootMoveConcept.getParent());
	}
}
