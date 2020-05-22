/**
 * Copyright (C) 2010, University of Manchester
 *
 * Bio Health Informatics Group
 */
package uk.ac.manchester.cs.goblin.gui;

/**
 * @author Colin Puleston
 */
abstract class ConstraintsListener {

	static ConstraintsListener INERT_LISTENER = new InertListener();

	static private class InertListener extends ConstraintsListener {

		void onConstraintChange() {
		}
	}

	abstract void onConstraintChange();
}
