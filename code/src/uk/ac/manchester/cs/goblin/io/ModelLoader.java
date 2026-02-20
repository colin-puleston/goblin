package uk.ac.manchester.cs.goblin.io;

import java.util.*;

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.goblin.model.*;
import uk.ac.manchester.cs.goblin.ontology.*;
import uk.ac.manchester.cs.goblin.io.config.*;

/**
 * @author Colin Puleston
 */
class ModelLoader {

	private Model model;
	private Ontology ontology;

	private EntityIds entityIds;

	private Map<OWLClass, Concept> dynamicClassesToConcepts = new HashMap<OWLClass, Concept>();

	private AttributeConstraintLoader attributeConstraintLoader = new AttributeConstraintLoader();

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

	private class DynamicAttributeLoader {

		private Set<EntityId> loadedAttributeIds = new HashSet<EntityId>();

		private class ClassAttributeLoader
						extends
							TypeRestrictionReader<OWLObjectAllValuesFrom> {

			private Concept source;
			private List<EntityId> localAttributeIds = new ArrayList<EntityId>();

			ClassAttributeLoader(Concept source, OWLClass sourceCls) {

				super(sourceCls, OWLObjectAllValuesFrom.class);

				this.source = source;
			}

			Collection<EntityId> loadAllForClass() {

				int i = 0;

				for (OWLObjectAllValuesFrom restriction : findAllRequiredRestrictions()) {

					Concept target = extractSingleConceptFromFiller(restriction);

					source.addDynamicAttribute(localAttributeIds.get(i++), target);
				}

				return localAttributeIds;
			}

			boolean requiredProperty(OWLObjectProperty property) {

				EntityId attrId = getEntityId(property);

				if (!attrId.dynamicId() || loadedAttributeIds.contains(attrId)) {

					return false;
				}

				if (localAttributeIds.contains(attrId)) {

					throw createDuplicateAttributeIdException(attrId);
				}

				localAttributeIds.add(attrId);

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

			private RuntimeException createDuplicateAttributeIdException(EntityId attrId) {

				return new RuntimeException(
							"Dynamic attribute already defined for property: "
							+ attrId + ", on concept: " + getSourceClass());
			}
		}

		DynamicAttributeLoader(Hierarchy hierarchy) {

			Concept root = hierarchy.getRootConcept();

			loadFrom(root, getRootClass(root));
		}

		private void loadFrom(Concept concept, OWLClass cls) {

			Collection<EntityId> localAttributeIds = loadFor(concept, cls);

			loadedAttributeIds.addAll(localAttributeIds);

			for (OWLClass subCls : getSubClasses(cls, true)) {

				loadFrom(dynamicClassesToConcepts.get(subCls), subCls);
			}

			loadedAttributeIds.removeAll(localAttributeIds);
		}

		private Collection<EntityId> loadFor(Concept concept, OWLClass cls) {

			return new ClassAttributeLoader(concept, cls).loadAllForClass();
		}
	}

	private class AttributeConstraintLoader extends AttributeVisitor {

		public void visit(CoreAttribute attribute, SimpleAttributeConfig config) {

			OWLClass rootSource = getCoreClass(attribute.getRootSourceConcept());
			OWLObjectProperty linkingProp = getCoreObjectProperty(config.getLinkingPropertyId());

			loadLinkingPropertyConstraints(attribute, rootSource, linkingProp);
		}

		public void visit(CoreAttribute attribute, AnchoredAttributeConfig config) {

			OWLClass anchor = getCoreClass(config.getAnchorConceptId());
			OWLObjectProperty sourceProp = getCoreObjectProperty(config.getSourcePropertyId());
			OWLObjectProperty targetProp = getCoreObjectProperty(config.getTargetPropertyId());

			for (OWLClass anchorSub : getSubClasses(anchor, false)) {

				new AnchoredConstraintLoader(attribute, anchor, anchorSub, sourceProp, targetProp);
			}
		}

		public void visit(CoreAttribute attribute, HierarchicalAttributeConfig config) {

			OWLClass rootSource = getCoreClass(attribute.getRootSourceConcept());

			for (OWLClass source : getSubClasses(rootSource, false)) {

				new HierarchicalConstraintLoader(attribute, source);
			}
		}

		public void visit(DynamicAttribute attribute) {

			OWLClass rootSource = getDynamicClass(attribute.getRootSourceConcept());
			OWLObjectProperty linkingProp = getDynamicObjectProperty(attribute.getAttributeId());

			loadLinkingPropertyConstraints(attribute, rootSource, linkingProp);
		}

		private void loadLinkingPropertyConstraints(
							Attribute attribute,
							OWLClass rootSource,
							OWLObjectProperty linkingProperty) {

			for (OWLClass source : getSubClasses(rootSource, false)) {

				new LinkingPropertyConstraintLoader(attribute, source, linkingProperty);
			}
		}
	}

	private abstract class PropertyConstraintLoader {

		private Attribute attribute;

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

		PropertyConstraintLoader(
			Attribute attribute,
			OWLClass sourceCls,
			OWLObjectProperty targetProperty) {

			this.attribute = attribute;
			this.targetProperty = targetProperty;

			allTargetExtractor = new AllTargetExtractor(sourceCls);
			someTargetExtractor = new SomeTargetExtractor(sourceCls);
		}

		void checkLoad(Concept source) {

			ConstraintsOption constraintsOpt = attribute.getConstraintsOption();

			if (constraintsOpt.validValues()) {

				checkLoadValidValuesConstraint(source);
			}

			if (constraintsOpt.impliedValues()) {

				checkLoadImpliedValueConstraints(source);
			}
		}

		private void checkLoadValidValuesConstraint(Concept source) {

			Set<Concept> targets = allTargetExtractor.lookForAllTargetConcepts();

			if (targets != null) {

				source.addValidValuesConstraint(attribute, targets);
			}
		}

		private void checkLoadImpliedValueConstraints(Concept source) {

			for (Concept target : someTargetExtractor.findAllSomeTargetConcepts()) {

				source.addImpliedValueConstraint(attribute, target);
			}
		}
	}

	private class LinkingPropertyConstraintLoader extends PropertyConstraintLoader {

		LinkingPropertyConstraintLoader(
			Attribute attribute,
			OWLClass sourceCls,
			OWLObjectProperty linkingProperty) {

			super(attribute, sourceCls, linkingProperty);

			checkLoad(getConcept(sourceCls));
		}
	}

	private class AnchoredConstraintLoader extends PropertyConstraintLoader {

		private OWLClass anchor;
		private OWLClass anchorSub;

		private class SourceExtractor extends TypeRestrictionReader<OWLObjectSomeValuesFrom> {

			private OWLObjectProperty sourceProperty;

			SourceExtractor(OWLObjectProperty sourceProperty) {

				super(anchorSub, OWLObjectSomeValuesFrom.class);

				this.sourceProperty = sourceProperty;
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

		AnchoredConstraintLoader(
			Attribute attribute,
			OWLClass anchor,
			OWLClass anchorSub,
			OWLObjectProperty sourceProperty,
			OWLObjectProperty targetProperty) {

			super(attribute, anchorSub, targetProperty);

			this.anchor = anchor;
			this.anchorSub = anchorSub;

			Concept source = new SourceExtractor(sourceProperty).lookForSourceConcept();

			if (source != null) {

				checkLoad(source);
			}
		}
	}

	private class HierarchicalConstraintLoader {

		private Attribute attribute;

		HierarchicalConstraintLoader(Attribute attribute, OWLClass sourceCls) {

			this.attribute = attribute;

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

				source.addImpliedValueConstraint(attribute, target);
			}
		}

		private boolean rootSource(OWLClass cls) {

			return getEntityId(cls).equals(attribute.getRootSourceConcept().getConceptId());
		}

		private boolean validTarget(Concept target) {

			return target.descendantOf(attribute.getRootTargetConcept());
		}
	}

	ModelLoader(Ontology ontology, ModelConfig modelConfig, DynamicIRIs dynamicIRIs) {

		this.ontology = ontology;

		model = modelConfig.createModel();
		entityIds = new EntityIds(dynamicIRIs);
	}

	Model load() throws BadDynamicOntologyException {

		try {

			loadConcepts();
			loadDynamicAttributes();
			loadConstraints();
		}
		catch (RuntimeException e) {

			throw new BadDynamicOntologyException(e);
		}

		return model;
	}

	private void loadConcepts() {

		for (Hierarchy hierarchy : model.getCoreHierarchies()) {

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

	private void loadDynamicAttributes() {

		for (Hierarchy hierarchy : model.getCoreHierarchies()) {

			if (hierarchy.dynamicAttributesEnabled()) {

				new DynamicAttributeLoader(hierarchy);
			}
		}
	}

	private void loadConstraints() {

		for (Hierarchy hierarchy : model.getCoreHierarchies()) {

			if (hierarchy.potentiallyHasAttributes()) {

				for (Attribute attribute : hierarchy.getAllAttributes()) {

					attributeConstraintLoader.visit(attribute);
				}
			}
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

	private OWLClass getDynamicClass(Concept concept) {

		return getDynamicClass(concept.getConceptId());
	}

	private OWLClass getDynamicClass(EntityId id) {

		return ontology.getClass(getDynamicIRI(id));
	}

	private OWLObjectProperty getDynamicObjectProperty(EntityId id) {

		return ontology.getObjectProperty(getDynamicIRI(id));
	}

	private IRI getCoreIRI(EntityId id) {

		return entityIds.toCoreIRI(id);
	}

	private IRI getDynamicIRI(EntityId id) {

		return entityIds.toDynamicIRI(id);
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
