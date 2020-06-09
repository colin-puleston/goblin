package uk.ac.manchester.cs.goblin.model;

import java.net.*;
import java.util.*;

/**
 * @author Colin Puleston
 */
public class Model extends HierarchyContainer {

	static private final String DEFAULT_SECTION_NAME_PREFIX = "Section-";

	private String dynamicNamespace;

	private List<ModelSection> sections = new ArrayList<ModelSection>();

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

	public ModelSection addSection() {

		return addSection(DEFAULT_SECTION_NAME_PREFIX + sections.size());
	}

	public ModelSection addSection(String name) {

		ModelSection section = new ModelSection(this, name);

		sections.add(section);

		return section;
	}

	public void setModelLoaded() {

		editActions.startTracking();
	}

	public void addEditListener(ModelEditListener listener) {

		editActions.addListener(listener);
	}

	public List<ModelSection> getSections() {

		return new ArrayList<ModelSection>(sections);
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

	public EntityId createEntityId(URI uri, String labelOrNull) {

		if (hasDynamicNamespace(uri)) {

			return toEntityId(createDynamicId(uri, labelOrNull));
		}

		return new EntityId(uri, labelOrNull);
	}

	public boolean containsDynamicConcept(DynamicId dynamicId) {

		return containsConcept(toEntityId(dynamicId));
	}

	boolean modelLoaded() {

		return editActions.trackingStarted();
	}

	boolean canResetDynamicConceptId(Concept concept, DynamicId newDynamicId) {

		EntityId newId = toEntityId(newDynamicId);

		return concept.getConceptId().equals(newId) || !containsConcept(newId);
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
