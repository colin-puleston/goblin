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

import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConstraintGroupPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String EDIT_PANEL_TITLE_FORMAT = "%s targets";

	static private final String SHOW_POTENTIAL_VALIDS_LABEL = "Show potentially valid";

	static private final String ADD_TARGETS_LABEL = "Add";
	static private final String REMOVE_TARGETS_LABEL = "Del";
	static private final String CLEAR_TARGETS_LABEL = "Clear";
	static private final String APPLY_EDITS_LABEL = "Apply edits";

	private ConstraintType type;
	private ConstraintSemantics semantics;

	private ConceptTree sourcesTree;

	private abstract class PanelPopulator {

		static private final long serialVersionUID = -1;

		final Concept source;
		final TargetsTree targetsTree;

		private class TargetsTree extends ConstraintTargetsTree {

			static private final long serialVersionUID = -1;

			TargetsTree() {

				super(!singleTargetSelection());
			}

			GCellDisplay getConceptDisplay(Concept concept) {

				return getCellDisplay(concept).forConcept(concept);
			}
		}

		private class TargetsTreeSelectorPanel extends ConceptTreeSelectorPanel {

			static private final long serialVersionUID = -1;

			TargetsTreeSelectorPanel() {

				super(targetsTree);
			}

			GCellDisplay getSelectorsCellDisplay(Concept concept, boolean highlight) {

				return getCellDisplay(concept).forConcept(concept, highlight);
			}
		}

		PanelPopulator(Concept source) {

			this.source = source;

			targetsTree = new TargetsTree();
		}

		void populate() {

			targetsTree.initialise(getValidValuesConstraint());

			add(createTargetsTreeHeaderPanel(), BorderLayout.NORTH);
			add(new JScrollPane(targetsTree), BorderLayout.CENTER);
		}

		void repopulate() {

			removeAll();
			populate();
			revalidate();
		}

		JComponent createTargetsTreeHeaderPanel() {

			return new TargetsTreeSelectorPanel();
		}

		abstract Constraint getValidValuesConstraint();

		boolean singleTargetSelection() {

			return false;
		}

		GoblinCellDisplay getCellDisplay(Concept concept) {

			return GoblinCellDisplay.CONSTRAINTS_VALID_TARGET;
		}
	}

	private class DefaultPanelPopulator extends PanelPopulator {

		static private final long serialVersionUID = -1;

		DefaultPanelPopulator() {

			super(type.getRootSourceConcept());
		}

		Constraint getValidValuesConstraint() {

			return source.lookForValidValuesConstraint(type);
		}

		boolean singleTargetSelection() {

			return false;
		}
	}

	private abstract class EditPanelPopulator extends PanelPopulator {

		static private final long serialVersionUID = -1;

		private Set<Concept> currentTargets = new HashSet<Concept>();

		private ConstraintTargetsDisplay targetsDisplay = null;
		private TargetSelectionsList targetSelectionsList = new TargetSelectionsList();

		private class TargetSelectionsList extends GList<Concept> {

			static private final long serialVersionUID = -1;

			TargetSelectionsList() {

				super(true, true);
			}

			void addTargets(Set<Concept> targets) {

				for (Concept target : targets) {

					addTarget(target);
				}
			}

			void addTarget(Concept target) {

				if (!containsEntity(target)) {

					if (singleTargetSelection()) {

						clearList();
					}

					addEntity(target, createCellDisplay(target));
				}
			}

			private GCellDisplay createCellDisplay(Concept target) {

				return getCellDisplay(target).forConcept(target);
			}
		}

		private class TargetAddButton extends ConceptTreeSelectionDependentButton {

			static private final long serialVersionUID = -1;

			private class SelectionsRemovalEnablingUpdater extends GListListener<Concept> {

				protected void onAdded(Concept entity) {
				}

				protected void onRemoved(Concept entity) {

					updateEnabling();
				}

				SelectionsRemovalEnablingUpdater() {

					targetSelectionsList.addListListener(this);
				}
			}

			protected void doButtonThing() {

				addTargetSelections(targetsTree.getAllSelectedConcepts());
				setEnabled(false);
			}

			TargetAddButton() {

				super(ADD_TARGETS_LABEL, targetsTree);

				new SelectionsRemovalEnablingUpdater();
			}

			boolean enableOnActiveSelections(List<Concept> selections) {

				for (Concept selection : selections) {

					if (selectableTarget(selection)) {

						return true;
					}
				}

				return false;
			}

			private boolean selectableTarget(Concept selection) {

				return !selection.isRoot() && !selectionTarget(selection);
			}
		}

		private class TargetRemoveButton extends ListSelectionDependentButton<Concept> {

			static private final long serialVersionUID = -1;

			protected void doButtonThing() {

				removeTargetSelections(targetSelectionsList.getSelectedEntities());
			}

			TargetRemoveButton() {

				super(REMOVE_TARGETS_LABEL, targetSelectionsList);
			}
		}

		private class TargetsClearButton extends GButton {

			static private final long serialVersionUID = -1;

			private class Enabler extends GListListener<Concept> {

				protected void onAdded(Concept entity) {

					setEnabled(true);
				}

				protected void onRemoved(Concept entity) {

					setEnabledIfAnyTargets();
				}

				Enabler() {

					setEnabledIfAnyTargets();

					targetSelectionsList.addListListener(this);
				}

				private void setEnabledIfAnyTargets() {

					setEnabled(targetSelectionsList.anyElements());
				}
			}

			protected void doButtonThing() {

				targetSelectionsList.clearList();
			}

			TargetsClearButton() {

				super(CLEAR_TARGETS_LABEL);

				new Enabler();
			}
		}

		private class ApplyEditsButton extends GButton {

			static private final long serialVersionUID = -1;

			private class Enabler extends GListListener<Concept> {

				protected void onAdded(Concept entity) {

					setEnabledIfAnyUpdates();
				}

				protected void onRemoved(Concept entity) {

					setEnabledIfAnyUpdates();
				}

				Enabler() {

					setEnabled(false);

					targetSelectionsList.addListListener(this);
				}

				private void setEnabledIfAnyUpdates() {

					setEnabled(!getTargetSelectionsSet().equals(currentTargets));
				}

				private Set<Concept> getTargetSelectionsSet() {

					return new HashSet<Concept>(getTargetSelections());
				}
			}

			protected void doButtonThing() {

				applyEdits(source, getTargetSelections());
				sourcesTree.redisplayForConstraintsEdit();
			}

			ApplyEditsButton() {

				super(APPLY_EDITS_LABEL);

				new Enabler();
			}
		}

		EditPanelPopulator(Concept source) {

			super(source);

			for (Constraint constraint : getEditConstraints()) {

				currentTargets.addAll(constraint.getTargetValues());
			}
		}

		void initialiseEditPanel() {

			targetsDisplay = createTargetsDisplay();
			targetSelectionsList.addTargets(currentTargets);
		}

		void populate() {

			super.populate();

			add(createActionsPanel(), BorderLayout.SOUTH);
		}

		GoblinCellDisplay getCellDisplay(Concept concept) {

			return targetsDisplay.getCellDisplay(concept);
		}

		abstract void applyEdits(Concept source, List<Concept> targets);

		private JPanel createActionsPanel() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(new JScrollPane(targetSelectionsList), BorderLayout.CENTER);
			panel.add(createActionsInvocationPanel(), BorderLayout.SOUTH);

			return TitledPanels.setTitle(panel, createActionsPanelTitle());
		}

		private JComponent createActionsInvocationPanel() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(createTargetsEditButtons(), BorderLayout.WEST);
			panel.add(new ApplyEditsButton(), BorderLayout.EAST);

			return panel;
		}

		private JComponent createTargetsEditButtons() {

			return ControlsPanel.horizontal(
						new TargetAddButton(),
						new TargetRemoveButton(),
						new TargetsClearButton());
		}

		private ConstraintTargetsDisplay createTargetsDisplay() {

			Constraint validValues = getValidValuesConstraint();
			Set<Constraint> impliedValues = source.getImpliedValueConstraints(type);

			return new ConstraintTargetsDisplay(validValues, impliedValues);
		}

		private List<Constraint> getEditConstraints() {

			return semantics.select(source.getConstraints(type));
		}

		private String createActionsPanelTitle() {

			return String.format(EDIT_PANEL_TITLE_FORMAT, semantics.getDisplayLabel());
		}

		private void addTargetSelections(List<Concept> newSelections) {

			for (Concept newSelection : newSelections) {

				addTargetSelection(newSelection);
			}
		}

		private void addTargetSelection(Concept newSelection) {

			for (Concept selection : getTargetSelections()) {

				if (conflictingConcepts(newSelection, selection)) {

					removeTargetSelection(selection);
				}
			}

			targetSelectionsList.addTarget(newSelection);
		}

		private void removeTargetSelections(List<Concept> selections) {

			for (Concept selection : selections) {

				removeTargetSelection(selection);
			}
		}

		private void removeTargetSelection(Concept selection) {

			targetSelectionsList.removeEntity(selection);
		}

		private List<Concept> getTargetSelections() {

			return targetSelectionsList.getEntities();
		}

		private boolean selectionTarget(Concept concept) {

			return targetSelectionsList.containsEntity(concept);
		}

		private boolean conflictingConcepts(Concept concept1, Concept concept2) {

			return concept1.descendantOf(concept2) || concept2.descendantOf(concept1);
		}

		private boolean validTargetConcept(Concept concept) {

			return concept.subsumedByAny(getValidValuesConstraint().getTargetValues());
		}
	}

	private class ValidValuesEditPanelPopulator extends EditPanelPopulator {

		private Constraint potentialValidValues;
		private Constraint localValidValues;

		private class ShowPotentiallyValidsSelector extends GCheckBox {

			static private final long serialVersionUID = -1;

			protected void onSelectionUpdate(boolean selected) {

				targetsTree.getRootNode().clearChildren();
				targetsTree.initialise(getValidValuesConstraint(selected));
			}

			ShowPotentiallyValidsSelector() {

				super(SHOW_POTENTIAL_VALIDS_LABEL);

				setSelected(false);
				setEnabled(localValidValues != null);
			}
		}

		ValidValuesEditPanelPopulator(Concept source) {

			super(source);

			potentialValidValues = source.getClosestAncestorValidValuesConstraint(type);
			localValidValues = source.lookForValidValuesConstraint(type);

			initialiseEditPanel();
		}

		JComponent createTargetsTreeHeaderPanel() {

			JPanel panel = new JPanel(new BorderLayout());

			panel.add(new ShowPotentiallyValidsSelector(), BorderLayout.WEST);
			panel.add(super.createTargetsTreeHeaderPanel(), BorderLayout.EAST);

			return panel;
		}

		Constraint getValidValuesConstraint() {

			return getValidValuesConstraint(false);
		}

		void applyEdits(Concept source, List<Concept> targets) {

			if (targets.isEmpty()) {

				if (localValidValues != null) {

					localValidValues.remove();
				}
			}
			else {

				source.addValidValuesConstraint(type, targets);
			}
		}

		private Constraint getValidValuesConstraint(boolean ensurePotential) {

			if (!ensurePotential && localValidValues != null) {

				return localValidValues;
			}

			return potentialValidValues;
		}
	}

	private class ImpliedValueEditPanelPopulator extends EditPanelPopulator {

		private Constraint validValues;
		private Map<Concept, Constraint> impliedValuesByTarget
								= new HashMap<Concept, Constraint>();

		ImpliedValueEditPanelPopulator(Concept source) {

			super(source);

			validValues = source.getClosestValidValuesConstraint(type);

			for (Constraint constraint : source.getImpliedValueConstraints(type)) {

				impliedValuesByTarget.put(constraint.getTargetValue(), constraint);
			}

			initialiseEditPanel();
		}

		Constraint getValidValuesConstraint() {

			return validValues;
		}

		boolean singleTargetSelection() {

			return type.singleValue();
		}

		void applyEdits(Concept source, List<Concept> targets) {

			for (Concept target : impliedValuesByTarget.keySet()) {

				if (!targets.contains(target)) {

					impliedValuesByTarget.get(target).remove();
				}
			}

			for (Concept target : targets) {

				if (!impliedValuesByTarget.keySet().contains(target)) {

					source.addImpliedValueConstraint(type, target);
				}
			}
		}
	}

	private class SourceConceptTracker extends GSelectionListener<GNode> {

		protected void onSelected(GNode node) {

			Concept selected = sourcesTree.getSelectedConcept();

			if (selected != null) {

				resetSourceConcept(selected);
			}
		}

		protected void onDeselected(GNode node) {

			clearSourceConcept();
		}
	}

	ConstraintGroupPanel(
		ConstraintType type,
		ConstraintSemantics semantics,
		ConceptTree sourcesTree) {

		super(new BorderLayout());

		this.type = type;
		this.semantics = semantics;
		this.sourcesTree = sourcesTree;

		new DefaultPanelPopulator().populate();

		sourcesTree.addNodeSelectionListener(new SourceConceptTracker());
	}

	private void resetSourceConcept(Concept source) {

		createPanelPopulator(source).repopulate();
	}

	private void clearSourceConcept() {

		new DefaultPanelPopulator().repopulate();
	}

	private PanelPopulator createPanelPopulator(Concept source) {

		if (source.equals(type.getRootSourceConcept())) {

			return new DefaultPanelPopulator();
		}

		return semantics.validValues()
				? new ValidValuesEditPanelPopulator(source)
				: new ImpliedValueEditPanelPopulator(source);
	}
}
