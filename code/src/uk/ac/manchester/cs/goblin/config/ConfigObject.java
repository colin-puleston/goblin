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

			public void doAdd(boolean replacement) {

				value = editValue;

				pollListenersForUpdate();
			}

			public void doRemove(boolean replacing) {
			}

			public EditLocation createLocation(boolean postRemovalOp) {

				return new ConfigEditLocation();
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

	class DataArray<V> extends ConfigField<List<V>> {

		DataArray() {

			this(new ArrayList<V>());
		}

		DataArray(List<V> values) {

			super(values);
		}

		void add(V value) {

			set(copyPlus(value));
		}

		void remove(V value) {

			set(copyMinus(value));
		}

		void includeAddAction(CompoundEditAction compoundAction, V value) {

			includeSetAction(compoundAction, copyPlus(value));
		}

		void includeRemoveAction(CompoundEditAction compoundAction, V value) {

			includeSetAction(compoundAction, copyMinus(value));
		}

		void replace(V replacementValue) {

			replace(Collections.singletonList(replacementValue));
		}

		void replace(List<V> replacementValues) {

			set(replacementValues);
		}

		void reorder(List<V> reorderedValues) {

			checkReorder(reorderedValues);

			set(reorderedValues);
		}

		boolean isEmpty() {

			return get().isEmpty();
		}

		int size() {

			return get().size();
		}

		V get(int index) {

			return get().get(index);
		}

		List<V> copy() {

			return new ArrayList<V>(get());
		}

		boolean contains(V hierarchy) {

			return get().contains(hierarchy);
		}

		List<ConfigUpdateListener> getUpdateListeners() {

			return dataArrayUpdateListeners;
		}

		private List<V> copyPlus(V value) {

			List<V> newValues = copy();

			newValues.add(value);

			return newValues;
		}

		private List<V> copyMinus(V value) {

			List<V> newValues = copy();

			newValues.remove(value);

			return newValues;
		}

		private void checkReorder(List<V> reorderedValues) {

			if (!new HashSet<V>(get()).equals(new HashSet<V>(reorderedValues))) {

				throw new RuntimeException("Invalid reordering!");
			}
		}
	}

	public void addDataFieldUpdateListener(ConfigUpdateListener listener) {

		dataFieldUpdateListeners.add(listener);
	}

	public void addDataArrayUpdateListener(ConfigUpdateListener listener) {

		dataArrayUpdateListeners.add(listener);
	}

	abstract ConfigEditActions getEditActions();
}
