package uk.ac.manchester.cs.goblin.model;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public class ModelEditLocation extends EditLocation {

	private Hierarchy hierarchy;
	private Concept concept = null;
	private Attribute attribute = null;

	public Hierarchy getEditedHierarchy() {

		return hierarchy;
	}

	public Concept getEditedConceptOrNull() {

		return concept;
	}

	public Attribute getEditedAttributeOrNull() {

		return attribute;
	}

	ModelEditLocation(Hierarchy hierarchy) {

		this.hierarchy = hierarchy;
	}

	ModelEditLocation(Concept concept) {

		this(concept.getHierarchy());

		this.concept = concept;
	}

	ModelEditLocation(Attribute attribute) {

		this(attribute.getRootSourceConcept());

		this.attribute = attribute;
	}
}
