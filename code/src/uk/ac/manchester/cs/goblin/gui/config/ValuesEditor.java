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

import uk.ac.manchester.cs.goblin.config.*;

/**
 * @author Colin Puleston
 */
abstract class ValuesEditor<S extends LabelledConfigEntity, V extends ValuesPanel> {

	private EditManager editManager;

	private class EditListener extends ValuesPanelListener {

		private V values;
		private S currentSource;

		EditListener(V values, S currentSource) {

			this.values = values;
			this.currentSource = currentSource;

			values.addListener(this);
		}

		void onValueEdit() {

			S newSource = createSource(values);

			newSource.resetLabel(currentSource.getLabel());
			replaceSource(currentSource, newSource);

			currentSource = newSource;

			editManager.registerEdit();
		}
	}

	ValuesEditor(EditManager editManager) {

		this.editManager = editManager;
	}

	boolean checkNewSource() {

		V values = checkCreateEmptyValues();

		if (values != null && checkNewValueSelection(values) && values.allValuesSet()) {

			addNewSource(createSource(values));

			editManager.registerEdit();

			return true;
		}

		return false;
	}

	V checkSourceEdits(S currentSource) {

		V values = createValues(currentSource);

		new EditListener(values, currentSource);

		return values;
	}

	abstract V checkCreateEmptyValues();

	abstract V createValues(S currentSource);

	abstract S createSource(V values);

	abstract void addNewSource(S newSource);

	abstract void replaceSource(S oldSource, S newSource);

	abstract String getSourceTypeName();

	private boolean checkNewValueSelection(V values) {

		return new InitialValuesDialog(values, getSourceTypeName()).okSelected();
	}
}
