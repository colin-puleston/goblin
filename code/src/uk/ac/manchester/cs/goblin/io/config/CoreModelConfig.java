package uk.ac.manchester.cs.goblin.io.config;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class CoreModelConfig {

	private List<ModelSectionConfig> sections = new ArrayList<ModelSectionConfig>();

	public void addSection(ModelSectionConfig section) {

		sections.add(section);
	}

	public List<ModelSectionConfig> getSections() {

		return new ArrayList<ModelSectionConfig>(sections);
	}
}
