package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

import uk.ac.manchester.cs.goblin.config.*;

/**
 * @author Colin Puleston
 */
class ConfigFileRenderer extends ConfigFileSerialiser {

	private XDocument document = new XDocument(ROOT_NODE_TAG);
	private XNode rootNode = document.getRootNode();

	void renderOntologyConfig(OntologyConfig ontologyConfig) {

		renderCoreFilename(ontologyConfig.getCoreFile());
		renderDynamicFilename(ontologyConfig.getDynamicFile());
		renderDynamicNamespace(ontologyConfig.getDynamicNamespace());
	}

	void renderModelConfig(ModelConfig model) {

		new ModelConfigRenderer(rootNode).render(model);
	}

	void writeToFile() {

		document.writeToFile(getConfigFile());
	}

	private void renderCoreFilename(File file) {

		rootNode.setValue(CORE_FILENAME_ATTR, file.getName());
	}

	private void renderDynamicFilename(File file) {

		rootNode.setValue(DYNAMIC_FILENAME_ATTR, file.getName());
	}

	private void renderDynamicNamespace(String namespace) {

		rootNode.setValue(DYNAMIC_NAMESPACE_ATTR, namespace);
	}
}
