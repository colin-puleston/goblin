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
	private ConstraintsListener constraintsListener = ConstraintsListener.INERT_LISTENER;

	private abstract class ConceptTreeNode extends GNode {

		protected boolean orderedChildren() {

			return true;
		}

		ConceptTreeNode() {

			super(ConceptTree.this);
		}

		void redisplayAllConstraintsOnDescendants(boolean modeChanged, boolean wasCollapsed) {

			for (GNode child : getChildren()) {

				((ConceptTreeNode)child).redisplayAllConstraints(modeChanged, wasCollapsed);
			}
		}

		abstract void redisplayAllConstraints(boolean modeChanged, boolean parentWasCollapsed);

		void addChildrenFor(Set<Concept> concepts) {

			for (Concept concept : concepts) {

				if (requiredConcept(concept)) {

					addChildFor(concept);
				}
			}
		}

		void addChildFor(Concept concept) {

			ConceptNode child = new ConceptNode(concept);

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

	private class RootNode extends ConceptTreeNode {

		protected void addInitialChildren() {

			addChildrenFor(rootConcepts);
		}

		protected GCellDisplay getDisplay() {

			return GCellDisplay.NO_DISPLAY;
		}

		void redisplayAllConstraints(boolean modeChanged) {

			redisplayAllConstraintsOnDescendants(modeChanged, false);
		}

		void redisplayAllConstraints(boolean modeChanged, boolean parentWasCollapsed) {

			throw new Error("Method should never be invoked!");
		}
	}

	private class ConceptNode extends ConceptTreeNode {

		private Concept concept;
		private Set<ConstraintGroup> displayedConstraints = new HashSet<ConstraintGroup>();

		private class ModelUpdateTracker implements ConceptListener {

			public void onChildAdded(Concept child, boolean replacement) {

				ConceptTreeNode parentNode = findParentNodeFor(child);

				if (parentNode != null) {

					parentNode.addChildFor(child);
					parentNode.expand();
				}
			}

			public void onConstraintAdded(Constraint constraint, boolean inward) {

				constraintsListener.onConstraintChange();
			}

			public void onConstraintRemoved(Constraint constraint, boolean inward) {

				constraintsListener.onConstraintChange();
			}

			public void onConceptRemoved(Concept concept, boolean replacing) {

				remove();
			}

			ModelUpdateTracker() {

				concept.addListener(this);
				onAddedConceptListener(concept, this);
			}
		}

		private class ConstraintRedisplayer {

			private boolean modeChanged;
			private boolean parentWasCollapsed;
			private boolean wasCollapsed = collapsed();

			private Set<ConstraintGroup> oldDisplayedConstraints = displayedConstraints;

			ConstraintRedisplayer(boolean modeChanged, boolean parentWasCollapsed) {

				this.modeChanged = modeChanged;
				this.parentWasCollapsed = parentWasCollapsed;

				displayedConstraints = new HashSet<ConstraintGroup>();

				redisplayAllConstraintsOnDescendants(modeChanged, wasCollapsed);
				addConstraintChildren();

				if (requireRecollapse()) {

					collapse();
				}
			}

			private boolean requireRecollapse() {

				if (parentWasCollapsed) {

					return true;
				}

				if (!wasCollapsed) {

					return false;
				}

				if (modeChanged) {

					return displayedConstraints.isEmpty();
				}

				return displayedConstraints.equals(oldDisplayedConstraints);
			}
		}

		protected void addInitialChildren() {

			addChildrenFor(concept.getChildren());
			addConstraintChildren();
		}

		protected int compareChildrenPriorToLabelCompare(GNode first, GNode second) {

			boolean firstIsConcept = first instanceof ConceptNode;
			boolean secondIsConcept = second instanceof ConceptNode;

			if (firstIsConcept == secondIsConcept) {

				return 0;
			}

			return firstIsConcept ? -1 : 1;
		}

		protected boolean autoExpand() {

			return rootConcepts.contains(concept);
		}

		protected GCellDisplay getDisplay() {

			return getConceptDisplay(concept);
		}

		ConceptNode(Concept concept) {

			this.concept = concept;

			new ModelUpdateTracker();
		}

		void redisplayAllConstraints(boolean modeChanged, boolean parentWasCollapsed) {

			new ConstraintRedisplayer(modeChanged, parentWasCollapsed);
		}

		Concept getConceptOrNull() {

			return concept;
		}

		ConceptNode findNode(Concept forConcept) {

			return concept.equals(forConcept) ? this : findDescendantNode(forConcept);
		}

		private void addConstraintChildren() {

			Hierarchy hierarchy = concept.getHierarchy();

			if (showAnyOutwardConstraints()) {

				addRelevantOutwardConstraintChildren();
			}

			if (showInwardConstraints()) {

				addAllInwardConstraintChildren();
			}
		}

		private void addRelevantOutwardConstraintChildren() {

			for (ConstraintType type : concept.getHierarchy().getConstraintTypes()) {

				if (showTypeOutwardConstraints(type)) {

					checkAddConstraintsChild(new OutwardConstraintGroup(concept, type));
				}
			}
		}

		private void addAllInwardConstraintChildren() {

			for (ConstraintType type : concept.getHierarchy().getInwardConstraintTypes()) {

				checkAddConstraintsChild(new InwardConstraintGroup(concept, type));
			}
		}

		private void checkAddConstraintsChild(ConstraintGroup group) {

			if (group.anyConstraints()) {

				addChild(new ConstraintGroupNode(this, group));
				displayedConstraints.add(group);
			}
		}
	}

	private abstract class ConstraintsNode extends ConceptTreeNode {

		private class Deselector extends GSelectionListener<GNode> {

			private ConceptNode sourceConceptNode;

			protected void onSelected(GNode node) {

				if (node == ConstraintsNode.this) {

					sourceConceptNode.select();
				}
			}

			protected void onDeselected(GNode node) {
			}

			Deselector(ConceptNode sourceConceptNode) {

				this.sourceConceptNode = sourceConceptNode;

				addNodeSelectionListener(this);
			}
		}

		ConstraintsNode(ConceptNode sourceConceptNode) {

			new Deselector(sourceConceptNode);
		}
	}

	private class ConstraintGroupNode extends ConstraintsNode {

		private ConceptNode sourceConceptNode;
		private ConstraintGroup group;

		protected void addInitialChildren() {

			for (Concept linked : group.getImpliedValueLinkedConcepts()) {

				addChild(new ImpliedValueConstraintLinkedNode(sourceConceptNode, linked));
			}
		}

		protected GCellDisplay getDisplay() {

			return GoblinCellDisplay.CONCEPTS_CONSTRAINT_GROUP.forConstraints(group);
		}

		ConstraintGroupNode(ConceptNode sourceConceptNode, ConstraintGroup group) {

			super(sourceConceptNode);

			this.sourceConceptNode = sourceConceptNode;
			this.group = group;
		}

		void redisplayAllConstraints(boolean modeChanged, boolean parentWasCollapsed) {

			remove();
		}
	}

	private class ImpliedValueConstraintLinkedNode extends ConstraintsNode {

		private GCellDisplay display;

		protected GCellDisplay getDisplay() {

			return display;
		}

		ImpliedValueConstraintLinkedNode(ConceptNode sourceConceptNode, Concept linked) {

			super(sourceConceptNode);

			display = GoblinCellDisplay.CONCEPTS_CONSTRAINT_IMPLIED_TARGET.forConcept(linked);
		}

		void redisplayAllConstraints(boolean modeChanged, boolean parentWasCollapsed) {
		}
	}

	private class MultiSelectionPruner extends GSelectionListener<GNode> {

		protected void onSelected(GNode node) {

			pruneSelections(extractConcept(node));
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

	void setConstraintsListener(ConstraintsListener listener) {

		constraintsListener = listener;
	}

	void initialise(Concept rootConcept) {

		initialise(Collections.singleton(rootConcept));
	}

	void initialise(Set<Concept> rootConcepts) {

		this.rootConcepts = rootConcepts;

		initialise(new RootNode());
	}

	void redisplayForConstraintsDisplayModeChange() {

		getConceptTreeRootNode().redisplayAllConstraints(true);
	}

	void redisplayForConstraintsEdit() {

		getConceptTreeRootNode().redisplayAllConstraints(false);

		reselectSelected();
	}

	boolean showAnyOutwardConstraints() {

		return false;
	}

	boolean showTypeOutwardConstraints(ConstraintType type) {

		return false;
	}

	boolean showInwardConstraints() {

		return false;
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

	abstract GCellDisplay getConceptDisplay(Concept concept);

	private ConceptTreeNode lookForNodeFor(Concept concept) {

		return getConceptTreeRootNode().findDescendantNode(concept);
	}

	private ConceptTreeNode findParentNodeFor(Concept concept) {

		ConceptTreeNode root = getConceptTreeRootNode();

		return concept.isRoot() ? root : root.findDescendantNode(concept.getParent());
	}

	private RootNode getConceptTreeRootNode() {

		return (RootNode)getRootNode();
	}
}
