package uk.ac.manchester.cs.goblin.io;

import java.io.*;
import java.net.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class DynamicModelRenderer {

	private Ontology ontology;
	private String dynamicNamespace;

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

			if (type instanceof HierarchicalConstraintType) {

				renderHierarchicalType((HierarchicalConstraintType)type);
			}
			else if (type instanceof AnchoredConstraintType) {

				renderAnchoredType((AnchoredConstraintType)type);
			}
			else if (type instanceof PropertyConstraintType) {

				renderSimpleType((PropertyConstraintType)type);
			}
			else {

				throw new Error("Unrecognised ConstraintType: " + type);
			}
		}

		private void renderSimpleType(PropertyConstraintType type) {

			OWLObjectProperty prop = getObjectProperty(type.getTargetPropertyId());

			addConsequenceAxiom(source, prop, targets);
		}

		private void renderAnchoredType(AnchoredConstraintType type) {

			OWLClass anchor = getCls(type.getAnchorConceptId());
			OWLClass anchorSub = addClass(anchor, createAnchorSubIRI(type));

			OWLObjectProperty srcProp = getObjectProperty(type.getSourcePropertyId());
			OWLObjectProperty tgtProp = getObjectProperty(type.getTargetPropertyId());

			ontology.addPremiseAxiom(anchor, anchorSub, srcProp, source);
			addConsequenceAxiom(anchorSub, tgtProp, targets);
		}

		private void renderHierarchicalType(HierarchicalConstraintType type) {

			for (OWLClass target : targets) {

				ontology.addSuperClass(source, target);
			}
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
		this.dynamicNamespace = dynamicNamespace;

		anchoredConstraintClassIRIs = new AnchoredConstraintClassIRIs(dynamicNamespace);
	}

	void write(Model model, File dynamicFile) {

		ontology.removeAllClasses();

		renderDynamicHierarchies(model);
		renderDynamicConstraints(model);

		ontology.write(dynamicFile);
	}

	private void renderDynamicHierarchies(Model model) {

		for (Hierarchy hierarchy : model.getDynamicHierarchies()) {

			Concept root = hierarchy.getRootConcept();

			renderHierarchyFrom(root, getCls(root));
		}
	}

	private void renderDynamicConstraints(Model model) {

		for (Hierarchy hierarchy : model.getDynamicHierarchies()) {

			renderConstraintsFrom(hierarchy.getRootConcept());
		}
	}

	private void renderHierarchyFrom(Concept concept, OWLClass cls) {

		for (Concept sub : concept.getChildren()) {

			renderHierarchyFrom(sub, resolveClass(cls, sub));
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

	private OWLClass resolveClass(OWLClass sup, Concept concept) {

		EntityId id = concept.getConceptId();
		IRI iri = getIRI(id);

		if (concept.coreConcept()) {

			return ontology.getClass(iri);
		}

		OWLClass cls = addClass(sup, iri);

		ontology.addLabel(cls, id.getLabel());

		return cls;
	}

	private OWLClass addClass(OWLClass sup, IRI iri) {

		return ontology.addClass(sup, iri);
	}

	private Set<OWLClass> getSubClasses(OWLClass cls, boolean direct) {

		return ontology.getSubClasses(cls, direct);
	}

	private Set<OWLClass> getClasses(Collection<Concept> concepts) {

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

		return id instanceof CoreId ? ((CoreId)id).getIRI() : createDynamicIRI(id);
	}

	private IRI createDynamicIRI(EntityId id) {

		return IRI.create(dynamicNamespace + '#' + id.getName());
	}
}
