package uk.ac.manchester.cs.goblin.io;

import org.semanticweb.owlapi.model.*;

/**
 * @author Colin Puleston
 */
class DynamicIRIs {

	private String dynamicNamespace;

	DynamicIRIs(String dynamicNamespace) {

		this.dynamicNamespace = dynamicNamespace;
	}

	IRI toDynamicIRI(String name) {

		return IRI.create(dynamicNamespace + '#' + name);
	}

	String toDynamicNameOrNull(IRI iri) {

		String i = iri.toString();

		if (i.startsWith(dynamicNamespace + '#')) {

			return i.substring(dynamicNamespace.length() + 1);
		}

		return null;
	}
}
