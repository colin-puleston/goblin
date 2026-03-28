package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public enum ConstraintsOption {

	VALID_VALUES_ONLY(true, false, false),
	SINGLE_IMPLIED_VALUES_ONLY(false, true, false),
	MULTI_IMPLIED_VALUES_ONLY(false, false, true),
	VALID_AND_SINGLE_IMPLIED_VALUES(true, true, false),
	VALID_AND_MULTI_IMPLIED_VALUES(true, false, true),
	NONE(false, false, false);

	static public ConstraintsOption[] coreAttributeOptions() {

		ConstraintsOption[] all = values();

		return Arrays.copyOfRange(all, 0, all.length - 1);
	}

	private boolean validValues;
	private boolean singleImpliedValues;
	private boolean multiImpliedValues;

	public boolean validValues() {

		return validValues;
	}

	public boolean singleImpliedValues() {

		return singleImpliedValues;
	}

	public boolean multiImpliedValues() {

		return multiImpliedValues;
	}

	public boolean impliedValues() {

		return singleImpliedValues() || multiImpliedValues();
	}

	ConstraintsOption(boolean valids, boolean singleImplieds, boolean multiImplieds) {

		validValues = valids;
		singleImpliedValues = singleImplieds;
		multiImpliedValues = multiImplieds;
	}
}
