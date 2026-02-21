package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.ontology.*;

/**
 * @author Colin Puleston
 */
class ConfigFileRenderer extends ConfigFileSerialiser {

	private OntologyIds ontologyIds;

	private XDocument document = new XDocument(ROOT_NODE_TAG);
	private XNode rootNode = document.getRootNode();

	ConfigFileRenderer(OntologyIds ontologyIds) {

		this.ontologyIds = ontologyIds;
	}

	void renderDynamicFilename(File file) {

		rootNode.setValue(DYNAMIC_FILENAME_ATTR, file.getName());
	}

	void renderDynamicNamespace(String namespace) {

		rootNode.setValue(DYNAMIC_NAMESPACE_ATTR, namespace);
	}

	void renderModelConfig(ModelConfig model) {

		new ModelConfigRenderer(rootNode, ontologyIds).render(model);
	}

	void writeToFile() {

		document.writeToFile(getConfigFile());
	}
}
