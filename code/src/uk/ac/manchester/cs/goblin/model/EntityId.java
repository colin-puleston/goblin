package uk.ac.manchester.cs.goblin.model;

import java.io.*;
import java.net.*;

import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
public abstract class EntityId {

	static public boolean validName(String name) {

		return !name.isEmpty() && encodeName(name).equals(name);
	}

	static public String nameToLabel(String name) {

		return KLabel.create(checkValidName(name));
	}

	static public String labelToNameOrNull(String label) {

		checkValidLabel(label);

		do {

			String name = KLabel.recreateName(label);

			if (validName(name)) {

				return name;
			}

			label = label.substring(0, label.length() - 1);
		}
		while (label.length() != 0);

		return null;
	}

	static private String resolveLabel(String name, String labelOrNull) {

		return labelOrNull != null ? checkValidLabel(labelOrNull) : nameToLabel(name);
	}

	static private String checkValidName(String name) {

		if (name.isEmpty()) {

			throw new RuntimeException("Name is empty!");
		}

		if (!validName(name)) {

			throw new RuntimeException("Not a valid name: " + name);
		}

		return name;
	}

	static private String checkValidLabel(String label) {

		if (label.isEmpty()) {

			throw new RuntimeException("Label is empty!");
		}

		return label;
	}

	static private String encodeName(String name) {

		try {

			return URLEncoder.encode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {

			throw new Error(e);
		}
	}

	private String name;
	private String label;

	public abstract boolean equals(Object other);

	public abstract int hashCode();

	public String toString() {

		return name + "(" + label + ")";
	}

	public String getName() {

		return name;
	}

	public String getLabel() {

		return label;
	}

	public abstract boolean dynamicId();

	protected EntityId(String name, String labelOrNull) {

		this.name = checkValidName(name);
		this.label = resolveLabel(name, labelOrNull);
	}
}
