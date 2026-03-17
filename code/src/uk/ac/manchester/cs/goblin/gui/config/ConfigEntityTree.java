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

	static ConfigEntity extractConfigEntity(GNode node) {

		return ((ConfigEntityNode)node).entity;
	}

	private abstract class ConfigEntityTreeNode extends GNode {

		final ConfigEntity entity;

		protected void addInitialChildren() {

			for (ConfigEntity childEntity : entity.getChildren()) {

				if (requiredEntity(childEntity)) {

					ConfigEntityNode child = new ConfigEntityNode(childEntity);

					addChild(child);
					child.ensureChildren();
				}
			}
		}

		protected boolean orderedChildren() {

			return true;
		}

		ConfigEntityTreeNode(ConfigEntity entity) {

			super(ConfigEntityTree.this);

			this.entity = entity;
		}
	}

	private class RootNode extends ConfigEntityTreeNode {

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

	private class ConfigEntityNode extends ConfigEntityTreeNode {

		protected boolean autoExpand() {

			return false;
		}

		protected GCellDisplay getDisplay() {

			return getEntityDisplay(entity);
		}

		ConfigEntityNode(ConfigEntity entity) {

			super(entity);
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

	abstract GCellDisplay getEntityDisplay(ConfigEntity entity);
}
