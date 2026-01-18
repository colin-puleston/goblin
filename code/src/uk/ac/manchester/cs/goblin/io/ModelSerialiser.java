package uk.ac.manchester.cs.goblin.io;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.config.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ModelSerialiser extends ConfigFileVocab {

	static private final String CONFIG_FILE_NAME = "goblin.xml";

	private KConfigNode configRootNode;

	private File dynamicFile;
	private DynamicIRIs dynamicIRIs;

	private Ontology ontology;

	public ModelSerialiser() {

		configRootNode = new KConfigFile(CONFIG_FILE_NAME).getRootNode();

		dynamicFile = getDynamicFileFromConfig();
		dynamicIRIs = new DynamicIRIs(getDynamicNamespaceFromConfig());

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

		new DynamicModelRenderer(ontology, dynamicIRIs).write(model, dynamicFile);
	}

	public void saveAs(Model model, File file) {

		dynamicFile = file;

		save(model);
	}

	public File getDynamicFile() {

		return dynamicFile;
	}

	private Model load(Ontology ont) throws BadDynamicOntologyException {

		Model model = new Model();

		new CoreModelLoader(model, ont).load(configRootNode);
		new DynamicModelLoader(model, ont, dynamicIRIs);

		return model;
	}

	private File getDynamicFileFromConfig() {

		return configRootNode.getResource(DYNAMIC_FILE_ATTR, KConfigResourceFinder.FILES);
	}

	private String getDynamicNamespaceFromConfig() {

		return configRootNode.getString(DYNAMIC_NAMESPACE_ATTR);
	}
}
