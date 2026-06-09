package uk.ac.manchester.cs.goblin.io;

import java.io.*;

/**
 * @author Colin Puleston
 */
public class BadProjectDirException extends Exception {

	static private final long serialVersionUID = -1;

	BadProjectDirException(String message) {

		super(message);
	}
}
