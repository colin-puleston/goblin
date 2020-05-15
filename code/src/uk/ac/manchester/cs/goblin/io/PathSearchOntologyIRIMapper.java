package uk.ac.manchester.cs.goblin.io;

import java.io.*;

import org.semanticweb.owlapi.model.*;

/**
 * @author Colin Puleston
 */
class PathSearchOntologyIRIMapper implements OWLOntologyIRIMapper {

	static private final long serialVersionUID = -1;

	static private final String URL_SEPARATOR = "/";
	static private final String URN_SEPARATOR = ":";
	static private final String OWL_EXTENSION = ".owl";

	private File leafDirectory;

	public IRI getDocumentIRI(IRI iri) {

		File file = lookForFile(leafDirectory, guessFileName(iri));

		return file != null ? IRI.create(file) : iri;
	}

	PathSearchOntologyIRIMapper(File leafDirectory) {

		this.leafDirectory = leafDirectory;
	}

	private String guessFileName(IRI iri) {

		String path = iri.toString();
		int lastDiv = path.lastIndexOf(URL_SEPARATOR);

		if (lastDiv == -1) {

			lastDiv = path.lastIndexOf(URN_SEPARATOR);
		}

		path = path.substring(lastDiv + 1);

		if (!path.endsWith(OWL_EXTENSION)) {

			path += OWL_EXTENSION;
		}

		return path;
	}

	private File lookForFile(File dir, String fileName) {

		File file = getFileOrNull(dir, fileName);

		if (file != null) {

			return file;
		}

		File parentDir = dir = dir.getParentFile();

		return parentDir != null ? lookForFile(parentDir, fileName) : null;
	}

	private File getFileOrNull(File dir, String fileName) {

		File caseFreeMatch = null;

		for (File file : dir.listFiles()) {

			String name = file.getName();

			if (name.equals(fileName)) {

				return file;
			}

			if (name.toUpperCase().equals(fileName.toUpperCase())) {

				caseFreeMatch = file;
			}
		}

		return caseFreeMatch;
	}
}