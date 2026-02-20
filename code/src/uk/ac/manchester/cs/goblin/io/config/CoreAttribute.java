package uk.ac.manchester.cs.goblin.io.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class CoreAttribute extends Attribute {

	private CoreAttributeConfig config;

	public String getLabel() {

		return config.getLabel();
	}

	public ConstraintsOption getConstraintsOption() {

		return config.getConstraintsOption();
	}

	public CoreAttributeConfig getConfig() {

		return config;
	}

	CoreAttribute(Model model, CoreAttributeConfig config) {

		super(
			model.getConcept(config.getRootSourceConceptId()),
			model.getConcept(config.getRootTargetConceptId()));

		this.config = config;
	}
}
