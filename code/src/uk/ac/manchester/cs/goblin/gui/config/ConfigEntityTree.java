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
import javax.swing.tree.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

import uk.ac.manchester.cs.goblin.io.config.*;

/**
 * @author Colin Puleston
 */
abstract class ConfigEntityTree extends GSelectorTree {

	static private final long serialVersionUID = -1;

	static boolean selectableEntity(GNode node) {

		return toSelectableEntityNodeOrNull(node) != null;
	}

	static ConfigEntity checkExtractSelectableEntity(GNode node) {

		EntityNode selNode = toSelectableEntityNodeOrNull(node);

		return selNode != null ? selNode.entity : null;
	}

	static private EntityNode toSelectableEntityNodeOrNull(GNode node) {

		if (node instanceof EntityNode) {

			EntityNode eNode = (EntityNode)node;

			if (eNode.selectable) {

				return eNode;
			}
		}

		return null;
	}

	private abstract class EntityTreeNode extends GNode {

		final ConfigEntity entity;

		protected void addInitialChildren() {

			for (ConfigEntity childEntity : entity.getChildren()) {

				if (requiredEntity(childEntity)) {

					EntityNode child = new EntityNode(childEntity);

					addChild(child);
					child.ensureChildren();
				}
			}
		}

		protected boolean orderedChildren() {

			return true;
		}

		EntityTreeNode(ConfigEntity entity) {

			super(ConfigEntityTree.this);

			this.entity = entity;
		}
	}

	private class RootNode extends EntityTreeNode {

		protected boolean autoExpand() {

			return true;
		}

		protected GCellDisplay getDisplay() {

			return GCellDisplay.NO_DISPLAY;
		}

		RootNode(ConfigEntity rootEntity) {

			super(rootEntity);
		}
	}

	private class EntityNode extends EntityTreeNode {

		final boolean selectable;

		protected boolean autoExpand() {

			return false;
		}

		protected GCellDisplay getDisplay() {

			return getEntityDisplay(entity, selectable);
		}

		EntityNode(ConfigEntity entity) {

			super(entity);

			selectable = selectableEntity(entity);
		}
	}

	ConfigEntityTree() {

		super(false);

		setRootVisible(false);
		setShowsRootHandles(true);
	}

	void initialise(ConfigEntity rootEntity) {

		initialise(new RootNode(rootEntity));
	}

	abstract boolean requiredEntity(ConfigEntity entity);

	abstract boolean selectableEntity(ConfigEntity entity);

	abstract GCellDisplay getEntityDisplay(ConfigEntity entity, boolean selectable);
}
