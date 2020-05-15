package uk.ac.manchester.cs.goblin.io;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class DynamicModelRenderer {

	private Ontology ontology;
	private AnchoredConstraintClassIRIs anchoredConstraintClassIRIs;

	private class ConstraintRenderer {

		private Constraint constraint;

		private OWLClass source;
		private Set<OWLClass> targets;

		ConstraintRenderer(Constraint constraint) {

			this.constraint = constraint;

			source = getCls(constraint.getSourceValue());
			targets = getClasses(constraint.getTargetValues());

			render();
		}

		private void render() {

			ConstraintType type = constraint.getType();

			if (type instanceof SimpleConstraintType) {

				renderSimple((SimpleConstraintType)type);
			}

			if (type instanceof AnchoredConstraintType) {

				renderAnchored((AnchoredConstraintType)type);
			}
		}

		private void renderSimple(SimpleConstraintType type) {

			OWLObjectProperty prop = getObjectProperty(type.getLinkingPropertyId());

			addConsequenceAxiom(source, prop, targets);
		}

		private void renderAnchored(AnchoredConstraintType type) {

			OWLClass anchor = getCls(type.getAnchorConceptId());
			OWLClass anchorSub = addClass(anchor, createAnchorSubIRI(type));

			OWLObjectProperty srcProp = getObjectProperty(type.getSourcePropertyId());
			OWLObjectProperty tgtProp = getObjectProperty(type.getTargetPropertyId());

			ontology.addPremiseAxiom(anchor, anchorSub, srcProp, source);
			addConsequenceAxiom(anchorSub, tgtProp, targets);
		}

		private void addConsequenceAxiom(
						OWLClass subject,
						OWLObjectProperty property,
						Set<OWLClass> values) {

			ConstraintSemantics semantics = constraint.getSemantics();

			if (constraint.getSemantics().validValues()) {

				ontology.addAllConsequenceAxiom(subject, property, values);
			}
			else {

				ontology.addSomeConsequenceAxioms(subject, property, values);
			}
		}

		private IRI createAnchorSubIRI(AnchoredConstraintType type) {

			return anchoredConstraintClassIRIs.create(constraint, type);
		}
	}

	DynamicModelRenderer(Ontology ontology, String dynamicNamespace) {

		this.ontology = ontology;

		anchoredConstraintClassIRIs = new AnchoredConstraintClassIRIs(dynamicNamespace);
	}

	void write(Model model, File dynamicFile) {

		ontology.removeAllClasses();

		renderNewHierarchies(model);
		renderNewConstraints(model);

		ontology.write(dynamicFile);
	}

	private void renderNewHierarchies(Model model) {

		for (Hierarchy hierarchy : model.getHierarchies()) {

			Concept root = hierarchy.getRootConcept();

			renderHierarchyFrom(root, getCls(root));
		}
	}

	private void renderNewConstraints(Model model) {

		for (Hierarchy hierarchy : model.getHierarchies()) {

			renderConstraintsFrom(hierarchy.getRootConcept());
		}
	}

	private void renderHierarchyFrom(Concept concept, OWLClass cls) {

		for (Concept sub : concept.getChildren()) {

			renderHierarchyFrom(sub, addClass(cls, sub.getConceptId()));
		}
	}

	private void renderConstraintsFrom(Concept concept) {

		for (Concept sub : concept.getChildren()) {

			renderConstraintsFor(sub);
			renderConstraintsFrom(sub);
		}
	}

	private void renderConstraintsFor(Concept concept) {

		for (Constraint constraint : concept.getConstraints()) {

			new ConstraintRenderer(constraint);
		}
	}

	private OWLClass addClass(OWLClass sup, EntityId conceptId) {

		OWLClass cls = addClass(sup, getIRI(conceptId));

		ontology.addLabel(cls, conceptId.getLabel());

		return cls;
	}

	private OWLClass addClass(OWLClass sup, IRI iri) {

		return ontology.addClass(sup, iri);
	}

	private Set<OWLClass> getSubClasses(OWLClass cls, boolean direct) {

		return ontology.getSubClasses(cls, direct);
	}

	private Set<OWLClass> getClasses(Set<Concept> concepts) {

		Set<OWLClass> classes = new HashSet<OWLClass>();

		for (Concept concept : concepts) {

			classes.add(getCls(concept));
		}

		return classes;
	}

	private OWLClass getCls(Concept concept) {

		return getCls(concept.getConceptId());
	}

	private OWLClass getCls(EntityId id) {

		return ontology.getClass(getIRI(id));
	}

	private OWLObjectProperty getObjectProperty(EntityId id) {

		return ontology.getObjectProperty(getIRI(id));
	}

	private IRI getIRI(EntityId id) {

		return IRI.create(id.getURI());
	}
}
