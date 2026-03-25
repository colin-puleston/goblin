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

import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
abstract class ConfigEntitySelectorDialog extends TreeNodeSelectorDialog<ConfigEntity> {

	static private final long serialVersionUID = -1;

	static private final String TITLE_FORMAT = "Select %s";

	private SelectorTree tree;
	private ConfigEntity rootNode;

	private ConfigEntity selection = null;

	private class SelectorTree extends ConfigEntityTree {

		static private final long serialVersionUID = -1;

		SelectorTree(ConfigEntity rootNode) {

			initialise(rootNode);
		}

		boolean requiredEntity(ConfigEntity entity) {

			return requiredInTree(entity);
		}

		GCellDisplay getEntityDisplay(ConfigEntity entity) {

			return getTreeCellDisplay(entity);
		}
	}

	protected Collection<ConfigEntity> getRootNodes() {

		return rootNode.getChildren();
	}

	protected Collection<ConfigEntity> getChildNodes(ConfigEntity parent) {

		return parent.getChildren();
	}

	protected String getNodeLabel(ConfigEntity entity) {

		return entity.getId().getLabel();
	}

	protected ConfigEntity toSubjectNode(GNode guiNode) {

		return ConfigEntityTree.extractConfigEntity(guiNode);
	}

	protected void onSelected(ConfigEntity entity) {

		selection = entity;
	}

	ConfigEntitySelectorDialog(JPanel parent, ConfigEntity rootNode, String entityType) {

		super(parent, String.format(TITLE_FORMAT, entityType));

		this.rootNode = rootNode;

		tree = new SelectorTree(rootNode);

		initialise(tree);
	}

	EntityId getSelectionIdOrNull() {

		return selection != null ? selection.getId() : null;
	}
}
