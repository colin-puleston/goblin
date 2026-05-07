package uk.ac.manchester.cs.goblin.config;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class ConfigObject<O extends ConfigObject<O>> {

	private List<ConfigUpdateListener> dataFieldUpdateListeners = new ArrayList<ConfigUpdateListener>();
	private List<ConfigUpdateListener> dataArrayUpdateListeners = new ArrayList<ConfigUpdateListener>();

	abstract class ConfigField<V> {

		private V value;

		ConfigField(V value) {

			this.value = value;
		}

		void set(V value) {

			this.value = value;

			pollListenersForUpdate();
		}

		V get() {

			return value;
		}

		abstract List<ConfigUpdateListener> getUpdateListeners();

		void pollListenersForUpdate() {

			for (ConfigUpdateListener listener : getUpdateListeners()) {

				listener.onUpdate();
			}
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

		private List<V> values;

		DataArray() {

			this(new ArrayList<V>());
		}

		DataArray(List<V> values) {

			super(values);

			this.values = values;
		}

		void add(V value) {

			values.add(value);

			pollListenersForUpdate();
		}

		void addAll(List<V> values) {

			values.addAll(values);

			pollListenersForUpdate();
		}

		void remove(V value) {

			values.remove(value);

			pollListenersForUpdate();
		}

		void clear() {

			values.clear();

			pollListenersForUpdate();
		}

		void replace(V replacementValue) {

			replace(Collections.singletonList(replacementValue));
		}

		void replace(List<V> replacementValues) {

			values.clear();
			values.addAll(replacementValues);

			pollListenersForUpdate();
		}

		void reorder(List<V> reorderedValues) {

			checkReorder(reorderedValues);

			replace(reorderedValues);
		}

		boolean isEmpty() {

			return values.isEmpty();
		}

		int size() {

			return values.size();
		}

		V get(int index) {

			return values.get(index);
		}

		List<V> copy() {

			return new ArrayList<V>(values);
		}

		boolean contains(V hierarchy) {

			return values.contains(hierarchy);
		}

		List<ConfigUpdateListener> getUpdateListeners() {

			return dataArrayUpdateListeners;
		}

		private void checkReorder(List<V> reorderedValues) {

			if (!new HashSet<V>(values).equals(new HashSet<V>(reorderedValues))) {

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
}
