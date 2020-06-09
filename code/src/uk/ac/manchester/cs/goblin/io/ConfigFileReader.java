package uk.ac.manchester.cs.goblin.io;

import java.io.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.config.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class ConfigFileReader extends ConfigFileVocab {

	static private final String CONFIG_FILE_NAME = "goblin.xml";

	private KConfigNode rootNode;

	ConfigFileReader() {

		rootNode = new KConfigFile(CONFIG_FILE_NAME).getRootNode();
	}

	File getDynamicFile() {

		return rootNode.getResource(DYNAMIC_FILE_ATTR, KConfigResourceFinder.FILES);
	}

	String getDynamicNamespace() {

		return rootNode.getString(DYNAMIC_NAMESPACE_ATTR);
	}

	Model loadCoreModel(Ontology ontology) {

		Model model = new Model(getDynamicNamespace());

		new CoreModelLoader(model, ontology).load(rootNode);

		return model;
	}
}