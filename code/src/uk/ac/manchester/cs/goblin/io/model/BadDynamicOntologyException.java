package uk.ac.manchester.cs.goblin.io.model;

import uk.ac.manchester.cs.goblin.io.*;

/**
 * @author Colin Puleston
 */
class BadDynamicOntologyException extends BadStartupException {

	static private final long serialVersionUID = -1;

	BadDynamicOntologyException(RuntimeException origin) {

		super("Bad dynamic-ontology: " + origin.getMessage());
	}
}
