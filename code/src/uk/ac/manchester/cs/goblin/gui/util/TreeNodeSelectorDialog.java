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

package uk.ac.manchester.cs.goblin.gui.util;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
public abstract class TreeNodeSelectorDialog<N> extends GDialog {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "Select %s";
	static private final String LIST_TITLE = "List";
	static private final String TREE_TITLE = "Tree";

	static private final Dimension WINDOW_SIZE = new Dimension(400, 400);

	private TreeFilterPanel treeFilterPanel = null;

	private class ListSelectionListener extends GSelectionListener<N> {

		protected void onSelected(N node) {

			performSelectionActions(node);
		}

		protected void onDeselected(N node) {
		}

		ListSelectionListener(GList<N> list) {

			list.addSelectionListener(this);
		}
	}

	private class TreeSelectionListener extends GSelectionListener<GNode> {

		protected void onSelected(GNode node) {

			N subjectNode = toSubjectNode(node);

			if (subjectNode != null) {

				performSelectionActions(subjectNode);
			}
		}

		protected void onDeselected(GNode node) {
		}

		TreeSelectionListener(GSelectorTree tree) {

			tree.addNodeSelectionListener(this);
		}
	}

	private class TreeFilterPanel extends GTreeFilterPanel<N> {

		static private final long serialVersionUID = -1;

		private GSelectorTree tree;

		protected void applyFilter(GLexicalFilter filter) {

			super.applyFilter(filter);

			tree.expandAll();
		}

		protected void clearFilter() {

			super.clearFilter();

			tree.collapseAll();
		}

		protected void reinitialiseTree() {

			tree.reinitialise();
		}

		protected Collection<N> getRootNodes() {

			return TreeNodeSelectorDialog.this.getRootNodes();
		}

		protected Collection<N> getChildNodes(N parent) {

			return TreeNodeSelectorDialog.this.getChildNodes(parent);
		}

		protected String getNodeLabel(N node) {

			return TreeNodeSelectorDialog.this.getNodeLabel(node);
		}

		TreeFilterPanel(GSelectorTree tree) {

			this.tree = tree;
		}
	}

	public Dimension getPreferredSize() {

		return WINDOW_SIZE;
	}

	protected TreeNodeSelectorDialog(JComponent parent, String title) {

		super(parent, title, true);
	}

	protected void initialise(GSelectorTree tree) {

		treeFilterPanel = new TreeFilterPanel(tree);

		GList<N> list = createList();

		new ListSelectionListener(list);
		new TreeSelectionListener(tree);

		display(createTabs(list, tree));
	}

	protected boolean requiredInTree(N node) {

		return treeFilterPanel == null || treeFilterPanel.requiredInTree(node);
	}

	protected abstract Collection<N> getRootNodes();

	protected abstract Collection<N> getChildNodes(N parent);

	protected abstract String getNodeLabel(N node);

	protected abstract N toSubjectNode(GNode guiNode);

	protected GCellDisplay getTreeCellDisplay(N node) {

		return getCellDisplay(node, passesFilter(node));
	}

	protected abstract GCellDisplay getCellDisplay(N node, boolean highlight);

	protected abstract void onSelected(N node);

	private GList<N> createList() {

		GList<N> list = new GList<N>(false, true);

		populateList(list, getRootNodes());

		return list;
	}

	private void populateList(GList<N> list, Collection<N> nodes) {

		for (N node : nodes) {

			list.addEntity(node, getCellDisplay(node, false));

			populateList(list, getChildNodes(node));
		}
	}

	private JTabbedPane createTabs(GList<N> list, GSelectorTree tree) {

		JTabbedPane tabs = new JTabbedPane();

		tabs.addTab(LIST_TITLE, new GListPanel<N>(list));
		tabs.addTab(TREE_TITLE, createTreePanel(tree));

		return tabs;
	}

	private JPanel createTreePanel(GSelectorTree tree) {

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new JScrollPane(tree), BorderLayout.CENTER);
		panel.add(treeFilterPanel, BorderLayout.SOUTH);

		return panel;
	}

	private boolean passesFilter(N node) {

		return treeFilterPanel != null && treeFilterPanel.passesFilter(node);
	}

	private void performSelectionActions(N selected) {

		onSelected(selected);

		dispose();
	}
}
