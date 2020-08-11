package uk.ac.manchester.cs.goblin.model;

import java.util.*;

/**
 * @author Colin Puleston
 */
public class ConceptGroup {

	private List<Concept> concepts = new ArrayList<Concept>();

	private abstract class GroupAction {

		private List<EditAction> subActions = new ArrayList<EditAction>();

		boolean checkPerform() {

			EditAction action = resolveGroupAction();

			if (action != null) {

				getEditActions().perform(action);

				return true;
			}

			return false;
		}

		abstract EditAction checkCreateSubAction(Concept concept);

		private EditAction resolveGroupAction() {

			addSubActions();

			if (subActions.isEmpty()) {

				return null;
			}

			if (subActions.size() == 1) {

				return subActions.get(0);
			}

			return new CompoundEditAction(subActions);
		}

		private void addSubActions() {

			for (Concept concept : concepts) {

				checkAddSubAction(concept);
			}
		}

		private void checkAddSubAction(Concept concept) {

			EditAction subAction = checkCreateSubAction(concept);

			if (subAction != null) {

				subActions.add(subAction);
			}
		}
	}

	private class GroupMover extends GroupAction {

		private Concept newParent;

		GroupMover(Concept newParent) {

			this.newParent = newParent;
		}

		EditAction checkCreateSubAction(Concept concept) {

			return concept.checkCreateMoveAction(newParent);
		}
	}

	private class GroupRemover extends GroupAction {

		EditAction checkCreateSubAction(Concept concept) {

			return concept.createRemoveAction();
		}
	}

	public ConceptGroup(Collection<Concept> concepts) {

		if (concepts.isEmpty()) {

			throw new RuntimeException("Empty concept set");
		}

		this.concepts.addAll(concepts);
	}

	public boolean moveAll(Concept newParent) {

		return new GroupMover(newParent).checkPerform();
	}

	public void removeAll() {

		new GroupRemover().checkPerform();
	}

	private EditActions getEditActions() {

		return concepts.get(0).getModel().getEditActions();
	}
}
