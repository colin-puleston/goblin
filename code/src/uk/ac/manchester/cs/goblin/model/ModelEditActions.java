package uk.ac.manchester.cs.goblin.model;

import uk.ac.manchester.cs.goblin.edit.*;

/**
 * @author Colin Puleston
 */

public class ModelEditActions extends EditActions<ModelEditLocation> {

	protected Class<ModelEditLocation> getEditLocationClass(){

		return ModelEditLocation.class;
	}
}
