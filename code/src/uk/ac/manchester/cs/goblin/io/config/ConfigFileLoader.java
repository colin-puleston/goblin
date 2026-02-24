package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.ontology.*;

/**
 * @author Colin Puleston
 */
public class ConfigFileLoader extends ConfigFileSerialiser {

	private XNode rootNode;

	public ConfigFileLoader() {

		rootNode = new XDocument(getConfigFile()).getRootNode();
	}

	public OntologyConfig loadOntologyConfig() {

		return new OntologyConfig(getCoreFile(), getDynamicFile());
	}

	public ModelConfig loadModelConfig(Ontology ontology) {

		return new ModelConfigLoader(ontology).load(rootNode);
	}

	private File getCoreFile() {

		return getFileFromClasspath(rootNode.getString(CORE_FILENAME_ATTR));
	}

	private File getDynamicFile() {

		return getFileFromClasspath(rootNode.getString(DYNAMIC_FILENAME_ATTR));
	}
}
