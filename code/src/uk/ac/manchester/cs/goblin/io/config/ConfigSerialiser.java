package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.*;
import uk.ac.manchester.cs.goblin.io.ontology.*;

/**
 * @author Colin Puleston
 */
public class ConfigSerialiser {

	private ProjectDir projectDir;

	private String projectName;
	private File coreOntologyFile;
	private File dynamicOntologyFile;

	private ConfigOntology configOntology;

	private ModelConfig modelConfig;

	public ConfigSerialiser(ProjectDir projectDir) throws BadStartupException {

		this.projectDir = projectDir;

		ConfigFileLoader fileLoader = new ConfigFileLoader(projectDir);

		projectName = fileLoader.getProjectName();
		coreOntologyFile = fileLoader.getCoreOntologyFile();
		dynamicOntologyFile = fileLoader.getDynamicOntologyFile();

		Ontology coreOntology = new Ontology(coreOntologyFile);

		modelConfig = fileLoader.loadModelConfig(coreOntology);
		configOntology = new ConfigOntology(coreOntology);
	}

	public String getProjectName() {

		return projectName;
	}

	public File getDynamicOntologyFile() {

		return dynamicOntologyFile;
	}

	public ConfigOntology getConfigOntology() {

		return configOntology;
	}

	public ModelConfig getModelConfig() {

		return modelConfig;
	}

	public void save() {

		ConfigFileRenderer fileRenderer = new ConfigFileRenderer(projectDir);

		fileRenderer.renderProjectName(projectName);
		fileRenderer.renderCoreOntologyFile(coreOntologyFile);
		fileRenderer.renderDynamicOntologyFile(dynamicOntologyFile);
		fileRenderer.renderModelConfig(modelConfig);

		fileRenderer.writeToFile();
	}
}
