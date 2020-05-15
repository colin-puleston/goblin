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

import java.awt.*;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class ConceptSelectorTreePanel extends JPanel {

	static private final long serialVersionUID = -1;

	private Set<Concept> rootConcepts;

	private SelectorTree tree;
	private FilterPanel filterPanel = new FilterPanel();

	private class SelectorTree extends ConceptTree {

		static private final long serialVersionUID = -1;

		SelectorTree() {

			super(false);

			initialise(rootConcepts);
		}

		boolean requiredConcept(Concept concept) {

			return filterPanel.requiredInTree(concept);
		}

		GCellDisplay getConceptDisplay(Concept concept) {

			return getSelectorCellDisplay(concept, filterPanel.passesFilter(concept));
		}

		void onConstraintChange() {

			throw new Error("Unexpected method invocation!");
		}
	}

	private class FilterPanel extends GTreeFilterPanel<Concept> {

		static private final long serialVersionUID = -1;

		protected void reinitialiseTree() {

			tree.reinitialise();
		}

		protected Set<Concept> getRootNodes() {

			return rootConcepts;
		}

		protected Collection<Concept> getChildNodes(Concept parent) {

			return parent.getChildren();
		}

		protected String getNodeLabel(Concept node) {

			return node.getConceptId().getLabel();
		}
	}

	private class ConceptSelectionListener extends GSelectionListener<GNode> {

		protected void onSelected(GNode node) {

			Concept selected = tree.getSelectedConcept();

			if (selected != null) {

				onSelection(selected);
			}
		}

		protected void onDeselected(GNode node) {
		}

		ConceptSelectionListener() {

			tree.addNodeSelectionListener(this);
		}
	}

	ConceptSelectorTreePanel(Concept rootConcept) {

		this(Collections.singleton(rootConcept));
	}

	ConceptSelectorTreePanel(Set<Concept> rootConcepts) {

		super(new BorderLayout());

		this.rootConcepts = rootConcepts;

		tree = new SelectorTree();

		add(new JScrollPane(tree), BorderLayout.CENTER);
		add(filterPanel, BorderLayout.SOUTH);

		new ConceptSelectionListener();
	}

	abstract GCellDisplay getSelectorCellDisplay(Concept concept, boolean highlight);

	abstract void onSelection(Concept selected);
}
