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
import javax.swing.tree.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class ConceptTree extends GSelectorTree {

	static private final long serialVersionUID = -1;

	static List<Concept> extractConcepts(Collection<GNode> nodes) {

		List<Concept> concepts = new ArrayList<Concept>();

		for (GNode node : nodes) {

			Concept concept = extractConcept(node);

			if (concept != null) {

				concepts.add(concept);
			}
		}

		return concepts;
	}

	static Concept extractConcept(GNode node) {

		return extractConcept((ConceptTreeNode)node);
	}

	static private Concept extractConcept(ConceptTreeNode node) {

		return node != null ? node.getConceptOrNull() : null;
	}

	private Set<Concept> rootConcepts;

	abstract class ConceptTreeNode extends GNode {

		protected boolean orderedChildren() {

			return true;
		}

		ConceptTreeNode() {

			super(ConceptTree.this);
		}

		void addChildrenFor(Set<Concept> concepts) {

			for (Concept concept : concepts) {

				if (requiredConcept(concept)) {

					addChildFor(concept);
				}
			}
		}

		void addChildFor(Concept concept) {

			ConceptNode child = createConceptNode(concept);

			addChild(child);
			child.ensureChildren();
		}

		Concept getConceptOrNull() {

			return null;
		}

		ConceptNode findDescendantNode(Concept forConcept) {

			for (GNode child : getChildren()) {

				if (child instanceof ConceptNode) {

					ConceptNode found = ((ConceptNode)child).findNode(forConcept);

					if (found != null) {

						return found;
					}
				}
			}

			return null;
		}
	}

	class ConceptNode extends ConceptTreeNode {

		final Concept concept;

		protected void addInitialChildren() {

			addChildrenFor(concept.getChildren());
		}

		protected boolean autoExpand() {

			return rootConcepts.contains(concept);
		}

		protected GCellDisplay getDisplay() {

			return getConceptDisplay(concept);
		}

		ConceptNode(Concept concept) {

			this.concept = concept;
		}

		Concept getConceptOrNull() {

			return concept;
		}

		ConceptNode findNode(Concept forConcept) {

			return concept.equals(forConcept) ? this : findDescendantNode(forConcept);
		}
	}

	private class RootNode extends ConceptTreeNode {

		protected void addInitialChildren() {

			addChildrenFor(rootConcepts);
		}

		protected GCellDisplay getDisplay() {

			return GCellDisplay.NO_DISPLAY;
		}
	}

	private class MultiSelectionPruner extends GSelectionListener<GNode> {

		protected void onSelected(GNode node) {

			Concept concept = extractConcept(node);

			if (concept != null) {

				pruneSelections(concept);
			}
		}

		protected void onDeselected(GNode node) {
		}

		MultiSelectionPruner() {

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

	ConceptTree(boolean multiSelect) {

		super(multiSelect);

		setRootVisible(false);
		setShowsRootHandles(true);

		if (multiSelect) {

			new MultiSelectionPruner();
		}
	}

	void initialise(Concept rootConcept) {

		initialise(Collections.singleton(rootConcept));
	}

	void initialise(Set<Concept> rootConcepts) {

		this.rootConcepts = rootConcepts;

		initialise(new RootNode());
	}

	Set<Concept> getRootConcepts() {

		return rootConcepts;
	}

	Concept getSelectedConcept() {

		return extractConcept(getSelectedNode());
	}

	List<Concept> getAllSelectedConcepts() {

		List<Concept> selectedConcepts = new ArrayList<Concept>();

		for (GNode node : getSelectedNodes()) {

			selectedConcepts.add(extractConcept(node));
		}

		return selectedConcepts;
	}

	void selectConcept(Concept concept) {

		ConceptTreeNode node = lookForNodeFor(concept);

		if (node != null) {

			node.select();
		}
	}

	void selectConcepts(Collection<Concept> concepts) {

		List<ConceptTreeNode> nodes = new ArrayList<ConceptTreeNode>();

		for (Concept concept : concepts) {

			ConceptTreeNode node = lookForNodeFor(concept);

			if (node != null) {

				nodes.add(node);
			}
		}

		selectAll(nodes);
	}

	boolean requiredConcept(Concept concept) {

		return true;
	}

	void onAddedConceptListener(Concept concept, ConceptListener listener) {
	}

	ConceptNode createConceptNode(Concept concept) {

		return new ConceptNode(concept);
	}

	ConceptTreeNode findParentNodeFor(Concept concept) {

		ConceptTreeNode root = getConceptTreeRootNode();

		return concept.isRoot() ? root : root.findDescendantNode(concept.getParent());
	}

	abstract GCellDisplay getConceptDisplay(Concept concept);

	private ConceptTreeNode lookForNodeFor(Concept concept) {

		return getConceptTreeRootNode().findDescendantNode(concept);
	}

	private ConceptTreeNode getConceptTreeRootNode() {

		return (ConceptTreeNode)getRootNode();
	}
}
