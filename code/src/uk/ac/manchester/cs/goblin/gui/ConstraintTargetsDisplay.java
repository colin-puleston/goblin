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

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConstraintTargetsDisplay {

	private Constraint validValues;
	private Set<Concept> impliedTargets = new HashSet<Concept>();

	ConstraintTargetsDisplay(Constraint validValues, Set<Constraint> impliedValues) {

		this.validValues = validValues;

		for (Constraint impliedValue : impliedValues) {

			impliedTargets.add(impliedValue.getTargetValue());
		}
	}

	GoblinCellDisplay getCellDisplay(Concept concept) {

		if (!validTarget(concept)) {

			return GoblinCellDisplay.CONSTRAINTS_POTENTIAL_TARGET;
		}

		if (impliedTargets.contains(concept)) {

			return GoblinCellDisplay.CONSTRAINTS_IMPLIED_TARGET;
		}

		return GoblinCellDisplay.CONSTRAINTS_VALID_TARGET;
	}

	private boolean validTarget(Concept concept) {

		return concept.subsumedByAny(validValues.getTargetValues());
	}
}
