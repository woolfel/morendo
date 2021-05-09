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
 * OnlyCondition is a special case of exist when there is
 * only 1 match for the given pattern.
 */
public class OnlyCondition extends ObjectCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public OnlyCondition() {
		super();
	}

	public String toPPString() {
        StringBuffer buf = new StringBuffer();
        int start = 0;
        String pad = "  ";
        buf.append(pad + "(only" + Constants.LINEBREAK);
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
		return CompilerProvider.getInstance(ruleCompiler).onlyConditionCompiler;
	}
    
    public static OnlyCondition newOnlyCondition(ObjectCondition cond) {
        OnlyCondition only = new OnlyCondition();
        only.constraints = cond.constraints;
        only.negated = cond.negated;
        only.nodes = cond.nodes;
        only.template = cond.template;
        only.templateName = cond.templateName;
        return only;
    }
}
