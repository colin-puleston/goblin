package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.ontology.*;

/**
 * @author Colin Puleston
 */
public class ConfigSerialiser {

	private ConfigFileLoader fileLoader = new ConfigFileLoader();

	private OntologyConfig ontologyConfig;
	private ModelConfig modelConfig;

	private ConfigOntology configOntology;

	public ConfigSerialiser() {

		ontologyConfig = fileLoader.loadOntologyConfig();

		Ontology coreOntology = new Ontology(ontologyConfig.getCoreFile());

		modelConfig = fileLoader.loadModelConfig(coreOntology);
		configOntology = new ConfigOntology(coreOntology);
	}

	public OntologyConfig getOntologyConfig() {

		return ontologyConfig;
	}

	public ModelConfig getModelConfig() {

		return modelConfig;
	}

	public ConfigOntology getConfigOntology() {

		return configOntology;
	}

	public void save() {

		ConfigFileRenderer fileRenderer = new ConfigFileRenderer();

		fileRenderer.renderOntologyConfig(ontologyConfig);
		fileRenderer.renderModelConfig(modelConfig);

		fileRenderer.writeToFile();
	}
}
