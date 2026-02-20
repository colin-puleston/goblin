package uk.ac.manchester.cs.goblin.ontology;

import org.semanticweb.owlapi.model.*;

/**
 * @author Colin Puleston
 */
public class DynamicIRIs {

	private String dynamicIRIPrefix;

	public DynamicIRIs(String dynamicNamespace) {

		dynamicIRIPrefix = dynamicNamespace + '#';
	}

	public boolean isDynamicIRI(IRI iri) {

		return iri.toString().startsWith(dynamicIRIPrefix);
	}

	public IRI toDynamicIRI(String name) {

		return IRI.create(dynamicIRIPrefix + name);
	}

	String toDynamicNameOrNull(IRI iri) {

		return isDynamicIRI(iri) ? extractDynamicName(iri) : null;
	}

	private String extractDynamicName(IRI iri) {

		return iri.toString().substring(dynamicIRIPrefix.length());
	}
}
