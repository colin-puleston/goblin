package uk.ac.manchester.cs.goblin.io;

import java.net.*;
import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class DynamicModelLoader {

	private Model model;
	private Ontology ontology;

	private Set<OWLClass> dynamicClasses = new HashSet<OWLClass>();

	private abstract class ConstraintLoader {

		private ConstraintType type;

		private OWLClass subject;
		private OWLObjectProperty targetProperty;

		private Set<OWLClassAxiom> subjectAxioms;

		private TargetExtractor allTargetExtractor;
		private TargetExtractor someTargetExtractor;

		abstract class ConceptExtractor {

			Concept extractOne(OWLClassExpression expr) {

				return getOne(extractAll(expr));
			}

			Concept extractOneOrNone(OWLClassExpression expr) {

				return lookForOne(extractAll(expr));
			}

			Set<Concept> extractAll(OWLClassExpression expr) {

				Set<Concept> concepts = new HashSet<Concept>();

				for (OWLClass cls : extractClasses(expr)) {

					concepts.add(getConcept(cls));
				}

				return concepts;
			}

			abstract OWLObjectProperty getProperty();

			abstract Class<? extends OWLQuantifiedObjectRestriction> getRestrictionType();

			private Set<OWLClass> extractClasses(OWLClassExpression expr) {

				OWLQuantifiedObjectRestriction restriction = asRestrictionOrNull(expr);

				if (restriction != null && restriction.getProperty().equals(getProperty())) {

					OWLClassExpression filler = restriction.getFiller();

					if (filler instanceof OWLClass) {

						return Collections.singleton((OWLClass)filler);
					}

					if (allowUnionFiller(restriction) && filler instanceof OWLObjectUnionOf) {

						return getClassOperands((OWLObjectUnionOf)filler);
					}
				}

				return Collections.emptySet();
			}

			private boolean allowUnionFiller(OWLQuantifiedObjectRestriction restriction) {

				return restriction instanceof OWLObjectAllValuesFrom;
			}

			private Set<OWLClass> getClassOperands(OWLObjectUnionOf union) {

				Set<OWLClass> classes = new HashSet<OWLClass>();

				for (OWLClassExpression expr : union.getOperands()) {

					if (!(expr instanceof OWLClass)) {

						throw createBadAxiomsException();
					}

					classes.add((OWLClass)expr);
				}

				return classes;
			}

			private OWLQuantifiedObjectRestriction asRestrictionOrNull(OWLClassExpression expr) {

				return asTypeOrNull(expr, getRestrictionType());
			}
		}

		private abstract class TargetExtractor extends ConceptExtractor {

			OWLObjectProperty getProperty() {

				return targetProperty;
			}
		}

		private class AllTargetExtractor extends TargetExtractor {

			Class<OWLObjectAllValuesFrom> getRestrictionType() {

				return OWLObjectAllValuesFrom.class;
			}
		}

		private class SomeTargetExtractor extends TargetExtractor {

			Class<OWLObjectSomeValuesFrom> getRestrictionType() {

				return OWLObjectSomeValuesFrom.class;
			}
		}

		ConstraintLoader(ConstraintType type, OWLClass subject, EntityId targetPropertyId) {

			this.type = type;
			this.subject = subject;

			targetProperty = getObjectProperty(targetPropertyId);
			subjectAxioms = ontology.getAxioms(subject);

			allTargetExtractor = new AllTargetExtractor();
			someTargetExtractor = new SomeTargetExtractor();
		}

		void checkLoad(Concept source) {

			Set<OWLClassExpression> targetExprs = lookForTargetsExprs();

			if (!targetExprs.isEmpty()) {

				if (type.semanticsOption(ConstraintSemantics.VALID_VALUES)) {

					checkLoadValidValuesConstraint(source, targetExprs);
				}

				if (type.semanticsOption(ConstraintSemantics.IMPLIED_VALUE)) {

					checkLoadImpliedValueConstraints(source, targetExprs);
				}
			}
		}

		<T extends OWLClassAxiom>Set<T> getSubjectAxioms(Class<T> axiomCls) {

			Set<T> axioms = new HashSet<T>();

			for (OWLClassAxiom axiom : subjectAxioms) {

				if (axiomCls.isAssignableFrom(axiom.getClass())) {

					axioms.add(axiomCls.cast(axiom));
				}
			}

			return axioms;
		}

		<E>E lookForOne(Set<E> elements) {

			if (elements.size() > 1) {

				throw createBadAxiomsException();
			}

			return elements.isEmpty() ? null : elements.iterator().next();
		}

		RuntimeException createBadAxiomsException() {

			return new RuntimeException(
						"Illegal set of axioms for constraint-definition class: "
						+ subject);
		}

		private void checkLoadValidValuesConstraint(
						Concept source,
						Set<OWLClassExpression> targetExprs) {

			Set<Concept> targets = extractAllTargetConcepts(targetExprs);

			if (!targets.isEmpty()) {

				source.addValidValuesConstraint(type, targets);
			}
		}

		private void checkLoadImpliedValueConstraints(
						Concept source,
						Set<OWLClassExpression> targetExprs) {

			for (Concept target : extractSomeTargetConcepts(targetExprs)) {

				source.addImpliedValueConstraint(type, target);
			}
		}

		private Set<Concept> extractAllTargetConcepts(Set<OWLClassExpression> exprs) {

			for (OWLClassExpression expr : exprs) {

				Set<Concept> targets = allTargetExtractor.extractAll(expr);

				if (!targets.isEmpty()) {

					return targets;
				}
			}

			return Collections.emptySet();
		}

		private Set<Concept> extractSomeTargetConcepts(Set<OWLClassExpression> exprs) {

			Set<Concept> targets = new HashSet<Concept>();

			for (OWLClassExpression expr : exprs) {

				Concept target = someTargetExtractor.extractOneOrNone(expr);

				if (target != null) {

					targets.add(target);
				}
			}

			return targets;
		}

		private Set<OWLClassExpression> lookForTargetsExprs() {

			Set<OWLClassExpression> exprs = new HashSet<OWLClassExpression>();

			for (OWLSubClassOfAxiom axiom : getSubjectAxioms(OWLSubClassOfAxiom.class)) {

				OWLClassExpression expr = lookForTargetsExpr(axiom);

				if (expr != null) {

					exprs.add(expr);
				}
			}

			return exprs;
		}

		private OWLClassExpression lookForTargetsExpr(OWLSubClassOfAxiom axiom) {

			if (axiom.getSubClass().equals(subject)) {

				OWLClassExpression sup = axiom.getSuperClass();

				if (sup.containsEntityInSignature(targetProperty)) {

					return sup;
				}
			}

			return null;
		}

		private <E>E getOne(Set<E> elements) {

			E element = lookForOne(elements);

			if (element == null) {

				throw createBadAxiomsException();
			}

			return element;
		}
	}

	private class SimpleConstraintLoader extends ConstraintLoader {

		SimpleConstraintLoader(SimpleConstraintType type, OWLClass sourceCls) {

			super(type, sourceCls, type.getLinkingPropertyId());

			checkLoad(getConcept(sourceCls));
		}
	}

	private class AnchoredConstraintLoader extends ConstraintLoader {

		private OWLClass anchor;
		private OWLClass anchorSub;
		private OWLObjectProperty sourceProperty;

		private SourceExtractor sourceExtractor;

		private class SourceExtractor extends ConceptExtractor {

			Set<Concept> extractAll(OWLClassExpression expr) {

				Set<OWLClassExpression> ops = asIntersection(expr).getOperands();

				if (ops.remove(anchor) && ops.size() == 1) {

					return super.extractAll(ops.iterator().next());
				}

				throw createBadAxiomsException();
			}

			OWLObjectProperty getProperty() {

				return sourceProperty;
			}

			Class<OWLObjectSomeValuesFrom> getRestrictionType() {

				return OWLObjectSomeValuesFrom.class;
			}

			private OWLObjectIntersectionOf asIntersection(OWLClassExpression expr) {

				OWLObjectIntersectionOf inter = asTypeOrNull(expr, OWLObjectIntersectionOf.class);

				if (inter != null) {

					return inter;
				}

				throw createBadAxiomsException();
			}
		}

		AnchoredConstraintLoader(AnchoredConstraintType type, OWLClass anchor, OWLClass anchorSub) {

			super(type, anchorSub, type.getTargetPropertyId());

			this.anchor = anchor;
			this.anchorSub = anchorSub;

			sourceProperty = getObjectProperty(type.getSourcePropertyId());
			sourceExtractor = new SourceExtractor();

			checkLoad();
		}

		private void checkLoad() {

			OWLClassExpression expr = lookForSourceExpr();

			if (expr != null) {

				checkLoad(sourceExtractor.extractOne(expr));
			}
		}

		private OWLClassExpression lookForSourceExpr() {

			Set<OWLClassExpression> exprs = new HashSet<OWLClassExpression>();

			for (OWLEquivalentClassesAxiom axiom : getSubjectAxioms(OWLEquivalentClassesAxiom.class)) {

				OWLClassExpression expr = lookForSourceExpr(axiom);

				if (expr != null) {

					exprs.add(expr);
				}
			}

			return lookForOne(exprs);
		}

		private OWLClassExpression lookForSourceExpr(OWLEquivalentClassesAxiom axiom) {

			Set<OWLClassExpression> exprs = axiom.getClassExpressions();

			if (exprs.size() == 2 && exprs.remove(anchorSub)) {

				OWLClassExpression expr = exprs.iterator().next();

				if (expr.containsEntityInSignature(sourceProperty)) {

					return expr;
				}
			}

			return null;
		}
	}

	DynamicModelLoader(Model model, Ontology ontology) throws BadDynamicOntologyException {

		this.model = model;
		this.ontology = ontology;

		try {

			loadConcepts();
			loadConstraints();
		}
		catch (RuntimeException e) {

			throw new BadDynamicOntologyException(e);
		}
	}

	private void loadConcepts() {

		for (Hierarchy hierarchy : model.getHierarchies()) {

			Concept root = hierarchy.getRootConcept();

			loadConceptsFrom(root, getRootClass(root));
		}
	}

	private void loadConceptsFrom(Concept concept, OWLClass cls) {

		for (OWLClass subCls : getSubClasses(cls, true)) {

			loadConceptsFrom(addSubConcept(concept, subCls), subCls);
		}
	}

	private void loadConstraints() {

		for (Hierarchy hierarchy : model.getHierarchies()) {

			for (ConstraintType type : hierarchy.getConstraintTypes()) {

				loadConstraintsOfType(type);
			}
		}
	}

	private void loadConstraintsOfType(ConstraintType type) {

		if (type instanceof SimpleConstraintType) {

			loadConstraintsOfType((SimpleConstraintType)type);
		}

		if (type instanceof AnchoredConstraintType) {

			loadConstraintsOfType((AnchoredConstraintType)type);
		}
	}

	private void loadConstraintsOfType(SimpleConstraintType type) {

		OWLClass rootSource = getCls(type.getRootSourceConcept());

		for (OWLClass source : getSubClasses(rootSource, false)) {

			new SimpleConstraintLoader(type, source);
		}
	}

	private void loadConstraintsOfType(AnchoredConstraintType type) {

		OWLClass anchor = getCls(type.getAnchorConceptId());

		for (OWLClass anchorSub : getSubClasses(anchor, false)) {

			new AnchoredConstraintLoader(type, anchor, anchorSub);
		}
	}

	private Concept addSubConcept(Concept concept, OWLClass subCls) {

		if (dynamicClasses.add(subCls)) {

			return concept.addChild(getConceptId(subCls));
		}

		throw new RuntimeException("Cannot add concept with multiple parents: " + subCls);
	}

	private Set<OWLClass> getSubClasses(OWLClass cls, boolean direct) {

		return ontology.getSubClasses(cls, direct);
	}

	private OWLClass getRootClass(Concept concept) {

		IRI iri = getIRI(concept.getConceptId());

		if (ontology.classExists(iri)) {

			return ontology.getClass(iri);
		}

		throw new RuntimeException("Cannot find hierarchy-root class: " + iri);
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

	private Concept getConcept(OWLClass cls) {

		return model.getConcept(getConceptId(cls));
	}

	private EntityId getConceptId(OWLClass cls) {

		URI uri = cls.getIRI().toURI();

		return model.createEntityId(uri, ontology.lookForLabel(cls));
	}

	private <T>T asTypeOrNull(Object obj, Class<T> type) {

		return type.isAssignableFrom(obj.getClass()) ? type.cast(obj) : null;
	}
}
