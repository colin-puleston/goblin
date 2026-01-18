package uk.ac.manchester.cs.goblin.io;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class EntityIds {

	private DynamicIRIs dynamicIRIs;

	EntityIds(DynamicIRIs dynamicIRIs) {

		this.dynamicIRIs = dynamicIRIs;
	}

	EntityId getId(OWLEntity entity, String label) {

		IRI iri = entity.getIRI();
		String dynName = toDynamicNameOrNull(iri);

		return dynName != null ? new DynamicId(dynName, label) : new CoreId(iri, label);
	}

	IRI toIRI(EntityId id) {

		if (id instanceof CoreId) {

			return ((CoreId)id).getIRI();
		}

		return dynamicIRIs.toDynamicIRI(id.getName());
	}

	private String toDynamicNameOrNull(IRI iri) {

		return dynamicIRIs.toDynamicNameOrNull(iri);
	}
}
