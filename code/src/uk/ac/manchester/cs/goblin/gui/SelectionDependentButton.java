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

/**
 * @author Colin Puleston
 */
abstract class SelectionDependentButton<S, A> extends GButton {

	static private final long serialVersionUID = -1;

	private class Enabler extends GSelectionListener<S> {

		protected void onSelected(S entity) {

			updateEnabling();
		}

		protected void onDeselected(S entity) {

			updateEnabling();
		}
	}

	SelectionDependentButton(String label) {

		super(label);

		setEnabled(enableOnNoActiveSelections());
	}

	GSelectionListener<S> initialise() {

		return new Enabler();
	}

	abstract List<A> getActiveSelections();

	void updateEnabling() {

		setEnabled(enableOnCurrentActiveSelections());
	}

	boolean enableOnActiveSelections(List<A> selections) {

		return true;
	}

	boolean enableOnNoActiveSelections() {

		return false;
	}

	private boolean enableOnCurrentActiveSelections() {

		List<A> selections = getActiveSelections();

		return selections.isEmpty()
					? enableOnNoActiveSelections()
					: enableOnActiveSelections(selections);
	}
}
