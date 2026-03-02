package uk.ac.manchester.cs.goblin.io.model;

import java.io.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.ontology.*;
import uk.ac.manchester.cs.goblin.io.config.*;

/**
 * @author Colin Puleston
 */
public class ModelSerialiser {

	private File dynamicFile;
	private Ontology ontology;
	private OntologyIds ontologyIds;

	private Model model;

	public ModelSerialiser() throws BadConfigFileException, BadDynamicOntologyException {

		ConfigFileLoader configFileLoader = new ConfigFileLoader();

		dynamicFile = configFileLoader.loadOntologyConfig().getDynamicFile();
		ontology = new Ontology(dynamicFile);
		ontologyIds = new OntologyIds(ontology.getOntologyIRI());

		model = load(configFileLoader);
	}

	public void save() {

		new DynamicModelRenderer(ontology, ontologyIds).write(model, dynamicFile);
	}

	public File getDynamicFile() {

		return dynamicFile;
	}

	public Model getModel() {

		return model;
	}

	private Model load(ConfigFileLoader configFileLoader) throws BadDynamicOntologyException {

		ModelConfig modelConfig = configFileLoader.loadModelConfig(ontology);

		return new ModelLoader(modelConfig, ontology, ontologyIds).load();
	}
}
