package org.jamocha.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.BaseAlpha;
import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Template;
import org.jamocha.rete.query.QueryBaseAlpha;

public abstract class AbstractCondition implements Condition {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The string template name from the parser, before we
     * resolve it to the Template object
     */
    protected String templateName = null;
    /**
     * the constraints for the condition element
     */
    @SuppressWarnings("rawtypes")
	protected List constraints = new ArrayList(8);
    /**
     * In the case the object pattern is negated, the boolean
     * would be set to true.
     */
    protected boolean negated = false;
    /**
     * a list for the RETE nodes created by RuleCompiler
     */
    @SuppressWarnings("rawtypes")
	protected List nodes = new ArrayList();
    /**
     * the deftemplate associated with the ObjectCondition
     */
    protected Template template = null;
    /**
     * The potential memory cost based on the maximum partial match
     * count. It is calculated based on the distinct values for each
     * slot.
     */
    protected long partialMatchCount = 0;
    
    @SuppressWarnings("unchecked")
	public void addNewAlphaNodes(BaseNode node) {
    	if (node != null) {
            if (!nodes.contains(node)) {
                nodes.add(node);
            }
            for(int i=0;i<node.getSuccessorNodes().length;i++){
                nodes.add(node.getSuccessorNodes()[i]);
                if (node instanceof BaseAlpha) {
                    addNewAlphaNodes(((BaseAlpha)node.getSuccessorNodes()[i]));
                } else if (node instanceof QueryBaseAlpha) {
                    addNewAlphaNodes(((QueryBaseAlpha)node.getSuccessorNodes()[i]));
                }
            }
    	}
    }

    @SuppressWarnings("unchecked")
	public void addNode(BaseNode node) {
        if (!this.nodes.contains(node)) {
            this.nodes.add(node);
        }
    }

    public void clear() {
        nodes.clear();
    }

    public abstract boolean compare(Condition cond);

    /**
     * returns the bindings, excluding predicateConstraints
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List getBindConstraints() {
        ArrayList binds = new ArrayList();
        Iterator itr = constraints.iterator();
        while (itr.hasNext()) {
            Object c = itr.next();
            if (c instanceof BoundConstraint) {
                BoundConstraint bc = (BoundConstraint)c;
                if (!bc.firstDeclaration() && !bc.getIsObjectBinding()) {
                    binds.add(c);
                }
            } else if (c instanceof PredicateConstraint) {
                PredicateConstraint pc = (PredicateConstraint)c;
                if (pc.isPredicateJoin()) {
                    binds.add(pc);
                }
            }
        }
        return binds;
    }

    public BaseNode getFirstNode() {
        if (this.nodes.size() > 0) {
            return (BaseNode)this.nodes.get(0);
        } else {
            return null;
        }
    }
    
    public BaseNode getLastNode() {
        if (this.nodes.size() > 0) {
            return (BaseNode)this.nodes.get(nodes.size() -1);
        } else {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
	public List getNodes() {
        return this.nodes;
    }

    /**
     * Subclasses must implement this method
     */
    public abstract String toPPString();

    public String getTemplateName() {
        return this.templateName;
    }
    
    public void setTemplateName(String name) {
        this.templateName = name;
    }
    
    public Template getTemplate() {
        return this.template;
    }
    
    public void setTemplate(Template tmpl) {
        this.template = tmpl;
    }
    
    /**
     * set whether or not the pattern is negated
     * @param negate
     */
    public void setNegated(boolean negate) {
        this.negated = negate;
    }
    
    /**
     * by default patterns are not negated. Negated Conditional Elements
     * (aka object patterns) are expensive, so they should be used with 
     * care.
     * @return
     */
    public boolean getNegated() {
        return this.negated;
    }
    
    @SuppressWarnings("unchecked")
	public Constraint[] getConstraints() {
        Constraint[] con = new Constraint[constraints.size()];
        return (Constraint[])constraints.toArray(con);
    }
    
    @SuppressWarnings("unchecked")
	public void addConstraint(Constraint con) {
        this.constraints.add(con);
    }
    
    @SuppressWarnings("unchecked")
	public void addConstraint(Constraint con, int position) {
        this.constraints.add(0,con);
    }
    
    public void removeConstraint(Constraint con) {
        this.constraints.remove(con);
    }
    
    public void setPartialMatchCount(long matchCount) {
    	this.partialMatchCount = matchCount;
    }
    
    public long getPartialMatchCount() {
    	return this.partialMatchCount;
    }
}
