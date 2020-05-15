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

	static private final String DIALOG_TITLE = "Select Concept";

	static private final String LIST_SEARCH_BUTTON_LABEL = "List?";
	static private final String TREE_SEARCH_BUTTON_LABEL = "Tree?";

	static private final Dimension LIST_SEARCH_DIALOG_SIZE = new Dimension(300, 400);
	static private final Dimension TREE_SEARCH_DIALOG_SIZE = new Dimension(400, 400);

	private ConceptTree targetTree;

	private class ListSearchButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			new ListSearchDialog();
		}

		ListSearchButton() {

			super(LIST_SEARCH_BUTTON_LABEL);
		}
	}

	private class TreeSearchButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			new TreeSearchDialog();
		}

		TreeSearchButton() {

			super(TREE_SEARCH_BUTTON_LABEL);
		}
	}

	private class SearchDialog extends GDialog {

		static private final long serialVersionUID = -1;

		SearchDialog() {

			super(ConceptTreeSelectorPanel.this, DIALOG_TITLE, true);
		}

		void selectInTargetTree(Concept concept) {

			targetTree.selectConcept(concept);

			dispose();
		}
	}

	private class ListSearchDialog extends SearchDialog {

		static private final long serialVersionUID = -1;

		private class ConceptList extends GList<Concept> {

			static private final long serialVersionUID = -1;

			private class TargetTreeConceptSelector extends GSelectionListener<Concept> {

				protected void onSelected(Concept concept) {

					selectInTargetTree(concept);
				}

				protected void onDeselected(Concept concept) {
				}

				TargetTreeConceptSelector() {

					addSelectionListener(this);
				}
			}

			ConceptList() {

				super(false, true);

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

		public Dimension getPreferredSize() {

			return LIST_SEARCH_DIALOG_SIZE;
		}

		ListSearchDialog() {

			display(new GListPanel<Concept>(new ConceptList()));
		}
	}

	private class TreeSearchDialog extends SearchDialog {

		static private final long serialVersionUID = -1;

		private class SearchTreePanel extends ConceptSelectorTreePanel {

			static private final long serialVersionUID = -1;

			SearchTreePanel() {

				super(targetTree.getRootConcepts());
			}

			GCellDisplay getSelectorCellDisplay(Concept concept, boolean highlight) {

				return getSelectorsCellDisplay(concept, highlight);
			}

			void onSelection(Concept selected) {

				selectInTargetTree(selected);
			}
		}

		public Dimension getPreferredSize() {

			return TREE_SEARCH_DIALOG_SIZE;
		}

		TreeSearchDialog() {

			display(new SearchTreePanel());
		}
	}

	ConceptTreeSelectorPanel(ConceptTree targetTree) {

		super(new BorderLayout());

		this.targetTree = targetTree;

		setBorder(LineBorder.createGrayLineBorder());
		add(createButtonsPanel(), BorderLayout.EAST);
	}

	abstract GCellDisplay getSelectorsCellDisplay(Concept concept, boolean highlight);

	private JPanel createButtonsPanel() {

		return ControlsPanel.horizontal(new ListSearchButton(), new TreeSearchButton());
	}
}
