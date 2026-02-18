package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.config.*;

import uk.ac.manchester.cs.goblin.io.ontology.*;

/**
 * @author Colin Puleston
 */
public class ConfigFileLoader extends ConfigFileVocab {

	static private final String CONFIG_FILE_NAME = "goblin.xml";

	private KConfigNode configRootNode;

	public ConfigFileLoader() {

		configRootNode = new KConfigFile(CONFIG_FILE_NAME).getRootNode();
	}

	public File getDynamicFile() {

		return configRootNode.getResource(DYNAMIC_FILE_ATTR, KConfigResourceFinder.FILES);
	}

	public String getDynamicNamespace() {

		return configRootNode.getString(DYNAMIC_NAMESPACE_ATTR);
	}

	public ModelConfig loadModelConfig(Ontology ontology) {

		return new ModelConfigLoader(ontology).load(configRootNode);
	}
}
