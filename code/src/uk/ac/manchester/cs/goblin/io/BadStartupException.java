package uk.ac.manchester.cs.goblin.io;

/**
 * @author Colin Puleston
 */
public class BadStartupException extends Exception {

	static private final long serialVersionUID = -1;

	protected BadStartupException(String message) {

		super(message);
	}

	protected BadStartupException(RuntimeException origin) {

		super(origin.getMessage());
	}
}
