package uk.ac.manchester.cs.goblin.io;

import java.io.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ModelSerialiser {

	private File dynamicFile;
	private Ontology ontology;

	private ConfigFileReader cfgReader = new ConfigFileReader();

	public ModelSerialiser() {

		dynamicFile = cfgReader.getDynamicFile();
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

		new DynamicModelRenderer(ontology, getDynamicNamespace()).write(model, dynamicFile);
	}

	public void saveAs(Model model, File file) {

		dynamicFile = file;

		save(model);
	}

	public File getDynamicFile() {

		return dynamicFile;
	}

	private Model load(Ontology ont) throws BadDynamicOntologyException {

		Model model = cfgReader.loadCoreModel(ont);

		new DynamicModelLoader(model, ont);

		return model;
	}

	private String getDynamicNamespace() {

		return cfgReader.getDynamicNamespace();
	}
}
