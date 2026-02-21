package uk.ac.manchester.cs.goblin.io.model;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.config.*;
import uk.ac.manchester.cs.goblin.io.ontology.*;

/**
 * @author Colin Puleston
 */
class AnchoredConstraintClassIRIs {

	static private final String NAME_FORMAT = "%s-Constraint-%d--%s-to-%s";

	private OntologyIds ontologyIds;

	private Map<String, Integer> indexesByAnchorName = new HashMap<String, Integer>();

	AnchoredConstraintClassIRIs(OntologyIds ontologyIds) {

		this.ontologyIds = ontologyIds;
	}

	IRI create(AnchoredAttributeConfig attributeConfig, Constraint constraint) {

		return ontologyIds.toDynamicIRI(createName(attributeConfig, constraint));
	}

	private String createName(AnchoredAttributeConfig attributeConfig, Constraint constraint) {

		String anchor = attributeConfig.getAnchorConceptId().getLabel();
		String source = constraint.getSourceValue().getConceptId().getLabel();
		String target = attributeConfig.getRootTargetConceptId().getLabel();

		int index = nextIndex(anchor);

		return String.format(NAME_FORMAT, anchor, index, source, target);
	}

	private int nextIndex(String anchorName) {

		Integer index = indexesByAnchorName.get(anchorName);

		if (index == null) {

			index = 0;
		}

		indexesByAnchorName.put(anchorName, ++index);

		return index;
	}

	private String getConceptLabel(Concept concept) {

		return concept.getConceptId().getLabel();
	}
}
