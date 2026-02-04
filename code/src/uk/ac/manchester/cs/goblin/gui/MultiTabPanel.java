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

/**
 * @author Colin Puleston
 */
abstract class MultiTabPanel<S> extends JTabbedPane {

	static private final long serialVersionUID = -1;

	private List<S> sources;

	private class Repopulater {

		private List<S> oldSources = sources;

		Repopulater() {

			sources = getSources();

			int selectedIndex = getNewSelectedIndex();

			removeOldTabs();
			insertNewTabs();

			if (selectedIndex != -1) {

				setSelectedIndex(selectedIndex);
			}
		}

		private int getNewSelectedIndex() {

			int oldIdx = getSelectedIndex();

			if (oldIdx == -1) {

				return -1;
			}

			return sources.indexOf(oldSources.get(oldIdx));
		}

		private void removeOldTabs() {

			int tabIdx = 0;

			for (S source : new ArrayList<S>(oldSources)) {

				if (sources.contains(source)) {

					tabIdx++;
				}
				else {

					removeTabAt(tabIdx);

					oldSources.remove(source);
					onRemoved(source);
				}
			}
		}

		private void insertNewTabs() {

			int tabIdx = 0;
			int oldIdx = 0;

			for (S source : sources) {

				if (oldIdx < oldSources.size() && oldSources.get(oldIdx).equals(source)) {

					oldIdx++;
				}
				else {

					addSourceTab(source, tabIdx);
				}

				tabIdx++;
			}
		}
	}

	MultiTabPanel(int tabPlacement) {

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

	JComponent getSourceComponent(S source) {

		return (JComponent)getComponentAt(sources.indexOf(source));
	}

	abstract List<S> getSources();

	abstract String getTitle(S source);

	abstract JComponent createComponent(S source);

	void onRemoved(S source) {
	}

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
