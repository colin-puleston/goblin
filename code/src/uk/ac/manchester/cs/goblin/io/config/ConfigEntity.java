package uk.ac.manchester.cs.goblin.io.config;

import java.util.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
public class ConfigEntity {

	private EntityId id;
	private List<ConfigEntity> children = new ArrayList<ConfigEntity>();

	public EntityId getId() {

		if (id == null) {

			throw new RuntimeException("Cannot retrieve id of root-entity");
		}

		return id;
	}

	public List<ConfigEntity> getChildren() {

		return new ArrayList<ConfigEntity>(children);
	}

	ConfigEntity() {

		this(null);
	}

	ConfigEntity(EntityId id) {

		this.id = id;
	}

	ConfigEntity addChild(EntityId childId) {

		ConfigEntity child = new ConfigEntity(childId);

		children.add(child);

		return child;
	}
}
