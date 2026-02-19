package uk.ac.manchester.cs.goblin.model;

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
