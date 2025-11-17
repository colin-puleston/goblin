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

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConstraintGroupPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String SHOW_POTENTIAL_VALIDS_LABEL = "Show potentially valid";

	static private final String ADD_TARGETS_LABEL = "Add";
	static private final String REMOVE_TARGETS_LABEL = "Del";
	static private final String CLEAR_TARGETS_LABEL = "Clear";
	static private final String APPLY_EDITS_LABEL = "Apply edits";

	private ConstraintType type;
	private ConceptTree sourcesTree;

	private Map<Concept, ConceptListener> conceptListeners = new HashMap<Concept, ConceptListener>();

	private abstract class PanelPopulator {

		static private final long serialVersionUID = -1;

		final Concept source;
		final TargetsTree targetsTree;

		private class TargetsTree extends ConceptTree {

			static private final long serialVersionUID = -1;

			TargetsTree() {

				super(true);
			}

			void initialise(Constraint constraint) {

				initialise(constraint.getTargetValues());
			}

			void onAddedConceptListener(Concept concept, ConceptListener listener) {

				conceptListeners.put(concept, listener);
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

			add(createHeaderPanel(), BorderLayout.NORTH);
			add(new JScrollPane(targetsTree), BorderLayout.CENTER);
		}

		void repopulate() {

			clearConceptListeners();

			removeAll();
			populate();
			revalidate();
		}

		JComponent createHeaderPanel() {

			return new TargetsTreeSelectorPanel();
		}

		abstract Constraint getValidValuesConstraint();

		abstract GoblinCellDisplay getCellDisplay(Concept concept);
	}

	private class DefaultPanelPopulator extends PanelPopulator {

		static private final long serialVersionUID = -1;

		DefaultPanelPopulator() {

			super(type.getRootSourceConcept());
		}

		Constraint getValidValuesConstraint() {

			return source.lookForValidValuesConstraint(type);
		}

		GoblinCellDisplay getCellDisplay(Concept concept) {

			return GoblinCellDisplay.CONSTRAINTS_VALID_TARGET;
		}
	}

	private class EditPanelPopulator extends PanelPopulator {

		private Constraint potentialValidValues;
		private Constraint localValidValues;

		private ConstraintTargetsDisplay targetsDisplay;

		private class ShowPotentiallyValidsOptionSelector extends GCheckBox {

			static private final long serialVersionUID = -1;

			protected void onSelectionUpdate(boolean selected) {

				targetsTree.getRootNode().clearChildren();
				targetsTree.initialise(getValidValuesConstraint(selected));
			}

			ShowPotentiallyValidsOptionSelector() {

				super(SHOW_POTENTIAL_VALIDS_LABEL);

				setSelected(false);
				setEnabled(localValidValues != null);
			}
		}

		EditPanelPopulator(Concept source) {

			super(source);

			potentialValidValues = source.getClosestAncestorValidValuesConstraint(type);
			localValidValues = source.lookForValidValuesConstraint(type);

			targetsDisplay = createTargetsDisplay();
		}

		void populate() {

			super.populate();

			add(createActionsTabs(), BorderLayout.SOUTH);
		}

		JComponent createHeaderPanel() {

			if (type.definesValidValues()) {

				JPanel panel = new JPanel(new BorderLayout());

				panel.add(new ShowPotentiallyValidsOptionSelector(), BorderLayout.WEST);
				panel.add(super.createHeaderPanel(), BorderLayout.EAST);

				return panel;
			}

			return super.createHeaderPanel();
		}

		Constraint getValidValuesConstraint() {

			return getValidValuesConstraint(false);
		}

		GoblinCellDisplay getCellDisplay(Concept concept) {

			return targetsDisplay.getCellDisplay(concept);
		}

		private JTabbedPane createActionsTabs() {

			JTabbedPane tabs = new JTabbedPane();

			if (type.definesValidValues()) {

				addActionsTab(tabs, createValidValuesPanel());
			}

			if (type.definesImpliedValues()) {

				addActionsTab(tabs, createImpliedValuesPanel());
			}

			return tabs;
		}

		private EditActionsPanel createValidValuesPanel() {

			return new ValidValuesEditActionsPanel(source, localValidValues, targetsTree);
		}

		private EditActionsPanel createImpliedValuesPanel() {

			return new ImpliedValuesEditActionsPanel(source, getValidValuesConstraint(), targetsTree);
		}

		private void addActionsTab(JTabbedPane tabs, EditActionsPanel actionsPanel) {

			tabs.addTab(actionsPanel.getTitle(), actionsPanel);
		}

		private ConstraintTargetsDisplay createTargetsDisplay() {

			Constraint validValues = getValidValuesConstraint();
			Set<Constraint> impliedValues = source.getImpliedValueConstraints(type);

			return new ConstraintTargetsDisplay(validValues, impliedValues);
		}

		private Constraint getValidValuesConstraint(boolean ensurePotential) {

			if (!ensurePotential && localValidValues != null) {

				return localValidValues;
			}

			return potentialValidValues;
		}
	}

	private abstract class EditActionsPanel extends JPanel {

		static private final long serialVersionUID = -1;

		private Concept source;
		private Set<Concept> currentTargets = new HashSet<Concept>();

		private ConceptTree targetsTree;
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

					addEntity(target, targetsTree.getConceptDisplay(target));
				}
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

				if (singleTargetSelection() && selections.size() != 1) {

					return false;
				}

				for (Concept selection : selections) {

					if (!validTargetSelection(selection)) {

						return false;
					}
				}

				return true;
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

				resetSourceConcept(source);
			}

			ApplyEditsButton() {

				super(APPLY_EDITS_LABEL);

				new Enabler();
			}
		}

		EditActionsPanel(Concept source, ConceptTree targetsTree) {

			super(new BorderLayout());

			this.source = source;
			this.targetsTree = targetsTree;

			findCurrentTargets();
			targetSelectionsList.addTargets(currentTargets);

			add(new JScrollPane(targetSelectionsList), BorderLayout.CENTER);
			add(createButtonsPanel(), BorderLayout.SOUTH);
		}

		String getTitle() {

			return getSemantics().getDisplayLabel();
		}

		abstract ConstraintSemantics getSemantics();

		abstract boolean singleTargetSelection();

		boolean validTargetSelection(Concept selection) {

			return !selection.isRoot() && !selectionTarget(selection);
		}

		abstract void applyEdits(Concept source, List<Concept> targets);

		private void findCurrentTargets() {

			for (Constraint constraint : getEditConstraints()) {

				currentTargets.addAll(constraint.getTargetValues());
			}
		}

		private List<Constraint> getEditConstraints() {

			return getSemantics().select(source.getConstraints(type));
		}

		private JComponent createButtonsPanel() {

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
	}

	private class ValidValuesEditActionsPanel extends EditActionsPanel {

		static private final long serialVersionUID = -1;

		private Constraint localValidValues;

		ValidValuesEditActionsPanel(
			Concept source,
			Constraint localValidValues,
			ConceptTree targetsTree) {

			super(source, targetsTree);

			this.localValidValues = localValidValues;
		}

		ConstraintSemantics getSemantics() {

			return ConstraintSemantics.VALID_VALUES;
		}

		boolean singleTargetSelection() {

			return false;
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
	}

	private class ImpliedValuesEditActionsPanel extends EditActionsPanel {

		static private final long serialVersionUID = -1;

		private Constraint validValues;

		private Map<Concept, Constraint> impliedValuesByTarget
								= new HashMap<Concept, Constraint>();

		ImpliedValuesEditActionsPanel(
			Concept source,
			Constraint validValues,
			ConceptTree targetsTree) {

			super(source, targetsTree);

			this.validValues = validValues;

			for (Constraint constraint : source.getImpliedValueConstraints(type)) {

				impliedValuesByTarget.put(constraint.getTargetValue(), constraint);
			}
		}

		String getTitle() {

			return super.getTitle() + (type.singleImpliedValues() ? "" : "(s)");
		}

		ConstraintSemantics getSemantics() {

			return ConstraintSemantics.IMPLIED_VALUE;
		}

		boolean singleTargetSelection() {

			return type.singleImpliedValues();
		}

		boolean validTargetSelection(Concept selection) {

			return super.validTargetSelection(selection) && validTarget(selection);
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

		private boolean validTarget(Concept concept) {

			return concept.subsumedByAny(validValues.getTargetValues());
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

	ConstraintGroupPanel(ConstraintType type, ConceptTree sourcesTree) {

		super(new BorderLayout());

		this.type = type;
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

		return new EditPanelPopulator(source);
	}

	private void clearConceptListeners() {

		for (Concept concept : conceptListeners.keySet()) {

			concept.removeListener(conceptListeners.get(concept));
		}
	}
}
