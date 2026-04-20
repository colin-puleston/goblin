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

	private class ListSelectionListener extends GSelectionListener<CoreHierarchyConfig> {

		protected void onSelected(CoreHierarchyConfig option) {

			selection = option;

			dispose();
		}

		protected void onDeselected(CoreHierarchyConfig option) {
		}

		ListSelectionListener(GList<CoreHierarchyConfig> list) {

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

	String getOptionDisplayLabel(CoreHierarchyConfig option) {

		return option.getLabel();
	}

	private GList<CoreHierarchyConfig> createList(List<CoreHierarchyConfig> options) {

		GList<CoreHierarchyConfig> list = new GList<CoreHierarchyConfig>(false, true);

		for (CoreHierarchyConfig option : options) {

			list.addEntity(option, getOptionCellDisplay(option));
		}

		new ListSelectionListener(list);

		return list;
	}

	private GCellDisplay getOptionCellDisplay(CoreHierarchyConfig option) {

		return ConfigCellDisplay.forHierarchy(getOptionDisplayLabel(option));
	}
}
