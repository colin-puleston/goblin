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
abstract class ConceptTreesPanel<S> extends JTabbedPane {

	static private final long serialVersionUID = -1;

	ConceptTreesPanel(int tabPlacement) {

		super(tabPlacement);
	}

	void populate() {

		int i = 0;

		for (S source : getSources()) {

			addTab(getTitle(source), createComponent(source));
		}
	}

	void repopulate() {

		removeAll();
		populate();
	}

	void makeSourceVisible(S source) {

		setSelectedIndex(getSources().indexOf(source));
	}

	int makeSourceVisible(Concept rootConcept) {

		int i = 0;

		for (S source : getSources()) {

			if (getRootConcept(source).equals(rootConcept)) {

				setSelectedIndex(i);

				break;
			}

			i++;
		}

		return i;
	}

	abstract List<S> getSources();

	abstract String getTitle(S treeSource);

	abstract Concept getRootConcept(S treeSource);

	abstract JComponent createComponent(S treeSource);
}
