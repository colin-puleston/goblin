package uk.ac.manchester.cs.goblin.config;

import java.io.*;

/**
 * @author Colin Puleston
 */
public class OntologyConfig {

	private File coreFile;
	private File dynamicFile;

	public OntologyConfig(File coreFile, File dynamicFile) {

		this.coreFile = coreFile;
		this.dynamicFile = dynamicFile;
	}

	public File getCoreFile() {

		return coreFile;
	}

	public File getDynamicFile() {

		return dynamicFile;
	}
}
