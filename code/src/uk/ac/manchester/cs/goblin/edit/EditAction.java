package uk.ac.manchester.cs.goblin.edit;

/**
 * @author Colin Puleston
 */
public abstract class EditAction {

	abstract void perform(boolean forward);

	abstract AtomicEditAction<?> getFinalAtomicAction(boolean forward);
}
