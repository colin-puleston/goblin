package uk.ac.manchester.cs.goblin.io.config;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.io.ontology.*;

/**
 * @author Colin Puleston
 */
public class ConfigOntology {

	private ConfigEntity rootConcept = new ConfigEntity();
	private ConfigEntity rootProperty = new ConfigEntity();

	private abstract class EntityLoader<E extends OWLEntity> {

		final Ontology coreOntology;

		EntityLoader(Ontology coreOntology) {

			this.coreOntology = coreOntology;
		}

		void loadDescendants(ConfigEntity configEntity, E owlEntity) {

			for (E cls : getChildEntities(owlEntity)) {

				loadDescendants(configEntity.addChild(getId(owlEntity)), owlEntity);
			}
		}

		abstract Set<E> getChildEntities(E owlEntity);

		private EntityId getId(E owlEntity) {

			return new CoreId(owlEntity.getIRI(), coreOntology.lookForLabel(owlEntity));
		}
	}

	private class ConceptLoader extends EntityLoader<OWLClass> {

		ConceptLoader(Ontology coreOntology) {

			super(coreOntology);

			loadDescendants(rootConcept, coreOntology.getRootClass());
		}

		Set<OWLClass> getChildEntities(OWLClass owlEntity) {

			return coreOntology.getSubClasses(owlEntity, true);
		}
	}

	private class PropertyLoader extends EntityLoader<OWLObjectProperty> {

		PropertyLoader(Ontology coreOntology) {

			super(coreOntology);

			loadDescendants(rootProperty, coreOntology.getRootObjectProperty());
		}

		Set<OWLObjectProperty> getChildEntities(OWLObjectProperty owlEntity) {

			return coreOntology.getSubProperties(owlEntity, true);
		}
	}

	public ConfigEntity getRootConcept() {

		return rootConcept;
	}

	public ConfigEntity getRootProperty() {

		return rootProperty;
	}

	ConfigOntology(Ontology coreOntology) {

		new ConceptLoader(coreOntology);
		new PropertyLoader(coreOntology);
	}
}
