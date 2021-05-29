/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rule;

import org.jamocha.rete.Constants;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.compiler.CompilerProvider;
import org.jamocha.rete.compiler.ConditionCompiler;

/**
 * @author Peter Lin
 *
 * ExistCondition for existential quantifier.
 */
public class ExistCondition extends ObjectCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ExistCondition() {
		super();
	}

    @SuppressWarnings("unchecked")
	public void addConstraint(Constraint con) {
        this.constraints.add(con);
        if (con instanceof BoundConstraint) {
        	((BoundConstraint)con).setBindableConstraint(false);
        }
    }
    
    @SuppressWarnings("unchecked")
	public void addConstraint(Constraint con, int position) {
        this.constraints.add(0,con);
        if (con instanceof BoundConstraint) {
        	((BoundConstraint)con).setBindableConstraint(false);
        }
    }
    
	public String toPPString() {
        StringBuffer buf = new StringBuffer();
        int start = 0;
        String pad = "  ";
        buf.append(pad + "(exists" + Constants.LINEBREAK);
        pad = "    ";
        buf.append(pad + "(" + getTemplateName() + Constants.LINEBREAK);
        for (int idx=start; idx < getConstraints().length; idx++) {
            Constraint cnstr = (Constraint)getConstraints()[idx];
            buf.append("  " + cnstr.toPPString());
        }
        buf.append(pad + ")" + Constants.LINEBREAK);
        pad = "  ";
        buf.append(pad + ")" + Constants.LINEBREAK);
        return buf.toString();
	}

	public ConditionCompiler getCompiler(RuleCompiler ruleCompiler) {
		CompilerProvider.getInstance(ruleCompiler);
		return CompilerProvider.existConditionCompiler;
	}
    
    public static ExistCondition newExistCondition(ObjectCondition cond) {
        ExistCondition exc = new ExistCondition();
        exc.constraints = cond.constraints;
        exc.negated = cond.negated;
        exc.nodes = cond.nodes;
        exc.template = cond.template;
        exc.templateName = cond.templateName;
        return exc;
    }
}
