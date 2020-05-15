package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class AutoConfirmations implements Confirmations {

	public boolean confirmConceptMove(List<Constraint> invalidatedConstraints) {

		return true;
	}

	public boolean confirmConstraintAddition(List<Constraint> conflicts) {

		return true;
	}
}
