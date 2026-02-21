package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.ontology.*;

/**
 * @author Colin Puleston
 */
public class ConfigSerialiser {

	private File dynamicFile;
	private String dynamicNamespace;

	private ConfigFileLoader fileLoader;

	public ConfigSerialiser() {

		fileLoader = new ConfigFileLoader();

		dynamicFile = fileLoader.getDynamicFile();
		dynamicNamespace = fileLoader.getDynamicNamespace();
	}

	public File getDynamicFile() {

		return dynamicFile;
	}

	public String getDynamicNamespace() {

		return dynamicNamespace;
	}

	public ModelConfig loadModelConfig() {

		return loadModelConfig(new Ontology(dynamicFile));
	}

	public ModelConfig loadModelConfig(Ontology ontology) {

		return fileLoader.loadModelConfig(ontology);
	}

	public void save(ModelConfig modelConfig) {

		ConfigFileRenderer fileRenderer = createFileRenderer();

		fileRenderer.renderDynamicFilename(dynamicFile);
		fileRenderer.renderDynamicNamespace(dynamicNamespace);
		fileRenderer.renderModelConfig(modelConfig);

		fileRenderer.writeToFile();
	}

	private ConfigFileRenderer createFileRenderer() {

		return new ConfigFileRenderer(new OntologyIds(dynamicNamespace));
	}
}
