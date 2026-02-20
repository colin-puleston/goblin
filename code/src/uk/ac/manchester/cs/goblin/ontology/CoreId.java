package uk.ac.manchester.cs.goblin.ontology;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class CoreId extends EntityId {

	static private final char[] IRI_FINAL_SEPARATOR_CHARS = new char[]{'#', '/', ':'};

	static private String extractName(IRI iri) {

		String i = iri.toString();

		for (char c : IRI_FINAL_SEPARATOR_CHARS) {

			int s = i.lastIndexOf(c);

			if (s != -1 && s != i.length() - 1) {

				return i.substring(s + 1);
			}
		}

		return i;
	}

	private IRI iri;

	public CoreId(IRI iri) {

		this(iri, null);
	}

	public CoreId(IRI iri, String labelOrNull) {

		super(extractName(iri), labelOrNull);

		this.iri = iri;
	}

	public IRI getIRI() {

		return iri;
	}

	public boolean equals(Object other) {

		return other instanceof CoreId && iri.equals(((CoreId)other).iri);
	}

	public int hashCode() {

		return iri.hashCode();
	}

	public boolean dynamicId() {

		return false;
	}
}
