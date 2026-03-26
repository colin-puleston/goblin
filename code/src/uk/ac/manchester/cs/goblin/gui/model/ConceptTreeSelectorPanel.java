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

package uk.ac.manchester.cs.goblin.gui.model;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
abstract class ConceptTreeSelectorPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String SEARCH_BUTTON_LABEL = "Search...";
	static private final String SEARCH_DIALOG_TITLE = "Select concept";

	private ConceptTree targetTree;

	private class SearchButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			new SearchDialog();
		}

		SearchButton() {

			super(SEARCH_BUTTON_LABEL);
		}
	}

	private class SearchDialog extends TreeNodeSelectorDialog<Concept> {

		static private final long serialVersionUID = -1;

		private SearchTree tree;

		private class SearchTree extends ConceptTree {

			static private final long serialVersionUID = -1;

			SearchTree() {

				super(false);

				initialise(targetTree.getRootConcepts());
			}

			boolean requiredConcept(Concept concept) {

				return passesTreeFiltering(concept);
			}

			GCellDisplay getConceptDisplay(Concept concept) {

				return getTreeCellDisplay(concept);
			}
		}

		protected Collection<Concept> getRootNodes() {

			return targetTree.getRootConcepts();
		}

		protected Collection<Concept> getChildNodes(Concept parent) {

			return parent.getChildren();
		}

		protected String getNodeLabel(Concept concept) {

			return concept.getConceptId().getLabel();
		}

		protected Concept toSubjectNode(GNode guiNode) {

			return tree.getSelectedConcept();
		}

		protected GCellDisplay getCellDisplay(Concept concept, boolean highlight) {

			return getSelectorsCellDisplay(concept, highlight);
		}

		protected void onSelected(Concept concept) {

			targetTree.selectConcept(concept);
		}

		SearchDialog() {

			super(ConceptTreeSelectorPanel.this, SEARCH_DIALOG_TITLE);

			tree = new SearchTree();

			initialise(tree);
		}
	}

	ConceptTreeSelectorPanel(ConceptTree targetTree) {

		super(new BorderLayout());

		this.targetTree = targetTree;

		setBorder(LineBorder.createGrayLineBorder());
		add(new SearchButton(), BorderLayout.EAST);
	}

	abstract GCellDisplay getSelectorsCellDisplay(Concept concept, boolean highlight);
}
