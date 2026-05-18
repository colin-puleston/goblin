package uk.ac.manchester.cs.goblin.model;

import java.net.*;
import java.util.*;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public class Model extends CoreHierarchyContainer {

	private List<ModelSection> sections = new ArrayList<ModelSection>();

	private ModelEditActions editActions = new ModelEditActions();
	private ConceptTracking conceptTracking = new ConceptTracking();
	private ConflictResolver conflictResolver = new ConflictResolver();

	public void addSection(ModelSection section) {

		sections.add(section);
	}

	public void setConfirmations(Confirmations confirmations) {

		conflictResolver.setConfirmations(confirmations);
	}

	public Hierarchy createDynamicValueHierarchy(EntityId rootConceptId) {

		return new DynamicValueHierarchy(this, rootConceptId);
	}

	public List<ModelSection> getSections() {

		return new ArrayList<ModelSection>(sections);
	}

	public ModelEditActions getEditActions() {

		return editActions;
	}

	ConceptTracking getConceptTracking() {

		return conceptTracking;
	}

	ConflictResolver getConflictResolver() {

		return conflictResolver;
	}
}
