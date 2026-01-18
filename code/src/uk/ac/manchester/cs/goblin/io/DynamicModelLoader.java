package uk.ac.manchester.cs.goblin.io;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
class DynamicModelLoader {

	private Model model;
	private Ontology ontology;

	private EntityIds entityIds;

	private Map<OWLClass, Concept> dynamicClassesToConcepts = new HashMap<OWLClass, Concept>();

	private abstract class RestrictionAxiomReader {

		private OWLClass sourceCls;

		RestrictionAxiomReader(OWLClass sourceCls) {

			this.sourceCls = sourceCls;
		}

		OWLClass getSourceClass() {

			return sourceCls;
		}

		<T extends OWLClassAxiom>Set<T> getSourceAxioms(Class<T> axiomCls) {

			Set<T> axioms = new HashSet<T>();

			for (OWLClassAxiom axiom : ontology.getAxioms(sourceCls)) {

				if (axiomCls.isAssignableFrom(axiom.getClass())) {

					axioms.add(axiomCls.cast(axiom));
				}
			}

			return axioms;
		}

		<E>E extractExactlyOne(Set<E> elements) {

			if (elements.size() != 1) {

				throw createBadAxiomsException();
			}

			return elements.iterator().next();
		}

		<E>E extractAtMostOne(Set<E> elements) {

			return elements.isEmpty() ? null : extractExactlyOne(elements);
		}

		RuntimeException createBadAxiomsException() {

			return new RuntimeException(
						"Illegal set of axioms for constraint-definition class: "
						+ sourceCls);
		}
	}

	private abstract class TypeRestrictionReader
							<R extends OWLQuantifiedObjectRestriction>
							extends RestrictionAxiomReader {

		private Class<R> restrictionType;

		TypeRestrictionReader(OWLClass sourceCls, Class<R> restrictionType) {

			super(sourceCls);

			this.restrictionType = restrictionType;
		}

		R findAtMostOneRequiredRestriction() {

			return extractAtMostOne(findAllRequiredRestrictions());
		}

		Set<R> findAllRequiredRestrictions() {

			Set<R> restrictions = new HashSet<R>();

			for (OWLSubClassOfAxiom axiom : getSourceAxioms(OWLSubClassOfAxiom.class)) {

				if (axiom.getSubClass().equals(getSourceClass())) {

					R restriction = asRequiredRestrictionOrNull(axiom.getSuperClass());

					if (restriction != null) {

						restrictions.add(restriction);
					}
				}
			}

			return restrictions;
		}

		R asRequiredRestriction(OWLClassExpression expr) {

			R restriction = asRequiredRestrictionOrNull(expr);

			if (restriction == null) {

				throw createBadAxiomsException();
			}

			return restriction;
		}

		R asRequiredRestrictionOrNull(OWLClassExpression expr) {

			R restriction = asTypeOrNull(expr, restrictionType);

			if (restriction != null && requiredProperty(restriction.getProperty())) {

				return restriction;
			}

			return null;
		}

		Concept extractSingleConceptFromFiller(R restriction) {

			return extractExactlyOne(extractFillerConcepts(restriction));
		}

		Set<Concept> extractFillerConcepts(R restriction) {

			Set<Concept> concepts = new HashSet<Concept>();

			for (OWLClass cls : extractFillerClasses(restriction)) {

				concepts.add(fillerToConcept(cls));
			}

			return concepts;
		}

		abstract boolean requiredProperty(OWLObjectProperty property);

		Concept fillerToConcept(OWLClass cls) {

			return getConcept(cls);
		}

		private boolean requiredProperty(OWLObjectPropertyExpression expr) {

			return expr instanceof OWLObjectProperty && requiredProperty((OWLObjectProperty)expr);
		}

		private Set<OWLClass> extractFillerClasses(R restriction) {

			OWLClassExpression filler = restriction.getFiller();

			if (filler instanceof OWLClass) {

				return Collections.singleton((OWLClass)filler);
			}

			if (allowUnionFiller() && filler instanceof OWLObjectUnionOf) {

				return getClassOperands((OWLObjectUnionOf)filler);
			}

			return Collections.emptySet();
		}

		private boolean allowUnionFiller() {

			return restrictionType == OWLObjectAllValuesFrom.class;
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
	}

	private class ConstraintTypeLoader {

		private Set<EntityId> loadedTypePropertyIds = new HashSet<EntityId>();

		private class ClassConstraintTypeLoader
						extends
							TypeRestrictionReader<OWLObjectAllValuesFrom> {

			private Concept source;
			private List<EntityId> localTypePropertyIds = new ArrayList<EntityId>();

			ClassConstraintTypeLoader(Concept source, OWLClass sourceCls) {

				super(sourceCls, OWLObjectAllValuesFrom.class);

				this.source = source;
			}

			Collection<EntityId> loadAllForClass() {

				Iterator<EntityId> propIdIter = localTypePropertyIds.iterator();

				for (OWLObjectAllValuesFrom restriction : findAllRequiredRestrictions()) {

					Concept target = extractSingleConceptFromFiller(restriction);

					source.addDynamicConstraintType(propIdIter.next(), target);
				}

				return localTypePropertyIds;
			}

			boolean requiredProperty(OWLObjectProperty property) {

				EntityId propertyId = getEntityId(property);

				if (!propertyId.dynamicId() || loadedTypePropertyIds.contains(propertyId)) {

					return false;
				}

				if (localTypePropertyIds.contains(propertyId)) {

					throw createDuplicateTypeException(propertyId);
				}

				localTypePropertyIds.add(propertyId);

				return true;
			}

			Concept fillerToConcept(OWLClass cls) {

				EntityId id = getEntityId(cls);
				Concept concept = model.lookForConcept(id);

				if (concept != null) {

					return concept;
				}

				return loadValueHierarchy(id, cls);
			}

			private Concept loadValueHierarchy(EntityId rootConceptId, OWLClass rootCls) {

				Hierarchy hierarchy = createValueHierarchy(rootConceptId);
				Concept rootConcept = hierarchy.getRootConcept();

				loadConceptsFrom(rootConcept, rootCls);

				return rootConcept;
			}

			private Hierarchy createValueHierarchy(EntityId rootConceptId) {

				return model.createDynamicValueHierarchy(rootConceptId);
			}

			private RuntimeException createDuplicateTypeException(EntityId propertyId) {

				return new RuntimeException(
							"Dynamic constraint-type already defined for property: "
							+ propertyId + ", on concept: " + getSourceClass());
			}
		}

		ConstraintTypeLoader(Hierarchy hierarchy) {

			Concept root = hierarchy.getRootConcept();

			loadFrom(root, getRootClass(root));
		}

		private void loadFrom(Concept concept, OWLClass cls) {

			Collection<EntityId> localTypePropertyIds = loadFor(concept, cls);

			loadedTypePropertyIds.addAll(localTypePropertyIds);

			for (OWLClass subCls : getSubClasses(cls, true)) {

				loadFrom(dynamicClassesToConcepts.get(subCls), subCls);
			}

			loadedTypePropertyIds.removeAll(localTypePropertyIds);
		}

		private Collection<EntityId> loadFor(Concept concept, OWLClass cls) {

			return new ClassConstraintTypeLoader(concept, cls).loadAllForClass();
		}
	}

	private abstract class PropertyConstraintLoader {

		private ConstraintType type;

		private OWLObjectProperty targetProperty;

		private AllTargetExtractor allTargetExtractor;
		private SomeTargetExtractor someTargetExtractor;

		private abstract class TargetExtractor
									<R extends OWLQuantifiedObjectRestriction>
									extends TypeRestrictionReader<R> {

			TargetExtractor(OWLClass sourceCls, Class<R> restrictionType) {

				super(sourceCls, restrictionType);
			}

			boolean requiredProperty(OWLObjectProperty property) {

				return property.equals(targetProperty);
			}
		}

		private class AllTargetExtractor extends TargetExtractor<OWLObjectAllValuesFrom> {

			AllTargetExtractor(OWLClass sourceCls) {

				super(sourceCls, OWLObjectAllValuesFrom.class);
			}

			Set<Concept> lookForAllTargetConcepts() {

				OWLObjectAllValuesFrom restriction = findAtMostOneRequiredRestriction();

				return restriction != null ? extractFillerConcepts(restriction) : null;
			}
		}

		private class SomeTargetExtractor extends TargetExtractor<OWLObjectSomeValuesFrom> {

			SomeTargetExtractor(OWLClass sourceCls) {

				super(sourceCls, OWLObjectSomeValuesFrom.class);
			}

			Set<Concept> findAllSomeTargetConcepts() {

				Set<Concept> concepts = new HashSet<Concept>();

				for (OWLObjectSomeValuesFrom restriction : findAllRequiredRestrictions()) {

					concepts.add(extractSingleConceptFromFiller(restriction));
				}

				return concepts;
			}
		}

		PropertyConstraintLoader(PropertyConstraintType type, OWLClass sourceCls) {

			this.type = type;

			targetProperty = getCoreObjectProperty(type.getTargetPropertyId());

			allTargetExtractor = new AllTargetExtractor(sourceCls);
			someTargetExtractor = new SomeTargetExtractor(sourceCls);
		}

		void checkLoad(Concept source) {

			if (type.definesValidValues()) {

				checkLoadValidValuesConstraint(source);
			}

			if (type.definesImpliedValues()) {

				checkLoadImpliedValueConstraints(source);
			}
		}

		private void checkLoadValidValuesConstraint(Concept source) {

			Set<Concept> targets = allTargetExtractor.lookForAllTargetConcepts();

			if (targets != null) {

				source.addValidValuesConstraint(type, targets);
			}
		}

		private void checkLoadImpliedValueConstraints(Concept source) {

			for (Concept target : someTargetExtractor.findAllSomeTargetConcepts()) {

				source.addImpliedValueConstraint(type, target);
			}
		}
	}

	private class SimpleConstraintLoader extends PropertyConstraintLoader {

		SimpleConstraintLoader(SimpleConstraintType type, OWLClass sourceCls) {

			super(type, sourceCls);

			checkLoad(getConcept(sourceCls));
		}
	}

	private class AnchoredConstraintLoader extends PropertyConstraintLoader {

		private OWLClass anchor;
		private OWLClass anchorSub;

		private class SourceExtractor extends TypeRestrictionReader<OWLObjectSomeValuesFrom> {

			private OWLObjectProperty sourceProperty;

			SourceExtractor(AnchoredConstraintType type) {

				super(anchorSub, OWLObjectSomeValuesFrom.class);

				sourceProperty = getCoreObjectProperty(type.getSourcePropertyId());
			}

			Concept lookForSourceConcept() {

				OWLObjectSomeValuesFrom restriction = lookForRequiredRestriction();

				if (restriction != null) {

					return extractSingleConceptFromFiller(restriction);
				}

				return null;
			}

			boolean requiredProperty(OWLObjectProperty property) {

				return property.equals(sourceProperty);
			}

			private OWLObjectSomeValuesFrom lookForRequiredRestriction() {

				OWLClassExpression equivExpr = lookForAnchorSubEquivExpr();

				if (equivExpr != null) {

					Set<OWLClassExpression> ops = asIntersection(equivExpr).getOperands();

					if (!ops.remove(anchor)) {

						throw createBadAxiomsException();
					}

					return asRequiredRestriction(extractExactlyOne(ops));
				}

				return null;
			}

			private OWLClassExpression lookForAnchorSubEquivExpr() {

				Set<OWLClassExpression> exprs = new HashSet<OWLClassExpression>();

				for (OWLEquivalentClassesAxiom axiom : getSourceAxioms(OWLEquivalentClassesAxiom.class)) {

					exprs.add(extractAnchorSubEquivExpr(axiom));
				}

				return extractExactlyOne(exprs);
			}

			private OWLClassExpression extractAnchorSubEquivExpr(OWLEquivalentClassesAxiom axiom) {

				Set<OWLClassExpression> exprs = axiom.getClassExpressions();

				if (!exprs.remove(anchorSub)) {

					throw createBadAxiomsException();
				}

				return extractExactlyOne(exprs);
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

			super(type, anchorSub);

			this.anchor = anchor;
			this.anchorSub = anchorSub;

			Concept source = new SourceExtractor(type).lookForSourceConcept();

			if (source != null) {

				checkLoad(source);
			}
		}
	}

	private class HierarchicalConstraintLoader {

		private HierarchicalConstraintType type;

		HierarchicalConstraintLoader(HierarchicalConstraintType type, OWLClass sourceCls) {

			this.type = type;

			loadAll(sourceCls);
		}

		private void loadAll(OWLClass sourceCls) {

			Concept source = getConcept(sourceCls);

			for (OWLClass targetCls : getSuperClasses(sourceCls, true)) {

				if (!rootSource(targetCls)) {

					checkLoad(source, targetCls);
				}
			}
		}

		private void checkLoad(Concept source, OWLClass targetCls) {

			Concept target = getConcept(targetCls);

			if (validTarget(target)) {

				source.addImpliedValueConstraint(type, target);
			}
		}

		private boolean rootSource(OWLClass cls) {

			return getEntityId(cls).equals(type.getRootSourceConcept().getConceptId());
		}

		private boolean validTarget(Concept target) {

			return target.descendantOf(type.getRootTargetConcept());
		}
	}

	DynamicModelLoader(
		Model model,
		Ontology ontology,
		DynamicIRIs dynamicIRIs)
		throws BadDynamicOntologyException {

		this.model = model;
		this.ontology = ontology;

		entityIds = new EntityIds(dynamicIRIs);

		try {

			loadConcepts();
			loadConstraintTypes();
			loadConstraints();
		}
		catch (RuntimeException e) {

			throw new BadDynamicOntologyException(e);
		}
	}

	private void loadConcepts() {

		for (Hierarchy hierarchy : model.getAllHierarchies()) {

			Concept root = hierarchy.getRootConcept();

			loadConceptsFrom(root, getRootClass(root));
		}
	}

	private void loadConceptsFrom(Concept concept, OWLClass cls) {

		for (OWLClass subCls : getSubClasses(cls, true)) {

			if (!dynamicClassesToConcepts.containsKey(subCls)) {

				Concept subConcept = addSubConcept(concept, subCls);

				dynamicClassesToConcepts.put(subCls, subConcept);

				loadConceptsFrom(subConcept, subCls);
			}
		}
	}

	private void loadConstraintTypes() {

		for (Hierarchy hierarchy : model.getDynamicHierarchies()) {

			new ConstraintTypeLoader(hierarchy);
		}
	}

	private void loadConstraints() {

		for (Hierarchy hierarchy : model.getDynamicHierarchies()) {

			for (ConstraintType type : hierarchy.getCoreConstraintTypes()) {

				loadConstraints(type);
			}
		}
	}

	private void loadConstraints(ConstraintType type) {

		if (type instanceof SimpleConstraintType) {

			loadSimpleConstraints((SimpleConstraintType)type);
		}

		if (type instanceof AnchoredConstraintType) {

			loadAnchoredConstraints((AnchoredConstraintType)type);
		}

		if (type instanceof HierarchicalConstraintType) {

			loadHierarchicalConstraints((HierarchicalConstraintType)type);
		}
	}

	private void loadSimpleConstraints(SimpleConstraintType type) {

		OWLClass rootSource = getCoreClass(type.getRootSourceConcept());

		for (OWLClass source : getSubClasses(rootSource, false)) {

			new SimpleConstraintLoader(type, source);
		}
	}

	private void loadAnchoredConstraints(AnchoredConstraintType type) {

		OWLClass anchor = getCoreClass(type.getAnchorConceptId());

		for (OWLClass anchorSub : getSubClasses(anchor, false)) {

			new AnchoredConstraintLoader(type, anchor, anchorSub);
		}
	}

	private void loadHierarchicalConstraints(HierarchicalConstraintType type) {

		OWLClass rootSource = getCoreClass(type.getRootSourceConcept());

		for (OWLClass source : getSubClasses(rootSource, false)) {

			new HierarchicalConstraintLoader(type, source);
		}
	}

	private Concept addSubConcept(Concept concept, OWLClass subCls) {

		EntityId childId = getEntityId(subCls);

		if (!childId.dynamicId()) {

			EntityId parentId = concept.getConceptId();

			if (parentId.dynamicId()) {

				throw createNonFixedParentException(parentId, childId);
			}
		}

		return concept.addChild(childId);
	}

	private Set<OWLClass> getSubClasses(OWLClass cls, boolean direct) {

		return ontology.getSubClasses(cls, direct);
	}

	private Set<OWLClass> getSuperClasses(OWLClass cls, boolean direct) {

		return ontology.getSuperClasses(cls, direct);
	}

	private OWLClass getRootClass(Concept concept) {

		IRI iri = getCoreIRI(concept.getConceptId());

		if (ontology.classExists(iri)) {

			return ontology.getClass(iri);
		}

		throw new RuntimeException("Cannot find hierarchy root-class: " + iri);
	}

	private OWLClass getCoreClass(Concept concept) {

		return getCoreClass(concept.getConceptId());
	}

	private OWLClass getCoreClass(EntityId id) {

		return ontology.getClass(getCoreIRI(id));
	}

	private OWLObjectProperty getCoreObjectProperty(EntityId id) {

		return ontology.getObjectProperty(getCoreIRI(id));
	}

	private IRI getCoreIRI(EntityId id) {

		if (id instanceof CoreId) {

			return ((CoreId)id).getIRI();
		}

		throw new RuntimeException("Unexpected dynamic entity: " + id);
	}

	private Concept getConcept(OWLClass cls) {

		Concept concept = model.lookForConcept(getEntityId(cls));

		if (concept != null) {

			return concept;
		}

		throw new RuntimeException("Referenced concept not loaded: " + cls);
	}

	private EntityId getEntityId(OWLEntity entity) {

		return entityIds.getId(entity, ontology.lookForLabel(entity));
	}

	private <T>T asTypeOrNull(Object obj, Class<T> type) {

		return type.isAssignableFrom(obj.getClass()) ? type.cast(obj) : null;
	}

	private RuntimeException createNonFixedParentException(EntityId parentId, EntityId childId) {

		return new RuntimeException(
						"Cannot add fixed child to dynamic parent concept: "
						+ childId + "-->" + parentId);
	}
}
