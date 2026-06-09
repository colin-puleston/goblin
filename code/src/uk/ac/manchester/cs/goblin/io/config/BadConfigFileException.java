package uk.ac.manchester.cs.goblin.io.config;

/**
 * @author Colin Puleston
 */
class BadConfigFileException extends BadConfigException {

	static private final long serialVersionUID = -1;

	BadConfigFileException(RuntimeException origin) {

		super("Bad configuration file: " + origin.getMessage());
	}
}
