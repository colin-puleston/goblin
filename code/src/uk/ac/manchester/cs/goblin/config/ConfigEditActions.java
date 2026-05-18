package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */

public class ConfigEditActions extends EditActions<ConfigEditLocation> {

	protected Class<ConfigEditLocation> getEditLocationClass(){

		return ConfigEditLocation.class;
	}
}
