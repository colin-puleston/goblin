package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ConflictResolution {

	static final ConflictResolution NO_CONFLICTS = new ConflictResolution();
	static final ConflictResolution NO_RESOLUTION = new ConflictResolution();

	private List<EditAction> resolvingEditActions;

	ConflictResolution(List<EditAction> resolvingEditActions) {

		this.resolvingEditActions = resolvingEditActions;
	}

	boolean resolvable() {

		return this != NO_RESOLUTION;
	}

	EditAction incorporateResolvingEdits(EditAction action) {

		if (resolvingEditActions.isEmpty()) {

			return action;
		}

		CompoundEditAction compoundAction = new CompoundEditAction();

		compoundAction.addSubActions(resolvingEditActions);
		compoundAction.addSubAction(action);

		return compoundAction;
	}

	private ConflictResolution() {

		this(Collections.emptyList());
	}
}
