package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ConflictResolution {

	static final ConflictResolution NO_CONFLICTS = new ConflictResolution();
	static final ConflictResolution NO_RESOLUTION = new ConflictResolution();

	private List<EditAction> resolvingEditActions = new ArrayList<EditAction>();

	ConflictResolution(List<EditAction> resolvingEditActions) {

		this.resolvingEditActions.addAll(resolvingEditActions);
	}

	ConflictResolution combineWith(ConflictResolution other) {

		ConflictResolution combo = new ConflictResolution();

		combo.resolvingEditActions.addAll(resolvingEditActions);
		combo.resolvingEditActions.addAll(other.resolvingEditActions);

		return combo;
	}

	boolean resolvable() {

		return this != NO_RESOLUTION;
	}

	EditAction incorporateResolvingEdits(EditAction action) {

		if (resolvingEditActions.isEmpty()) {

			return action;
		}

		CompoundEditAction compound = new CompoundEditAction();

		compound.addSubActions(resolvingEditActions);
		compound.addSubAction(action);

		return compound;
	}

	private ConflictResolution() {
	}
}
