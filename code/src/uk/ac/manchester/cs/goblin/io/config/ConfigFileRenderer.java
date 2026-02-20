package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

import uk.ac.manchester.cs.goblin.ontology.*;

/**
 * @author Colin Puleston
 */
public class ConfigFileRenderer extends ConfigFileSerialiser {

	private DynamicIRIs dynamicIRIs;

	private XDocument document = new XDocument(ROOT_NODE_TAG);
	private XNode rootNode = document.getRootNode();

	public ConfigFileRenderer(DynamicIRIs dynamicIRIs) {

		this.dynamicIRIs = dynamicIRIs;
	}

	public void renderDynamicFilename(File file) {

		rootNode.setValue(DYNAMIC_FILENAME_ATTR, file.getName());
	}

	public void renderDynamicNamespace(String namespace) {

		rootNode.setValue(DYNAMIC_NAMESPACE_ATTR, namespace);
	}

	public void renderModelConfig(ModelConfig model) {

		new ModelConfigRenderer(rootNode, dynamicIRIs).render(model);
	}

	public void writeToFile() {

		document.writeToFile(getConfigFile());
	}
}
