package uk.ac.manchester.cs.goblin.io.config;

import java.io.*;

/**
 * @author Colin Puleston
 */
class BadOntologyFilepathException extends BadConfigException {

	static private final long serialVersionUID = -1;

	BadOntologyFilepathException(String ontologyRole, File file) {

		super(ontologyRole + " ontology file not found: " + file);
	}
}
