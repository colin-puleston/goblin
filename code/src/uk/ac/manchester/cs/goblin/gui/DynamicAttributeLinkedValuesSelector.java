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

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class DynamicAttributeLinkedValuesSelector extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Current values hierarchies";
	static private final String CANCEL_LABEL = "Cancel";

	static private final Dimension WINDOW_SIZE = new Dimension(600, 400);

	private ModelSection localSection;
	private Hierarchy localHierarchy;

	private Hierarchy selectedValuesHierarchy = null;

	private class Option implements Comparable<Option> {

		private DynamicAttribute attribute;
		private ModelSection sourceSection;
		private Hierarchy sourceHierarchy;

		private String label;

		public int compareTo(Option other) {

			int lc = compareLocationTo(other);

			return lc != 0 ? lc : label.compareTo(other.label);
		}

		Option(
			DynamicAttribute attribute,
			ModelSection sourceSection,
			Hierarchy sourceHierarchy) {

			this.attribute = attribute;
			this.sourceSection = sourceSection;
			this.sourceHierarchy = sourceHierarchy;

			label = createLabel();
		}

		GCellDisplay createCellDisplay() {

			return GoblinCellDisplay.CONCEPTS_DYNAMIC.forConcept(label);
		}

		Hierarchy getValuesHierarchy() {

			return attribute.getRootTargetConcept().getHierarchy();
		}

		private String createLabel() {

			StringBuilder s = new StringBuilder();

			if (!localSection()) {

				s.append(sourceSection.getLabel() + " / ");
			}

			if (!localHierarchy()) {

				s.append(sourceHierarchy.getLabel() + " / ");
			}

			s.append(attribute.getRootSourceConcept().getConceptId().getLabel() + " >> ");
			s.append(attribute.getAttributeId().getLabel() + " >> ");
			s.append(attribute.getRootTargetConcept().getConceptId().getLabel());

			return s.toString();
		}

		private int compareLocationTo(Option other) {

			int lc1 = getLocationComparatorValue();
			int lc2 = other.getLocationComparatorValue();

			return lc1 - lc2;
		}

		private int getLocationComparatorValue() {

			return localHierarchy() ? -1 : (localSection() ? 0 : 1);
		}

		private boolean localSection() {

			return sourceSection == localSection;
		}

		private boolean localHierarchy() {

			return sourceHierarchy == localHierarchy;
		}
	}

	private class OptionsList extends GList<Option> {

		static private final long serialVersionUID = -1;

		private class Selector extends GSelectionListener<Option> {

			protected void onSelected(Option option) {

				selectedValuesHierarchy = option.getValuesHierarchy();

				dispose();
			}

			protected void onDeselected(Option option) {
			}
		}

		OptionsList() {

			super(false, false);

			for (Option option : getSortedOptions()) {

				addEntity(option, option.createCellDisplay());
			}

			addSelectionListener(new Selector());
		}

		private SortedSet<Option> getSortedOptions() {

			SortedSet<Option> sorter = new TreeSet<Option>();

			for (ModelSection section : localHierarchy.getModel().getSections()) {

				for (Hierarchy hierarchy : section.getCoreHierarchies()) {

					for (DynamicAttribute attribute : hierarchy.getDynamicAttributes()) {

						sorter.add(new Option(attribute, section, hierarchy));
					}
				}
			}

			return sorter;
		}
	}

	private class CancelButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			dispose();
		}

		CancelButton() {

			super(CANCEL_LABEL);
		}
	}

	DynamicAttributeLinkedValuesSelector(Concept newAttributeSource) {

		super(TITLE, true);

		localHierarchy = newAttributeSource.getHierarchy();
		localSection = findLocalSection();

		setPreferredSize(WINDOW_SIZE);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		display(createMainComponent());
	}

	Hierarchy getSelectedValuesHierarchy() {

		return selectedValuesHierarchy;
	}

	private JComponent createMainComponent() {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new JScrollPane(new OptionsList()), BorderLayout.CENTER);
		panel.add(new CancelButton(), BorderLayout.SOUTH);

		return panel;
	}

	private ModelSection findLocalSection() {

		for (ModelSection section : localHierarchy.getModel().getSections()) {

			for (Hierarchy hierarchy : section.getCoreHierarchies()) {

				if (hierarchy == localHierarchy) {

					return section;
				}
			}
		}

		throw new Error("Should never happen!");
	}
}
