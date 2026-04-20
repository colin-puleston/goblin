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

package uk.ac.manchester.cs.goblin.gui.config;

import java.awt.Color;
import java.awt.Font;

import uk.ac.manchester.cs.mekon_util.gui.*;
import uk.ac.manchester.cs.mekon_util.gui.icon.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class ConfigCellDisplay {

	static private final Color CONCEPT_CLR = Color.BLUE;
	static private final Color PROPERTY_CLR = Color.ORANGE;
	static private final Color INERT_ENTITY_CLR = Color.GRAY;
	static private final Color HIGHLIGHT_CLR = new Color(255, 237, 160);

	static private final GIcon CONCEPT_ICON = createConceptIcon(CONCEPT_CLR);
	static private final GIcon PROPERTY_ICON = createPropertyIcon(PROPERTY_CLR);

	static private final GIcon INERT_CONCEPT_ICON = createConceptIcon(INERT_ENTITY_CLR);
	static private final GIcon INERT_PROPERTY_ICON = createPropertyIcon(INERT_ENTITY_CLR);

	static GCellDisplay forHierarchy(String displayLabel) {

		return forEntity(displayLabel, false, CONCEPT_ICON);
	}

	static GCellDisplay forConcept(ConfigEntity concept, boolean highlight) {

		return forEntity(concept, highlight, CONCEPT_ICON);
	}

	static GCellDisplay forInertConcept(ConfigEntity property) {

		return forEntity(property, false, INERT_CONCEPT_ICON);
	}

	static GCellDisplay forProperty(ConfigEntity property, boolean highlight) {

		return forEntity(property, highlight, PROPERTY_ICON);
	}

	static GCellDisplay forInertProperty(ConfigEntity property) {

		return forEntity(property, false, INERT_PROPERTY_ICON);
	}

	static private GCellDisplay forEntity(ConfigEntity entity, boolean highlight, GIcon icon) {

		return forEntity(entity.getId().getLabel(), highlight, icon);
	}

	static private GCellDisplay forEntity(String label, boolean highlight, GIcon icon) {

		GCellDisplay display = new GCellDisplay(label, icon);

		if (highlight) {

			display.setBackgroundColour(HIGHLIGHT_CLR);
		}

		display.setFontStyle(Font.BOLD);

		return display;
	}

	static private GIcon createConceptIcon(Color clr) {

		return new GIcon(CellDisplayUtils.largeCircle(clr));
	}

	static private GIcon createPropertyIcon(Color clr) {

		return new GIcon(CellDisplayUtils.largeRightwardTriangle(clr));
	}
}
