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

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.config.*;

/**
 * @author Colin Puleston
 */
class TargetHierarchyMonitor {

	private List<CoreAttributeConfig> attributes = new ArrayList<CoreAttributeConfig>();
	private List<TargetHierarchyListener> listeners = new ArrayList<TargetHierarchyListener>();

	private RelabelProcessor relabelProcessor = new RelabelProcessor();
	private RemovalProcessor removalProcessor = new RemovalProcessor();

	private abstract class UpdateProcessor {

		void processFor(CoreHierarchyConfig hierarchy) {

			EntityId rootConceptId = hierarchy.getRootConceptId();

			for (CoreAttributeConfig attribute : copyAttributes()) {

				if (attribute.getRootTargetConceptId().equals(rootConceptId)) {

					pollAttributeListenersForUpdate(attribute);
				}
			}
		}

		abstract void invokeListenerForUpdate(
							TargetHierarchyListener listener,
							CoreAttributeConfig attribute);

		private void pollAttributeListenersForUpdate(CoreAttributeConfig attribute) {

			for (TargetHierarchyListener listener : listeners) {

				invokeListenerForUpdate(listener, attribute);
			}
		}
	}

	private class RelabelProcessor extends UpdateProcessor {

		void invokeListenerForUpdate(
				TargetHierarchyListener listener,
				CoreAttributeConfig attribute) {

			listener.onHierarchyRelabelled(attribute);
		}
	}

	private class RemovalProcessor extends UpdateProcessor {

		void invokeListenerForUpdate(
				TargetHierarchyListener listener,
				CoreAttributeConfig attribute) {

			listener.onHierarchyRemoved(attribute);
		}
	}

	TargetHierarchyMonitor(ModelConfig modelConfig) {

		for (CoreHierarchyConfig hierarchy : modelConfig.getHierarchies()) {

			for (CoreAttributeConfig attribute : hierarchy.getCoreAttributes()) {

				attributes.add(attribute);
			}
		}
	}

	void addListener(TargetHierarchyListener listener) {

		listeners.add(listener);
	}

	void onCoreAttributeAdded(CoreAttributeConfig attribute) {

		attributes.add(attribute);
	}

	void onCoreAttributeRemoved(CoreAttributeConfig attribute) {

		attributes.remove(attribute);
	}

	void onCoreHierarchyRelabelled(CoreHierarchyConfig hierarchy) {

		relabelProcessor.processFor(hierarchy);
	}

	void onCoreHierarchyRemoved(CoreHierarchyConfig hierarchy) {

		removalProcessor.processFor(hierarchy);
	}

	private List<CoreAttributeConfig> copyAttributes() {

		return new ArrayList<CoreAttributeConfig>(attributes);
	}
}