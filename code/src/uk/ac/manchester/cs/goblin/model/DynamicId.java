package uk.ac.manchester.cs.goblin.model;

import java.io.*;
import java.net.*;

import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
public class DynamicId {

	static public DynamicId fromName(String name) {

		return new DynamicId(name, nameToLabel(name));
	}

	static public DynamicId fromLabelOrNull(String label) {

		String name = labelToName(label);

		return name != null ? new DynamicId(name, label) : null;
	}

	static public DynamicId fromURIOrNull(URI uri, String dynamicNamespace) {

		String name = uriToDynamicNameOrNull(uri, dynamicNamespace);

		return name != null ? fromName(name) : null;
	}

	static public boolean validName(String name) {

		return !name.isEmpty() && encodeName(name).equals(name);
	}

	static private String nameToLabel(String name) {

		return KLabel.create(checkValidName(name));
	}

	static private String labelToName(String label) {

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

	static private String uriToDynamicNameOrNull(URI uri, String dynamicNamespace) {

		String u = uri.toString();

		if (u.startsWith(dynamicNamespace + '#')) {

			return u.substring(dynamicNamespace.length() + 1);
		}

		return null;
	}

	private String name;
	private String label;

	public DynamicId(String name, String label) {

		this.name = checkValidName(name);
		this.label = checkValidLabel(label);
	}

	public String toString() {

		return name + "(" + label + ")";
	}

	public String getName() {

		return name;
	}

	public String getLabel() {

		return label;
	}

	public boolean independentNameAndLabel() {

		return !nameToLabel(name).equals(label);
	}

	EntityId toEntityId(String dynamicNamespace) {

		EntityId entityId = new EntityId(nameToURI(dynamicNamespace), label);

		entityId.setDynamicId(this);

		return entityId;
	}

	private URI nameToURI(String dynamicNamespace) {

		try {

			return new URI(dynamicNamespace + '#' + name);
		}
		catch (URISyntaxException e) {

			throw new RuntimeException("Not a valid URI fragment: " + name);
		}
	}
}
