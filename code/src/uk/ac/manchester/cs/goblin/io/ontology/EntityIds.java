package uk.ac.manchester.cs.goblin.io.ontology;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class EntityIds {

	private DynamicIRIs dynamicIRIs;

	public EntityIds(DynamicIRIs dynamicIRIs) {

		this.dynamicIRIs = dynamicIRIs;
	}

	public EntityId getId(OWLEntity entity, String label) {

		IRI iri = entity.getIRI();
		String dynName = toDynamicNameOrNull(iri);

		return dynName != null ? new DynamicId(dynName, label) : new CoreId(iri, label);
	}

	public IRI toIRI(EntityId id) {

		IRI iri = toCoreIRIOrNull(id);

		if (iri == null) {

			iri = toDynamicIRIOrNull(id);
		}

		if (iri != null) {

			return iri;
		}

		throw new Error("Unrecognised EntityId type: " + id.getClass());
	}

	public IRI toCoreIRI(EntityId id) {

		IRI iri = toCoreIRIOrNull(id);

		if (iri != null) {

			return iri;
		}

		throw new RuntimeException("Unexpected dynamic entity: " + id);
	}

	public IRI toDynamicIRI(EntityId id) {

		IRI iri = toDynamicIRIOrNull(id);

		if (iri != null) {

			return iri;
		}

		throw new RuntimeException("Unexpected non-dynamic entity: " + id);
	}

	private String toDynamicNameOrNull(IRI iri) {

		return dynamicIRIs.toDynamicNameOrNull(iri);
	}

	private IRI toCoreIRIOrNull(EntityId id) {

		return id instanceof CoreId ? ((CoreId)id).getIRI() : null;
	}

	private IRI toDynamicIRIOrNull(EntityId id) {

		return id instanceof DynamicId ? toDynamicIRI((DynamicId)id) : null;
	}

	private IRI toDynamicIRI(DynamicId id) {

		return dynamicIRIs.toDynamicIRI(id.getName());
	}
}
