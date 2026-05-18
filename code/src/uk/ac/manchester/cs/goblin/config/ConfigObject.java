
package uk.ac.manchester.cs.goblin.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */
public abstract class ConfigObject<O extends ConfigObject<O>> {

	private List<ConfigUpdateListener> dataFieldUpdateListeners = new ArrayList<ConfigUpdateListener>();
	private List<ConfigUpdateListener> dataArrayUpdateListeners = new ArrayList<ConfigUpdateListener>();

	abstract class ConfigField<V> {

		private V value;

		private class FieldEditTarget implements EditTarget {

			private V editValue;
			private V preEditValue = null;

			public void doAdd(boolean replacement) {

				preEditValue = value;
				value = editValue;

				pollListenersForUpdate();
			}

			public void doRemove(boolean replacing) {
			}

			public EditLocation createLocation(boolean postRemovalOp) {

				return createConfigEditLocation(preEditValue);
			}

			FieldEditTarget(V editValue) {

				this.editValue = editValue;
			}
		}

		ConfigField(V value) {

			this.value = value;
		}

		void set(V newValue) {

			if (!newValue.equals(value)) {

				getEditActions().perform(createReplaceAction(newValue));
			}
		}

		void includeSetAction(CompoundEditAction compoundAction, V newValue) {

			if (!newValue.equals(value)) {

				compoundAction.addSubAction(createReplaceAction(newValue));
			}
		}

		V get() {

			return value;
		}

		ConfigEditLocation createConfigEditLocation(V preEditValue) {

			return createEditLocation();
		}

		abstract List<ConfigUpdateListener> getUpdateListeners();

		void pollListenersForUpdate() {

			for (ConfigUpdateListener listener : copyUpdateListeners()) {

				listener.onUpdate();
			}
		}

		private List<ConfigUpdateListener> copyUpdateListeners() {

			return new ArrayList<ConfigUpdateListener>(getUpdateListeners());
		}

		private ReplaceAction<FieldEditTarget> createReplaceAction(V newValue) {

			FieldEditTarget oldTarget = new FieldEditTarget(value);
			FieldEditTarget newTarget = new FieldEditTarget(newValue);

			return new ReplaceAction<FieldEditTarget>(oldTarget, newTarget);
		}
	}

	class DataField<V> extends ConfigField<V> {

		DataField(V value) {

			super(value);
		}

		List<ConfigUpdateListener> getUpdateListeners() {

			return dataFieldUpdateListeners;
		}
	}

	class DataArray<E extends ConfigObject<?>> extends ConfigField<List<E>> {

		DataArray() {

			this(new ArrayList<E>());
		}

		DataArray(List<E> elements) {

			super(elements);
		}

		void add(E element) {

			set(copyPlus(element));
		}

		void remove(E element) {

			set(copyMinus(element));
		}

		void includeAddAction(CompoundEditAction compoundAction, E element) {

			includeSetAction(compoundAction, copyPlus(element));
		}

		void includeRemoveAction(CompoundEditAction compoundAction, E element) {

			includeSetAction(compoundAction, copyMinus(element));
		}

		void replace(E replacementElement) {

			replace(Collections.singletonList(replacementElement));
		}

		void replace(List<E> replacementElements) {

			set(replacementElements);
		}

		void reorder(List<E> reorderedElements) {

			checkReorder(reorderedElements);

			set(reorderedElements);
		}

		boolean isEmpty() {

			return get().isEmpty();
		}

		int size() {

			return get().size();
		}

		E get(int index) {

			return get().get(index);
		}

		List<E> copy() {

			return new ArrayList<E>(get());
		}

		boolean contains(E hierarchy) {

			return get().contains(hierarchy);
		}

		ConfigEditLocation createConfigEditLocation(List<E> preEditValue) {

			E added = lookForAddedElement(preEditValue);

			return added != null ? added.createEditLocation() : createEditLocation();
		}

		List<ConfigUpdateListener> getUpdateListeners() {

			return dataArrayUpdateListeners;
		}

		private List<E> copyPlus(E element) {

			List<E> newElements = copy();

			newElements.add(element);

			return newElements;
		}

		private List<E> copyMinus(E element) {

			List<E> newElements = copy();

			newElements.remove(element);

			return newElements;
		}

		private void checkReorder(List<E> reorderedElements) {

			if (!new HashSet<E>(get()).equals(new HashSet<E>(reorderedElements))) {

				throw new RuntimeException("Invalid reordering!");
			}
		}

		private E lookForAddedElement(List<E> preAddElements) {

			List<E> elements = copy();

			elements.removeAll(preAddElements);

			return elements.size() == 1 ? elements.get(0) : null;
		}
	}

	public void addDataFieldUpdateListener(ConfigUpdateListener listener) {

		dataFieldUpdateListeners.add(listener);
	}

	public void addDataArrayUpdateListener(ConfigUpdateListener listener) {

		dataArrayUpdateListeners.add(listener);
	}

	abstract ConfigEditActions getEditActions();

	abstract ConfigEditLocation createEditLocation();
}
