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

import java.awt.Dimension;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.config.*;

/**
 * @author Colin Puleston
 */
class HierarchySelector extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Select hierarchy";

	static private final Dimension WINDOW_SIZE = new Dimension(300, 300);

	private CoreHierarchyConfig selection = null;

	private class Option {

		final CoreHierarchyConfig hierarchy;

		public String toString() {

			return hierarchy.getLabel();
		}

		Option(CoreHierarchyConfig hierarchy) {

			this.hierarchy = hierarchy;
		}
	}

	private class ListSelectionListener extends GSelectionListener<Option> {

		protected void onSelected(Option option) {

			selection = option.hierarchy;

			dispose();
		}

		protected void onDeselected(Option selected) {
		}

		ListSelectionListener(GList<Option> list) {

			list.addSelectionListener(this);
		}
	}

	HierarchySelector(List<CoreHierarchyConfig> options) {

		super(TITLE, true);

		setPreferredSize(WINDOW_SIZE);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		display(new JScrollPane(createList(options)));
	}

	CoreHierarchyConfig getSelectionOrNull() {

		return selection;
	}

	private GList<Option> createList(List<CoreHierarchyConfig> options) {

		GList<Option> list = new GList<Option>(false, true);

		for (CoreHierarchyConfig option : options) {

			list.addEntity(new Option(option), ConfigCellDisplay.forHierarchy(option));
		}

		new ListSelectionListener(list);

		return list;
	}
}
