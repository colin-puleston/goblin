package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;

import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.*;
import uk.ac.manchester.cs.goblin.io.ontology.*;

/**
 * @author Colin Puleston
 */
public class ConfigFileLoader extends ConfigFileVocab {

	private ProjectDir projectDir;
	private XNode rootNode;

	public ConfigFileLoader(ProjectDir projectDir) throws BadConfigException {

		this.projectDir = projectDir;

		try {

			rootNode = new XDocument(projectDir.getConfigFile()).getRootNode();
		}
		catch (XDocumentException e) {

			throw new BadConfigFileException(e);
		}
	}

	public String getProjectName() throws BadConfigException {

		return getValue(PROJECT_NAME_ATTR);
	}

	public File getCoreOntologyFile() throws BadConfigException {

		return getOntologyFile("Core", CORE_FILENAME_ATTR);
	}

	public File getDynamicOntologyFile() throws BadConfigException {

		return getOntologyFile("Ontology", DYNAMIC_FILENAME_ATTR);
	}

	public ModelConfig loadModelConfig(Ontology ontology) throws BadConfigException {

		return new ModelConfigLoader(ontology).load(rootNode);
	}

	private File getOntologyFile(String role, String attr) throws BadConfigException {

		File file = projectDir.getFile(getValue(attr));

		if (!file.exists()) {

			throw new BadOntologyFilepathException(role, file);
		}

		return file;
	}

	private String getValue(String attr) throws BadConfigException {

		try {

			return rootNode.getString(attr);
		}
		catch (XDocumentException e) {

			throw new BadConfigFileException(e);
		}
	}
}
