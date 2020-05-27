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
import javax.swing.border.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class ConceptTreeSelectorPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String SEARCH_BUTTON_LABEL = "Search...";
	static private final String SEARCH_DIALOG_TITLE = "Select Concept";
	static private final String SEARCH_LIST_TITLE = "List";
	static private final String SEARCH_TREE_TITLE = "Tree";

	static private final Dimension SEARCH_DIALOG_SIZE = new Dimension(400, 400);

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

	private class SearchDialog extends GDialog {

		static private final long serialVersionUID = -1;

		public Dimension getPreferredSize() {

			return SEARCH_DIALOG_SIZE;
		}

		SearchDialog() {

			super(ConceptTreeSelectorPanel.this, SEARCH_DIALOG_TITLE, true);

			display(createTabs());
		}

		void onSelected(Concept concept) {

			targetTree.selectConcept(concept);

			dispose();
		}

		private JTabbedPane createTabs() {

			JTabbedPane tabs = new JTabbedPane();

			tabs.addTab(SEARCH_LIST_TITLE, new GListPanel<Concept>(new SearchList(this)));
			tabs.addTab(SEARCH_TREE_TITLE, new TreeSearchPanel(this));

			return tabs;
		}
	}

	private class SearchList extends GList<Concept> {

		static private final long serialVersionUID = -1;

		private SearchDialog dialog;

		private class TargetTreeConceptSelector extends GSelectionListener<Concept> {

			protected void onSelected(Concept concept) {

				dialog.onSelected(concept);
			}

			protected void onDeselected(Concept concept) {
			}

			TargetTreeConceptSelector() {

				addSelectionListener(this);
			}
		}

		SearchList(SearchDialog dialog) {

			super(false, true);

			this.dialog = dialog;

			populate(targetTree.getRootConcepts());

			new TargetTreeConceptSelector();
		}

		private void populate(Set<Concept> concepts) {

			for (Concept concept : concepts) {

				addEntity(concept, getSelectorsCellDisplay(concept, false));
				populate(concept.getChildren());
			}
		}
	}

	private class TreeSearchPanel extends ConceptSelectorTreePanel {

		static private final long serialVersionUID = -1;

		private SearchDialog dialog;

		TreeSearchPanel(SearchDialog dialog) {

			super(targetTree.getRootConcepts());

			this.dialog = dialog;
		}

		GCellDisplay getSelectorCellDisplay(Concept concept, boolean highlight) {

			return getSelectorsCellDisplay(concept, highlight);
		}

		void onSelection(Concept selected) {

			dialog.onSelected(selected);
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
