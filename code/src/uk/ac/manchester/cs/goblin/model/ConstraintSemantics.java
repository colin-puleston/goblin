package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public enum ConstraintSemantics {

	VALID_VALUES, IMPLIED_VALUE;

	public boolean validValues() {

		return this == VALID_VALUES;
	}

	public boolean impliedValue() {

		return this == IMPLIED_VALUE;
	}

	public String getDisplayLabel() {

		String s = toString();
		String d = "";

		d += s.charAt(0);
		d += s.substring(1).toLowerCase().replaceAll("_", "-");

		return d;
	}

	public List<Constraint> select(Collection<Constraint> candidates) {

		List<Constraint> selections = new ArrayList<Constraint>();

		for (Constraint candidate : candidates) {

			if (candidate.hasSemantics(this)) {

				selections.add(candidate);
			}
		}

		return selections;
	}
}
