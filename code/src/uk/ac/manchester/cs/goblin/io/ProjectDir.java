package uk.ac.manchester.cs.goblin.io;

import java.io.*;

/**
 * @author Colin Puleston
 */
public class ProjectDir {

	static public final String CONFIG_FILENAME = "goblin.xml";

	private File projectDir;
	private File configFile;

	public ProjectDir(File projectDir) throws BadProjectDirException {

		this.projectDir = projectDir;

		configFile = getFile(CONFIG_FILENAME);

		if (!projectDir.exists() || projectDir.isFile()) {

			throw new BadProjectDirException("Project folder does not exist: " + projectDir);
		}

		if (!configFile.exists()) {

			throw new BadProjectDirException("Config file does not exist: " + configFile);
		}
	}

	public File getConfigFile() {

		return configFile;
	}

	public File getFile(String name) {

		return new File(projectDir, name);
	}
}
