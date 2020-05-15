package uk.ac.manchester.cs.goblin.io;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class AnchoredConstraintClassIRIs {

	static private final String NAME_FORMAT = "%s-Constraint-%d--%s-to-%s";

	private String dynamicNamespace;

	private Map<String, Integer> indexesByAnchorName = new HashMap<String, Integer>();

	AnchoredConstraintClassIRIs(String dynamicNamespace) {

		this.dynamicNamespace = dynamicNamespace;
	}

	IRI create(Constraint constraint, AnchoredConstraintType type) {

		return IRI.create(dynamicNamespace + '#' + createName(constraint, type));
	}

	private String createName(Constraint constraint, AnchoredConstraintType type) {

		String anchor = type.getAnchorConceptId().getLabel();
		String source = getConceptLabel(constraint.getSourceValue());
		String target = getConceptLabel(type.getRootTargetConcept());

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
