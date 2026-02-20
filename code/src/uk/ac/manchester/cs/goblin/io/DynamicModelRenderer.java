package uk.ac.manchester.cs.goblin.io;

import java.io.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.ontology.*;
import uk.ac.manchester.cs.goblin.io.config.*;

/**
 * @author Colin Puleston
 */
class DynamicModelRenderer {

	private Ontology ontology;
	private EntityIds entityIds;

	private AnchoredConstraintClassIRIs anchoredConstraintClassIRIs;

	private Set<Hierarchy> renderedDynamicValueHierarchies = new HashSet<Hierarchy>();

	private class ConstraintRenderer extends AttributeVisitor {

		private Constraint constraint;

		private OWLClass source;
		private Set<OWLClass> targets;

		public void visit(CoreAttribute attribute, SimpleAttributeConfig config) {

			renderLinkingPropertyConstraint(config.getLinkingPropertyId());
		}

		public void visit(CoreAttribute attribute, AnchoredAttributeConfig config) {

			OWLClass anchor = getCls(config.getAnchorConceptId());
			OWLClass anchorSub = addClass(anchor, createAnchorSubIRI(config));

			OWLObjectProperty srcProp = getObjectProperty(config.getSourcePropertyId());
			OWLObjectProperty tgtProp = getObjectProperty(config.getTargetPropertyId());

			ontology.addPremiseAxiom(anchor, anchorSub, srcProp, source);
			addConsequenceAxiom(anchorSub, tgtProp, targets);
		}

		public void visit(CoreAttribute attribute, HierarchicalAttributeConfig config) {

			for (OWLClass target : targets) {

				ontology.addSuperClass(source, target);
			}
		}

		public void visit(DynamicAttribute attribute) {

			renderLinkingPropertyConstraint(attribute.getAttributeId());
		}

		ConstraintRenderer(Constraint constraint) {

			this.constraint = constraint;

			source = getCls(constraint.getSourceValue());
			targets = getClasses(constraint.getTargetValues());

			visit(constraint.getAttribute());
		}

		private void renderLinkingPropertyConstraint(EntityId propertyId) {

			addConsequenceAxiom(source, getObjectProperty(propertyId), targets);
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

		private IRI createAnchorSubIRI(AnchoredAttributeConfig attributeConfig) {

			return anchoredConstraintClassIRIs.create(attributeConfig, constraint);
		}
	}

	DynamicModelRenderer(Ontology ontology, DynamicIRIs dynamicIRIs) {

		this.ontology = ontology;

		entityIds = new EntityIds(dynamicIRIs);
		anchoredConstraintClassIRIs = new AnchoredConstraintClassIRIs(dynamicIRIs);
	}

	void write(Model model, File dynamicFile) {

		ontology.removeAllClasses();

		renderDynamicHierarchies(model);
		renderDynamicConstraints(model);

		ontology.write(dynamicFile);
	}

	private void renderDynamicHierarchies(Model model) {

		for (Hierarchy hierarchy : model.getCoreHierarchies()) {

			if (!hierarchy.fixedStructure()) {

				renderHierarchy(hierarchy);
			}

			if (hierarchy.dynamicAttributesEnabled()) {

				renderDynamicValueHierarchies(hierarchy);
			}
		}
	}

	private void renderDynamicValueHierarchies(Hierarchy coreHierarchy) {

		for (DynamicAttribute attribute : coreHierarchy.getDynamicAttributes()) {

			Hierarchy valueHierarchy = attribute.getRootTargetConcept().getHierarchy();

			if (renderedDynamicValueHierarchies.add(valueHierarchy)) {

				renderHierarchy(valueHierarchy);
			}
		}
	}

	private void renderHierarchy(Hierarchy hierarchy) {

		Concept root = hierarchy.getRootConcept();

		renderHierarchyFrom(root, getCls(root));
	}

	private void renderHierarchyFrom(Concept concept, OWLClass cls) {

		for (Concept sub : concept.getChildren()) {

			renderHierarchyFrom(sub, resolveClass(cls, sub));
		}
	}

	private void renderDynamicConstraints(Model model) {

		for (Hierarchy hierarchy : model.getCoreHierarchies()) {

			if (hierarchy.potentiallyHasAttributes()) {

				renderConstraintsFrom(hierarchy.getRootConcept());
			}
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

		return entityIds.toIRI(id);
	}
}
