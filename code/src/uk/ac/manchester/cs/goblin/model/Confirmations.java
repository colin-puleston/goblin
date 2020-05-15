package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public interface Confirmations {

	public boolean confirmConceptMove(List<Constraint> invalidatedConstraints);

	public boolean confirmConstraintAddition(List<Constraint> conflicts);
}
