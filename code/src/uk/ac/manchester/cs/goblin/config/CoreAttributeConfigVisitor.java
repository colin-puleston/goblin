package uk.ac.manchester.cs.goblin.config;

/**
 * @author Colin Puleston
 */
 public abstract class CoreAttributeConfigVisitor {

	public void visit(CoreAttributeConfig config) {

		config.accept(this);
	}

	public abstract void visit(SimpleAttributeConfig config);

	public abstract void visit(AnchoredAttributeConfig config);

	public abstract void visit(HierarchicalAttributeConfig config);
}
