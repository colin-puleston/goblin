package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

import uk.ac.manchester.cs.goblin.io.ontology.*;

/**
 * @author Colin Puleston
 */
public class ConfigFileLoader extends ConfigFileSerialiser {

	private XNode rootNode;

	public ConfigFileLoader() {

		rootNode = new XDocument(getConfigFile()).getRootNode();
	}

	public File getDynamicFile() {

		return getFileFromClasspath(rootNode.getString(DYNAMIC_FILENAME_ATTR));
	}

	public String getDynamicNamespace() {

		return rootNode.getString(DYNAMIC_NAMESPACE_ATTR);
	}

	public ModelConfig loadModelConfig(Ontology ontology) {

		return new ModelConfigLoader(ontology).load(rootNode);
	}
}
