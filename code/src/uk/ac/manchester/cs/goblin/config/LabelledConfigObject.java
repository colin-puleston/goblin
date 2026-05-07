package uk.ac.manchester.cs.goblin.config;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class LabelledConfigObject<O extends LabelledConfigObject<O>> extends ConfigObject<O> {

	private LabelField label;
	private List<ConfigUpdateListener> labelUpdateListeners = new ArrayList<ConfigUpdateListener>();

	private class LabelField extends ConfigField<String> {

		LabelField(String value) {

			super(value);
		}

		List<ConfigUpdateListener> getUpdateListeners() {

			return labelUpdateListeners;
		}
	}

	public void addLabelUpdateListener(ConfigUpdateListener listener) {

		labelUpdateListeners.add(listener);
	}

	public void resetLabel(String label) {

		this.label.set(label);
	}

	public String getLabel() {

		return label.get();
	}

	public String toString() {

		return label.get();
	}

	LabelledConfigObject(String label) {

		this.label = new LabelField(label);
	}
}
