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

	private class ModelEditActions extends EditActions<ModelEditLocation> {

		protected Class<ModelEditLocation> getEditLocationClass(){

			return ModelEditLocation.class;
		}
	}

	public void addSection(ModelSection section) {

		sections.add(section);
	}

	public void setModelLoaded() {

		editActions.startTracking();
	}

	public void setConfirmations(Confirmations confirmations) {

		conflictResolver.setConfirmations(confirmations);
	}

	public void addEditListener(EditListener listener) {

		editActions.addListener(listener);
	}

	public Hierarchy createDynamicValueHierarchy(EntityId rootConceptId) {

		return new DynamicValueHierarchy(this, rootConceptId);
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

	public ModelEditLocation undo() {

		return editActions.undo();
	}

	public ModelEditLocation redo() {

		return editActions.redo();
	}

	EditActions<?> getEditActions() {

		return editActions;
	}

	ConceptTracking getConceptTracking() {

		return conceptTracking;
	}

	ConflictResolver getConflictResolver() {

		return conflictResolver;
	}
}
