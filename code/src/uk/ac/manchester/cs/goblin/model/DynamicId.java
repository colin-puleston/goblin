package uk.ac.manchester.cs.goblin.model;

import java.io.*;
import java.net.*;

import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
public class DynamicId {

	static public DynamicId fromName(String name) {

		return new DynamicId(name, nameToLabel(name));
	}

	static public DynamicId fromLabel(String label) {

		return new DynamicId(labelToName(label), label);
	}

	static private String nameToLabel(String name) {

		return KLabel.create(decodeName(checkNonEmptyName(name)));
	}

	static private String labelToName(String label) {

		return KLabel.recreateName(encodeName(checkNonEmptyLabel(label)));
	}

	static private String checkNonEmptyName(String name) {

		return checkNonEmpty(name, "name");
	}

	static private String checkNonEmptyLabel(String label) {

		return checkNonEmpty(label, "label");
	}

	static private String checkNonEmpty(String thing, String thingName) {

		if (thing.isEmpty()) {

			throw new RuntimeException(thingName + " is empty!");
		}

		return thing;
	}

	static private String encodeName(String name) {

		try {

			return URLEncoder.encode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {

			throw new Error(e);
		}
	}

	static private String decodeName(String name) {

		try {

			return URLDecoder.decode(name, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {

			throw new Error(e);
		}
	}

	private String name;
	private String label;

	public DynamicId(String name, String label) {

		this.name = encodeName(checkNonEmptyName(name));
		this.label = checkNonEmptyLabel(label);
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

	EntityId toEntityId(String namespace) {

		EntityId entityId = new EntityId(nameToURI(namespace), label);

		entityId.setDynamicId(this);

		return entityId;
	}

	private URI nameToURI(String namespace) {

		try {

			return new URI(namespace + '#' + name);
		}
		catch (URISyntaxException e) {

			throw new Error("Not a valid URI fragment: " + name);
		}
	}
}
