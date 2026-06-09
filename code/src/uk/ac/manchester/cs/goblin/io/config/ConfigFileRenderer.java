package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.*;

/**
 * @author Colin Puleston
 */
class ConfigFileRenderer extends ConfigFileVocab {

	private ProjectDir projectDir;

	private XDocument document = new XDocument(ROOT_NODE_TAG);
	private XNode rootNode = document.getRootNode();

	ConfigFileRenderer(ProjectDir projectDir) {

		this.projectDir = projectDir;
	}

	void renderCoreOntologyFile(File file) {

		rootNode.setValue(CORE_FILENAME_ATTR, file.getName());
	}

	void renderDynamicOntologyFile(File file) {

		rootNode.setValue(DYNAMIC_FILENAME_ATTR, file.getName());
	}

	void renderModelConfig(ModelConfig model) {

		new ModelConfigRenderer(rootNode).render(model);
	}

	void writeToFile() {

		document.writeToFile(projectDir.getConfigFile());
	}
}
