package uk.ac.manchester.cs.goblin.io.model;

import java.io.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.*;
import uk.ac.manchester.cs.goblin.io.ontology.*;
import uk.ac.manchester.cs.goblin.io.config.*;

/**
 * @author Colin Puleston
 */
public class ModelSerialiser {

	private String projectName;

	private File dynamicOntologyFile;

	private Ontology ontology;
	private OntologyIds ontologyIds;

	private Model model;

	public ModelSerialiser(ProjectDir projectDir) throws BadStartupException {

		ConfigFileLoader configFileLoader = new ConfigFileLoader(projectDir);

		projectName = configFileLoader.getProjectName();
		dynamicOntologyFile = configFileLoader.getDynamicOntologyFile();

		ontology = new Ontology(dynamicOntologyFile);
		ontologyIds = new OntologyIds(ontology.getOntologyIRI());

		model = loadModel(configFileLoader);
	}

	public String getProjectName() {

		return projectName;
	}

	public File getDynamicOntologyFile() {

		return dynamicOntologyFile;
	}

	public Model getModel() {

		return model;
	}

	public void save() {

		new DynamicModelRenderer(ontology, ontologyIds).write(model, dynamicOntologyFile);
	}

	private Model loadModel(ConfigFileLoader configFileLoader) throws BadStartupException {

		ModelConfig modelConfig = configFileLoader.loadModelConfig(ontology);

		return new ModelLoader(modelConfig, ontology, ontologyIds).load();
	}
}
