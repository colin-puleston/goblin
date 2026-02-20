package uk.ac.manchester.cs.goblin.ontology;

import org.semanticweb.owlapi.model.*;

/**
 * @author Colin Puleston
 */
class DynamicIRIs {

	private String dynamicIRIPrefix;

	DynamicIRIs(String dynamicNamespace) {

		dynamicIRIPrefix = dynamicNamespace + '#';
	}

	IRI toDynamicIRI(String name) {

		return IRI.create(dynamicIRIPrefix + name);
	}

	String toDynamicNameOrNull(IRI iri) {

		return isDynamicIRI(iri) ? extractDynamicName(iri) : null;
	}

	private boolean isDynamicIRI(IRI iri) {

		return iri.toString().startsWith(dynamicIRIPrefix);
	}

	private String extractDynamicName(IRI iri) {

		return iri.toString().substring(dynamicIRIPrefix.length());
	}
}
