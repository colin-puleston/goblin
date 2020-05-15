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

import java.awt.Color;
import java.awt.Font;
import java.util.*;

import uk.ac.manchester.cs.mekon.gui.*;
import uk.ac.manchester.cs.mekon.gui.icon.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
enum GoblinCellDisplay {

	CONCEPTS_DEFAULT(
		largeCircle(ConceptColor.DEFAULT)),
	CONCEPTS_MOVE_SUBJECT(
		largeCircle(ConceptColor.MOVE_SUBJECT)),
	CONCEPTS_CONSTRAINT_GROUP(
		mediumCircle(ConstraintColor.VALID_TARGET)),
	CONCEPTS_CONSTRAINT_IMPLIED_TARGET(
		mediumCircle(ConstraintColor.IMPLIED_TARGET)),

	CONSTRAINTS_POTENTIAL_TARGET(
		largeCircle(ConstraintColor.POTENTIAL_TARGET)),
	CONSTRAINTS_VALID_TARGET(
		largeCircle(ConstraintColor.VALID_TARGET)),
	CONSTRAINTS_IMPLIED_TARGET(
		largeCircle(ConstraintColor.VALID_TARGET),
		smallCircle(ConstraintColor.IMPLIED_TARGET));

	static private final int LARGE_ICON_SIZE = 12;
	static private final int MEDIUM_ICON_SIZE = 8;
	static private final int SMALL_ICON_SIZE = 6;

	static private final Color HIGHLIGHTED_BACKGROUND_CLR = new Color(255,237,160);

	static private class ConceptColor {

		static final Color DEFAULT = Color.CYAN;
		static final Color MOVE_SUBJECT = Color.GRAY.brighter();
	}

	static private class ConstraintColor {

		static final Color POTENTIAL_TARGET = Color.YELLOW;
		static final Color VALID_TARGET = Color.GREEN;
		static final Color IMPLIED_TARGET = Color.GREEN.darker();
	}

	static private GIconRenderer largeCircle(Color clr) {

		return new GOvalRenderer(clr, LARGE_ICON_SIZE);
	}

	static private GIconRenderer mediumCircle(Color clr) {

		return reducedCircle(clr, MEDIUM_ICON_SIZE);
	}

	static private GIconRenderer smallCircle(Color clr) {

		return reducedCircle(clr, SMALL_ICON_SIZE);
	}

	static private GIconRenderer reducedCircle(Color clr, int size) {

		GIconRenderer r = new GOvalRenderer(clr, size);
		int offset = (LARGE_ICON_SIZE - size) / 2;

		r.setXOffset(offset);
		r.setYOffset(offset);

		return r;
	}

	private GIcon icon = new GIcon();

	GCellDisplay forConcept(Concept concept) {

		GCellDisplay display = new GCellDisplay(concept.getConceptId().getLabel(), icon);

		display.setFontStyle(Font.BOLD);

		return display;
	}

	GCellDisplay forConcept(Concept concept, boolean highlight) {

		GCellDisplay display = forConcept(concept);

		if (highlight) {

			display.setBackgroundColour(HIGHLIGHTED_BACKGROUND_CLR);
		}

		return display;
	}

	GCellDisplay forConstraints(ConstraintGroup group) {

		GCellDisplay display = new GCellDisplay(group.getTypeName(), icon);
		Set<Concept> validValueTargets = group.getValidValuesTargets();

		display.setFontStyle(Font.ITALIC);

		if (!validValueTargets.isEmpty()) {

			display.setModifier(forValidValuesConstraintTargets(validValueTargets));
		}

		return display;
	}

	private GoblinCellDisplay(GIconRenderer... renderers) {

		for (GIconRenderer renderer : renderers) {

			icon.addRenderer(renderer);
		}
	}

	private GCellDisplay forValidValuesConstraintTargets(Set<Concept> targets) {

		GCellDisplay display = new GCellDisplay(getConceptSetLabel(targets));

		display.setFontStyle(Font.BOLD | Font.ITALIC);

		return display;
	}

	private String getConceptSetLabel(Set<Concept> concepts) {

		SortedSet<String> labels = new TreeSet<String>();

		for (Concept concept : concepts) {

			labels.add(concept.getConceptId().getLabel());
		}

		return labels.toString();
	}
}
