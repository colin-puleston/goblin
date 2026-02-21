package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.ontology.*;

/**
 * @author Colin Puleston
 */
class ConfigFileLoader extends ConfigFileSerialiser {

	private XNode rootNode;

	ConfigFileLoader() {

		rootNode = new XDocument(getConfigFile()).getRootNode();
	}

	File getDynamicFile() {

		return getFileFromClasspath(rootNode.getString(DYNAMIC_FILENAME_ATTR));
	}

	String getDynamicNamespace() {

		return rootNode.getString(DYNAMIC_NAMESPACE_ATTR);
	}

	ModelConfig loadModelConfig(Ontology ontology) {

		return new ModelConfigLoader(ontology).load(rootNode);
	}
}
