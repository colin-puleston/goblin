package uk.ac.manchester.cs.goblin.config;

import uk.ac.manchester.cs.goblin.model.*;

/**
 * @author Colin Puleston
 */
 public abstract class AttributeVisitor {

	 private class CoreAttributeVisitInvoker extends CoreAttributeConfigVisitor {

		private CoreAttribute attribute;

		public void visit(SimpleAttributeConfig config) {

			AttributeVisitor.this.visit(attribute, config);
		}

		public void visit(AnchoredAttributeConfig config) {

			AttributeVisitor.this.visit(attribute, config);
		}

		public void visit(HierarchicalAttributeConfig config) {

			AttributeVisitor.this.visit(attribute, config);
		}

		CoreAttributeVisitInvoker(CoreAttribute attribute) {

			this.attribute = attribute;

			visit(attribute.getConfig());
		}
	}

	public void visit(Attribute attribute) {

		if (attribute instanceof CoreAttribute) {

			new CoreAttributeVisitInvoker((CoreAttribute)attribute);
		}
		else if (attribute instanceof DynamicAttribute) {

			visit((DynamicAttribute)attribute);
		}
		else {

			throw new Error("Unrecognised Attribute type: " + attribute);
		}
	}

	public abstract void visit(CoreAttribute attribute, SimpleAttributeConfig config);

	public abstract void visit(CoreAttribute attribute, AnchoredAttributeConfig config);

	public abstract void visit(CoreAttribute attribute, HierarchicalAttributeConfig config);

	public abstract void visit(DynamicAttribute attribute);
}
