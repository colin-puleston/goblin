package uk.ac.manchester.cs.goblin.io.config;

import uk.ac.manchester.cs.goblin.io.*;

/**
 * @author Colin Puleston
 */
class BadConfigException extends BadStartupException {

	static private final long serialVersionUID = -1;

	BadConfigException(String message) {

		super(message);
	}

	BadConfigException(RuntimeException origin) {

		super(origin);
	}
}
