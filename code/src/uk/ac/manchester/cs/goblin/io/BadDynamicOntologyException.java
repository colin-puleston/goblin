package uk.ac.manchester.cs.goblin.io;

/**
 * @author Colin Puleston
 */
public class BadDynamicOntologyException extends Exception {

	static private final long serialVersionUID = -1;

	BadDynamicOntologyException(RuntimeException origin) {

		super("Bad dynamic-ontology: " + origin.getMessage());

		origin.printStackTrace();
	}
}
