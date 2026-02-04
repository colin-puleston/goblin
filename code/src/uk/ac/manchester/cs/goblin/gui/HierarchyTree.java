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

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class HierarchyTree extends DynamicConceptTree {

	static private final long serialVersionUID = -1;

	private Hierarchy hierarchy;

	private ConceptMover conceptMover = new ConceptMover();

	private ConstraintsDisplayMode constraintsDisplayMode = ConstraintsDisplayMode.NONE;
	private Attribute attributeSelection = null;

	private class HierarchyConceptNode extends DynamicConceptNode {

		protected void addInitialChildren() {

			super.addInitialChildren();

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

		HierarchyConceptNode(Concept concept) {

			super(concept);
		}

		void onConstraintsUpdated() {

			if (showAnyConstraints()) {

				redisplayConceptConstraints();
			}
		}

		void redisplayAllConstraints(boolean parentWasCollapsed) {

			boolean wasCollapsed = collapsed();

			redisplayConceptConstraints();
			redisplayAllConstraintsOnDescendantsOf(this, wasCollapsed);

			if (wasCollapsed || parentWasCollapsed) {

				collapse();
			}
		}

		private void redisplayConceptConstraints() {

			removeConstraintChildren();
			addConstraintChildren();
		}

		private void removeConstraintChildren() {

			for (GNode child : getChildren()) {

				if (child instanceof ConstraintGroupNode) {

					((ConstraintGroupNode)child).remove();
				}
			}
		}

		private void addConstraintChildren() {

			if (showAnyOutwardConstraints()) {

				addApplicableOutwardConstraintChildren();
			}

			if (showInwardConstraints()) {

				addAllInwardConstraintChildren();
			}
		}

		private void addApplicableOutwardConstraintChildren() {

			for (Attribute attribute : concept.getApplicableAttributes()) {

				if (showOutwardConstraintsFor(attribute)) {

					checkAddConstraintsChild(new OutwardConstraintGroup(concept, attribute));
				}
			}
		}

		private void addAllInwardConstraintChildren() {

			for (Attribute attribute : concept.getApplicableInwardAttributes()) {

				checkAddConstraintsChild(new InwardConstraintGroup(concept, attribute));
			}
		}

		private void checkAddConstraintsChild(ConstraintGroup group) {

			if (group.anyConstraints()) {

				addChild(new ConstraintGroupNode(this, group));
			}
		}
	}

	private abstract class ConstraintsRelatedNode extends ConceptTreeNode {

		final ConceptNode linkedConceptNode;

		ConstraintsRelatedNode(ConceptNode linkedConceptNode) {

			this.linkedConceptNode = linkedConceptNode;
		}

		void selectLinkedConceptNode() {

			linkedConceptNode.select();
		}
	}

	private class ConstraintsRelatedNodeDeselector extends GSelectionListener<GNode> {

		protected void onSelected(GNode node) {

			if (node instanceof ConstraintsRelatedNode) {

				((ConstraintsRelatedNode)node).selectLinkedConceptNode();
			}
		}

		protected void onDeselected(GNode node) {
		}

		ConstraintsRelatedNodeDeselector() {

			addNodeSelectionListener(this);
		}
	}

	private class ConstraintGroupNode extends ConstraintsRelatedNode {

		private ConstraintGroup group;

		protected void addInitialChildren() {

			for (Concept linked : group.getImpliedValueLinkedConcepts()) {

				addChild(new ImpliedValueConstraintLinkedNode(linkedConceptNode, linked));
			}
		}

		protected GCellDisplay getDisplay() {

			return GoblinCellDisplay.CONCEPTS_CONSTRAINT_GROUP.forConstraints(group);
		}

		ConstraintGroupNode(ConceptNode linkedConceptNode, ConstraintGroup group) {

			super(linkedConceptNode);

			this.group = group;
		}
	}

	private class ImpliedValueConstraintLinkedNode extends ConstraintsRelatedNode {

		private GCellDisplay display;

		protected GCellDisplay getDisplay() {

			return display;
		}

		ImpliedValueConstraintLinkedNode(ConceptNode linkedConceptNode, Concept linked) {

			super(linkedConceptNode);

			display = GoblinCellDisplay.CONCEPTS_CONSTRAINT_IMPLIED_TARGET.forConcept(linked);
		}
	}

	HierarchyTree(Hierarchy hierarchy) {

		super(true);

		this.hierarchy = hierarchy;

		initialise(hierarchy.getRootConcept());

		new ConstraintsRelatedNodeDeselector();
	}

	Hierarchy getHierarchy() {

		return hierarchy;
	}

	ConceptMover getConceptMover() {

		return conceptMover;
	}

	GCellDisplay getConceptDisplay(Concept concept) {

		return getGoblinCellDisplay(concept).forConcept(concept);
	}

	void setConstraintsDisplayMode(ConstraintsDisplayMode mode) {

		if (mode != constraintsDisplayMode) {

			constraintsDisplayMode = mode;

			redisplayAllConstraints();
		}
	}

	void setAttributeSelection(Attribute selection) {

		attributeSelection = selection;

		if (constraintsDisplayMode == ConstraintsDisplayMode.CURRENT_OUTWARDS) {

			redisplayAllConstraints();
		}
	}

	void update() {

		reselectSelected();
		updateAllNodeDisplays();
	}

	ConceptNode createConceptNode(Concept concept) {

		return new HierarchyConceptNode(concept);
	}

	GoblinCellDisplay getGoblinCellDisplay(Concept concept) {

		if (concept.coreConcept()) {

			return GoblinCellDisplay.CONCEPTS_CORE;
		}

		if (conceptMover.movingConcept(concept)) {

			return GoblinCellDisplay.CONCEPTS_MOVE_SUBJECT;
		}

		return GoblinCellDisplay.CONCEPTS_DYNAMIC;
	}

	private void redisplayAllConstraints() {

		redisplayAllConstraintsOnDescendantsOf(getRootNode(), false);
	}

	private void redisplayAllConstraintsOnDescendantsOf(GNode node, boolean wasCollapsed) {

		for (GNode child : node.getChildren()) {

			if (child instanceof HierarchyConceptNode) {

				((HierarchyConceptNode)child).redisplayAllConstraints(wasCollapsed);
			}
		}
	}

	private boolean showAnyConstraints() {

		return showAnyOutwardConstraints() || showInwardConstraints();
	}

	private boolean showAnyOutwardConstraints() {

		return constraintsDisplayMode.anyOutwards();
	}

	private boolean showOutwardConstraintsFor(Attribute attribute) {

		switch (constraintsDisplayMode) {

			case ALL_OUTWARDS:
				return true;

			case CURRENT_OUTWARDS:
				return attribute == attributeSelection;
		}

		throw new Error("Unexpected constraints display-mode: " + constraintsDisplayMode);
	}

	private boolean showInwardConstraints() {

		return constraintsDisplayMode == ConstraintsDisplayMode.ALL_INWARDS;
	}
}