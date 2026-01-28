package uk.ac.manchester.cs.goblin.model;

import java.net.*;
import java.util.*;

/**
 * @author Colin Puleston
 */
public class Model extends CoreHierarchyContainer {

	static private final String DEFAULT_SECTION_NAME_PREFIX = "Section-";

	private List<ModelSection> sections = new ArrayList<ModelSection>();

	private EditActions editActions = new EditActions();
	private ConceptTracking conceptTracking = new ConceptTracking();
	private ConflictResolver conflictResolver = new ConflictResolver();

	public void setConfirmations(Confirmations confirmations) {

		conflictResolver.setConfirmations(confirmations);
	}

	public ModelSection addSection() {

		return addSection(DEFAULT_SECTION_NAME_PREFIX + sections.size());
	}

	public ModelSection addSection(String label) {

		ModelSection section = new ModelSection(this, label);

		sections.add(section);

		return section;
	}

	public void setModelLoaded() {

		editActions.startTracking();
	}

	public void addEditListener(ModelEditListener listener) {

		editActions.addListener(listener);
	}

	public Hierarchy createDynamicValueHierarchy(EntityId rootConceptId) {

		return new DynamicValueHierarchy(this, rootConceptId);
	}

	public List<ModelSection> getSections() {

		return new ArrayList<ModelSection>(sections);
	}

	public List<Hierarchy> getCoreHierarchies() {

		List<Hierarchy> hierarchies = new ArrayList<Hierarchy>();

		for (ModelSection section : sections) {

			hierarchies.addAll(section.getCoreHierarchies());
		}

		return hierarchies;
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

	EditActions getEditActions() {

		return editActions;
	}

	ConceptTracking getConceptTracking() {

		return conceptTracking;
	}

	ConflictResolver getConflictResolver() {

		return conflictResolver;
	}
}
