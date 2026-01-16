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

import java.awt.Component;
import java.awt.Font;
import java.util.*;

import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
abstract class ConceptTreesPanel<S> extends JTabbedPane {

	static private final long serialVersionUID = -1;

	private List<S> sources;

	private class Repopulater {

		private List<S> currentSources = getSources();
		private int selectedIndex = getSelectedIndex();

		Repopulater() {

			if (selectedIndex != -1 && !selectionStillValid()) {

				selectedIndex = -1;
			}

			removeOldTabs();
			insertNewTabs();

			sources = currentSources;

			if (selectedIndex != -1) {

				setSelectedIndex(selectedIndex);
			}
		}

		private boolean selectionStillValid() {

			return currentSources.contains(sources.get(selectedIndex));
		}

		private void removeOldTabs() {

			int tab = 0;

			for (S source : sources) {

				if (currentSources.contains(source)) {

					tab++;
				}
				else {

					removeTabAt(tab);
				}
			}
		}

		private void insertNewTabs() {

			int tabIdx = 0;
			int oldSourceIdx = 0;

			for (S source : currentSources) {

				if (oldSourceIdx < sources.size() && sources.get(oldSourceIdx).equals(source)) {

					oldSourceIdx++;
				}
				else {

					addSourceTab(source, tabIdx);
				}

				tabIdx++;
			}
		}
	}

	ConceptTreesPanel(int tabPlacement) {

		super(tabPlacement);
	}

	void populate() {

		sources = getSources();

		int index = 0;

		for (S source : sources) {

			addSourceTab(source, index++);
		}
	}

	void repopulate() {

		new Repopulater();
	}

	void resetTabLabel(S source) {

		setTabLabel(source, sources.indexOf(source));
	}

	void makeSourceVisible(S source) {

		setSelectedIndex(sources.indexOf(source));
	}

	int checkMakeSourceVisible(Concept rootConcept) {

		int i = 0;

		for (S source : sources) {

			if (getRootConcept(source).equals(rootConcept)) {

				setSelectedIndex(i);

				return i;
			}

			i++;
		}

		return -1;
	}

	abstract List<S> getSources();

	abstract String getTitle(S source);

	abstract Concept getRootConcept(S source);

	abstract JComponent createComponent(S source);

	boolean requiresItalicizedLabel(S source) {

		return false;
	}

	private void addSourceTab(S source, int index) {

		insertTab("", null, createComponent(source), null, index);

		setTabLabel(source, index);
	}

	private void setTabLabel(S source, int index) {

		setTabComponentAt(index, createTabLabel(source, index));
	}

	private JLabel createTabLabel(S source, int index) {

		JLabel label = new JLabel(getTitle(source));
		Font font = label.getFont();

		font = GFonts.toMedium(font);

		if (requiresItalicizedLabel(source)) {

			font = font.deriveFont(Font.ITALIC);
		}

		label.setFont(font);

		return label;
	}
}
