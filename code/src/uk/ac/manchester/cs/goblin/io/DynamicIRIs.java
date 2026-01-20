package uk.ac.manchester.cs.goblin.io;

import org.semanticweb.owlapi.model.*;

/**
 * @author Colin Puleston
 */
class DynamicIRIs {

	private String dynamicIRIPrefix;

	DynamicIRIs(String dynamicNamespace) {

		dynamicIRIPrefix = dynamicNamespace + '#';
	}

	boolean isDynamicIRI(IRI iri) {

		return iri.toString().startsWith(dynamicIRIPrefix);
	}

	IRI toDynamicIRI(String name) {

		return IRI.create(dynamicIRIPrefix + name);
	}

	String toDynamicNameOrNull(IRI iri) {

		return isDynamicIRI(iri) ? extractDynamicName(iri) : null;
	}

	private String extractDynamicName(IRI iri) {

		return iri.toString().substring(dynamicIRIPrefix.length());
	}
}
