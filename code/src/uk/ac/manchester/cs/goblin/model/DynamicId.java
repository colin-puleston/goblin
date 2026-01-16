package uk.ac.manchester.cs.goblin.model;

/**
 * @author Colin Puleston
 */
public class DynamicId extends EntityId {

	public DynamicId(String name) {

		super(name, null);
	}

	public DynamicId(String name, String labelOrNull) {

		super(name, labelOrNull);
	}

	public boolean equals(Object other) {

		return other instanceof DynamicId && getName().equals(((DynamicId)other).getName());
	}

	public int hashCode() {

		return getName().hashCode();
	}

	public boolean dynamicId() {

		return true;
	}
}
