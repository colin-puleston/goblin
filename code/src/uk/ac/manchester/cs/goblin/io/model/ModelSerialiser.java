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

	private ConfigSerialiser configSerialiser = new ConfigSerialiser();

	public ModelSerialiser() {

		dynamicFile = configSerialiser.getDynamicFile();
		ontology = new Ontology(dynamicFile);
		ontologyIds = new OntologyIds(configSerialiser.getDynamicNamespace());
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

		new DynamicModelRenderer(ontology, ontologyIds).write(model, dynamicFile);
	}

	public void saveAs(Model model, File file) {

		dynamicFile = file;

		save(model);
	}

	public File getDynamicFile() {

		return dynamicFile;
	}

	private Model load(Ontology ont) throws BadDynamicOntologyException {

		ModelConfig modelConfig = configSerialiser.loadModelConfig(ont);

		return new ModelLoader(modelConfig, ont, ontologyIds).load();
	}
}
