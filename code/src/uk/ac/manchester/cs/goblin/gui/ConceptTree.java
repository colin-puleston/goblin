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

import uk.ac.manchester.cs.mekon.gui.*;

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

			addChild(new ConceptNode(concept));
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

		private Set<Concept> rootConcepts;

		protected void addInitialChildren() {

			addChildrenFor(rootConcepts);
		}

		protected GCellDisplay getDisplay() {

			return GCellDisplay.NO_DISPLAY;
		}

		RootNode(Set<Concept> rootConcepts) {

			this.rootConcepts = rootConcepts;
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
				}
			}

			public void onConstraintAdded(Constraint constraint) {

				onConstraintChange();
			}

			public void onConstraintRemoved(Constraint constraint) {

				onConstraintChange();
			}

			public void onConceptRemoved(Concept concept, boolean replacing) {

				remove();
			}

			ModelUpdateTracker() {

				concept.addListener(this);
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

			for (ConstraintType type : concept.getHierarchy().getConstraintTypes()) {

				if (showConstraints(type)) {

					ConstraintGroup group = new ConstraintGroup(concept, type);

					if (group.anyConstraints()) {

						addChild(new ConstraintGroupNode(this, group));
						displayedConstraints.add(group);
					}
				}
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

		ConstraintsNode(ConceptNode parentNode) {

			new Deselector(parentNode);
		}
	}

	private class ConstraintGroupNode extends ConstraintsNode {

		private ConceptNode sourceConceptNode;
		private ConstraintGroup group;

		protected void addInitialChildren() {

			for (Concept target : group.getImpliedValueTargets()) {

				addChild(new ImpliedValueConstraintTargetNode(sourceConceptNode, target));
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

	private class ImpliedValueConstraintTargetNode extends ConstraintsNode {

		private GCellDisplay display;

		protected GCellDisplay getDisplay() {

			return display;
		}

		ImpliedValueConstraintTargetNode(ConceptNode sourceConceptNode, Concept target) {

			super(sourceConceptNode);

			display = GoblinCellDisplay.CONCEPTS_CONSTRAINT_IMPLIED_TARGET.forConcept(target);
		}

		void redisplayAllConstraints(boolean modeChanged, boolean parentWasCollapsed) {
		}
	}

	ConceptTree(boolean multiSelect) {

		super(multiSelect);

		setRootVisible(false);
		setShowsRootHandles(true);
	}

	void initialise(Concept rootConcept) {

		initialise(Collections.singleton(rootConcept));
	}

	void initialise(Set<Concept> rootConcepts) {

		initialise(new RootNode(rootConcepts));

		this.rootConcepts = rootConcepts;
	}

	void redisplayForConstraintsDisplayModeChange() {

		getConceptTreeRootNode().redisplayAllConstraints(true);
	}

	void redisplayForConstraintsEdit() {

		getConceptTreeRootNode().redisplayAllConstraints(false);
	}

	boolean showConstraints(ConstraintType type) {

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

	abstract GCellDisplay getConceptDisplay(Concept concept);

	abstract void onConstraintChange();

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
