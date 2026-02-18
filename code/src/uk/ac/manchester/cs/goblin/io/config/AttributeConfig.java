package uk.ac.manchester.cs.goblin.io.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public abstract class AttributeConfig {

	private String label;

	private EntityId rootSourceConceptId;
	private EntityId rootTargetConceptId;

	public String toString() {

		return getLabel() + "(" + rootSourceConceptId + " --> " + rootTargetConceptId + ")";
	}

	public String getLabel() {

		return label;
	}

	public EntityId getRootSourceConceptId() {

		return rootSourceConceptId;
	}

	public EntityId getRootTargetConceptId() {

		return rootTargetConceptId;
	}

	AttributeConfig(
		String label,
		EntityId rootSourceConceptId,
		EntityId rootTargetConceptId) {

		this.label = label;
		this.rootSourceConceptId = rootSourceConceptId;
		this.rootTargetConceptId = rootTargetConceptId;
	}

	Attribute createAttribute(Model model) {

		Concept rootSourceConcept = model.getConcept(rootSourceConceptId);
		Concept rootTargetConcept = model.getConcept(rootTargetConceptId);

		return createAttribute(label, rootSourceConcept, rootTargetConcept);
	}

	abstract Attribute createAttribute(
							String label,
							Concept rootSourceConcept,
							Concept rootTargetConcept);
}
