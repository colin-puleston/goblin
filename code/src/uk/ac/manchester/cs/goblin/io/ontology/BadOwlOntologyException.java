package uk.ac.manchester.cs.goblin.io.ontology;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.io.*;

/**
 * @author Colin Puleston
 */
public class BadOwlOntologyException extends BadStartupException {

	static private final long serialVersionUID = -1;

	BadOwlOntologyException(String message) {

		super("Bad OWL ontology: " + message);
	}

	BadOwlOntologyException(OWLException origin) {

		this(origin.getMessage());
	}
}
