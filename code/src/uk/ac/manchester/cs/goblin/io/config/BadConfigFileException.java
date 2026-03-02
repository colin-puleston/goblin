package uk.ac.manchester.cs.goblin.io.config;

/**
 * @author Colin Puleston
 */
public class BadConfigFileException extends Exception {

	static private final long serialVersionUID = -1;

	BadConfigFileException(RuntimeException origin) {

		super("Bad configuration file: " + origin.getMessage());

		origin.printStackTrace();
	}
}
