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

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.config.*;
import uk.ac.manchester.cs.goblin.gui.util.*;

/**
 * @author Colin Puleston
 */
class AttributesGonfigPanel extends MultiTabPanelWithEditControls<CoreAttributeConfig> {

	static private final long serialVersionUID = -1;

	private CoreHierarchyConfig hierarchy;
	private ConfigOntology ontology;

	protected List<CoreAttributeConfig> getSources() {

		return hierarchy.getCoreAttributes();
	}

	protected String getTitle(CoreAttributeConfig attribute) {

		return attribute.getLabel();
	}

	protected JComponent createComponent(CoreAttributeConfig attribute) {

		return new AttributeConfigValuesPanel(attribute, ontology, false);
	}

	protected boolean checkNewSource() {

		AttributeConfigValuesPanel values
			= new AttributeConfigValuesPanel(ontology, AttributeType.SIMPLE);

		if (checkValueEdits(values) && values.allSet()) {

			hierarchy.addCoreAttribute(createAttribute(values));

			return true;
		}

		return false;
	}

	protected boolean checkEditSource(CoreAttributeConfig attribute) {

		AttributeConfigValuesPanel values
			= new AttributeConfigValuesPanel(attribute, ontology, true);

		if (checkValueEdits(values)) {

			hierarchy.replaceCoreAttribute(attribute, createAttribute(values));

			return true;
		}

		return false;
	}

	protected boolean checkDeleteSource(CoreAttributeConfig attribute) {

		return false;
	}

	AttributesGonfigPanel(CoreHierarchyConfig hierarchy, ConfigOntology ontology) {

		super(JTabbedPane.LEFT);

		this.hierarchy = hierarchy;
		this.ontology = ontology;

		populate();
	}

	private boolean checkValueEdits(AttributeConfigValuesPanel values) {

		return new ValuesEditDialog(values, "attribute").editedValues();
	}

	private CoreAttributeConfig createAttribute(AttributeConfigValuesPanel values) {

		return values.createConfig(hierarchy.getRootConceptId());
	}
}
