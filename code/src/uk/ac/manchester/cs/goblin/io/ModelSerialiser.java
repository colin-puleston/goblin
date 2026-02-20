package uk.ac.manchester.cs.goblin.io;

import java.io.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.ontology.*;
import uk.ac.manchester.cs.goblin.io.config.*;

/**
 * @author Colin Puleston
 */
public class ModelSerialiser {

	private ConfigFileLoader configFileLoader;

	private File dynamicFile;
	private EntityIds entityIds;

	private Ontology ontology;

	public ModelSerialiser() {

		configFileLoader = new ConfigFileLoader();

		dynamicFile = configFileLoader.getDynamicFile();
		entityIds = new EntityIds(configFileLoader.getDynamicNamespace());

		ontology = new Ontology(dynamicFile);
	}

	public Model load() throws BadDynamicOntologyException {

		return load(ontology);
	}

	public Model loadFrom(File file) throws BadDynamicOntologyException {

		Ontology ont = new Ontology(file);
		Model model = load(ont);

		dynamicFile = file;
		ontology = ont;

		return model;
	}

	public void save(Model model) {

		new DynamicModelRenderer(ontology, entityIds).write(model, dynamicFile);
	}

	public void saveAs(Model model, File file) {

		dynamicFile = file;

		save(model);
	}

	public File getDynamicFile() {

		return dynamicFile;
	}

	private Model load(Ontology ont) throws BadDynamicOntologyException {

		ModelConfig modelConfig = configFileLoader.loadModelConfig(ont);

		return new ModelLoader(ont, modelConfig, entityIds).load();
	}
}
