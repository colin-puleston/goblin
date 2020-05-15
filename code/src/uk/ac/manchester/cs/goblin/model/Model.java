package uk.ac.manchester.cs.goblin.model;

import java.net.*;
import java.util.*;

/**
 * @author Colin Puleston
 */
public class Model {

	private String dynamicNamespace;

	private List<Hierarchy> hierarchies = new ArrayList<Hierarchy>();

	private EditActions editActions;
	private ConceptTracking conceptTracking;
	private ConstraintTracking constraintTracking;
	private ConflictResolver conflictResolver;

	public Model(String dynamicNamespace) {

		this.dynamicNamespace = dynamicNamespace;

		editActions = new EditActions();
		conceptTracking = new ConceptTracking();
		constraintTracking = new ConstraintTracking();
		conflictResolver = new ConflictResolver();
	}

	public void setConfirmations(Confirmations confirmations) {

		conflictResolver.setConfirmations(confirmations);
	}

	public void startEditTracking() {

		editActions.startTracking();
	}

	public void addEditListener(ModelEditListener listener) {

		editActions.addListener(listener);
	}

	public boolean canUndo() {

		return editActions.canUndo();
	}

	public boolean canRedo() {

		return editActions.canRedo();
	}

	public EditLocation undo() {

		return editActions.undo();
	}

	public EditLocation redo() {

		return editActions.redo();
	}

	public Hierarchy addHierarchy(EntityId rootConceptId) {

		Hierarchy hierarchy = new Hierarchy(this, rootConceptId);

		hierarchies.add(hierarchy);

		return hierarchy;
	}

	public EntityId createEntityId(URI uri, String labelOrNull) {

		if (hasDynamicNamespace(uri)) {

			return toEntityId(createDynamicId(uri, labelOrNull));
		}

		return new EntityId(uri, labelOrNull);
	}

	public List<Hierarchy> getHierarchies() {

		return new ArrayList<Hierarchy>(hierarchies);
	}

	public Hierarchy getHierarchy(EntityId rootConceptId) {

		for (Hierarchy hierarchy : hierarchies) {

			if (hierarchy.hasRootConcept(rootConceptId)) {

				return hierarchy;
			}
		}

		throw new RuntimeException("Not root-concept: " + rootConceptId);
	}

	public Concept getConcept(EntityId conceptId) {

		Concept concept = lookForConcept(conceptId);

		if (concept != null) {

			return concept;
		}

		throw new RuntimeException("Cannot find concept: " + conceptId);
	}

	public boolean dynamicConceptExists(DynamicId dynamicId) {

		return conceptExists(toEntityId(dynamicId));
	}

	boolean canResetDynamicConceptId(Concept concept, DynamicId newDynamicId) {

		EntityId newId = toEntityId(newDynamicId);

		return concept.getConceptId().equals(newId) || !conceptExists(newId);
	}

	EntityId toEntityId(DynamicId dynamicId) {

		return dynamicId.toEntityId(dynamicNamespace);
	}

	EditActions getEditActions() {

		return editActions;
	}

	ConceptTracking getConceptTracking() {

		return conceptTracking;
	}

	ConstraintTracking getConstraintTracking() {

		return constraintTracking;
	}

	ConflictResolver getConflictResolver() {

		return conflictResolver;
	}

	private Concept lookForConcept(EntityId conceptId) {

		for (Hierarchy hierarchy : hierarchies) {

			if (hierarchy.hasConcept(conceptId)) {

				return hierarchy.getConcept(conceptId);
			}
		}

		return null;
	}

	private boolean conceptExists(EntityId conceptId) {

		return lookForConcept(conceptId) != null;
	}

	private boolean hasDynamicNamespace(URI uri) {

		return uri.toString().startsWith(dynamicNamespace + '#');
	}

	private DynamicId createDynamicId(URI uri, String labelOrNull) {

		String name = uri.getFragment();

		return labelOrNull != null
					? new DynamicId(name, labelOrNull)
					: DynamicId.fromName(name);
	}
}
