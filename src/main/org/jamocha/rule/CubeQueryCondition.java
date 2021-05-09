package org.jamocha.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.Constants;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.compiler.CompilerProvider;
import org.jamocha.rete.compiler.ConditionCompiler;

public class CubeQueryCondition extends ObjectCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CubeQueryCondition() {
		super();
	}

	public ConditionCompiler getCompiler(RuleCompiler ruleCompiler) {
		return CompilerProvider.getInstance(ruleCompiler).cubeQueryConditionCompiler;
	}
	
	public List getQueryConstraints() {
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
                binds.add(pc);
            }
        }
        return binds;
	}
    public String toPPString(int tabs) {
    	StringBuffer buf = new StringBuffer();
    	buf.append("(cubequery" + Constants.LINEBREAK);
    	buf.append(")" + Constants.LINEBREAK);
    	return buf.toString();
    }
}
