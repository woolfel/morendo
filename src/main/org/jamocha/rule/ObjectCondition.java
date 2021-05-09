/*
 * Copyright 2002-2009 Peter Lin
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

import org.jamocha.rete.*;
import org.jamocha.rete.compiler.CompilerProvider;
import org.jamocha.rete.compiler.ConditionCompiler;


/**
 * @author Peter Lin
 *
 * ObjectCondition is equivalent to RuleML 0.83 resourceType. ObjectCondition
 * matches on the fields of an object. The patterns may be simple value
 * comparisons, or joins against other objects.
 */
public class ObjectCondition extends AbstractCondition {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//hasNotEqual and hasPredicateJoin determine which kind of joinNode to create
    private boolean hasNotEqual = false;
    
    private boolean hasPredicateJoin = false;

	/**
	 * 
	 */
	public ObjectCondition() {
		super();
	}
	
    public void addConstraint(Constraint con) {
        this.constraints.add(con);
        if (this.getNegated() && con instanceof BoundConstraint) {
        	((BoundConstraint)con).setBindableConstraint(false);
        }
    }
    
    public void addConstraint(Constraint con, int position) {
        this.constraints.add(0,con);
        if (this.getNegated() && con instanceof BoundConstraint) {
        	((BoundConstraint)con).setBindableConstraint(false);
        }
    }
    
    /**
     * TODO - currently we don't need it and it isn't implemented.
     * should finish implementing it.
     */
	public boolean compare(Condition cond) {
		return false;
	}

    /**
     * The current implementation expects the deffact or object binding
     * constriant to be first.
     */
    public String toPPString() {
    	StringBuffer buf = new StringBuffer();
    	int start = 0;
    	// this is a hack, but it keeps the code simple for spacing
    	// default indent for CE is 2 spaces
    	String pad = "  ";
        boolean obind = false;
    	Constraint cn = (Constraint)this.constraints.get(0);
    	if (cn instanceof BoundConstraint) {
    		BoundConstraint bc = (BoundConstraint)cn;
    		if (bc.getIsObjectBinding()) {
    			start = 1;
    			buf.append(bc.toFactBindingPPString());
    			// since the first Constraint is a fact binding we
    			// change the padding to 1 space
    			pad = " ";
                obind = true;
    		}
    	}
        if (this.negated) {
            buf.append(pad + "(not" + Constants.LINEBREAK);
            pad = "    ";
        }
    	buf.append(pad + "(" + this.templateName + Constants.LINEBREAK);
    	for (int idx=start; idx < this.constraints.size(); idx++) {
    		Constraint cnstr = (Constraint)this.constraints.get(idx);
            if (this.negated) {
                buf.append("  " + cnstr.toPPString());
            } else {
                buf.append(cnstr.toPPString());
            }
    	}
        if (this.negated) {
            buf.append(pad + ")" + Constants.LINEBREAK);
            pad = "  ";
        }
        if (obind && !this.negated) {
            buf.append(pad + " )" + Constants.LINEBREAK);
        } else {
            buf.append(pad + ")" + Constants.LINEBREAK);
        }
    	return buf.toString();
    }

    public String toPPString(int tabs) {
        StringBuffer buf = new StringBuffer();
        int tabCount = tabs;
        int start = 0;
        boolean obind = false;
        Constraint cn = (Constraint)this.constraints.get(0);
        if (cn instanceof BoundConstraint) {
            BoundConstraint bc = (BoundConstraint)cn;
            if (bc.getIsObjectBinding()) {
                start = 1;
                buf.append(bc.toFactBindingPPString());
                // since the first Constraint is a fact binding we
                // change the padding to 1 space
                obind = true;
            }
        }
        if (this.negated) {
            buf.append(padding(tabCount) + "(not" + Constants.LINEBREAK);
            tabCount++;
        }
        buf.append(padding(tabCount) + "(" + this.templateName + Constants.LINEBREAK);
        for (int idx=start; idx < this.constraints.size(); idx++) {
            Constraint cnstr = (Constraint)this.constraints.get(idx);
            if (this.negated) {
                buf.append(padding(tabCount) + cnstr.toPPString());
            } else {
                // tabCount - 1 since constraint toPPString already pads
                buf.append(padding(tabCount - 1) + cnstr.toPPString());
            }
        }
        if (this.negated) {
            buf.append(padding(tabCount) + ")" + Constants.LINEBREAK);
        }
        if (obind && !this.negated) {
            buf.append(padding(tabCount +1) + ")" + Constants.LINEBREAK);
        } else {
            buf.append(padding(tabCount) + ")" + Constants.LINEBREAK);
        }
        return buf.toString();
    }
    
    protected String padding(int count) {
        String pad = "";
        for (int idx=0; idx < count; idx++) {
            pad += "  ";
        }
        return pad;
    }
    
	public ConditionCompiler getCompiler(RuleCompiler ruleCompiler) {
		return CompilerProvider.getInstance(ruleCompiler).objectConditionCompiler;
	}
	
	public ConditionCompiler getCompiler(QueryCompiler ruleCompiler) {
		return CompilerProvider.getInstance(ruleCompiler).objectConditionCompiler;
	}
	
	public ConditionCompiler getCompiler(GraphQueryCompiler ruleCompiler) {
		return CompilerProvider.getInstance(ruleCompiler).objectConditionCompiler;
	}
	
	public boolean isHasNotEqual() {
		return hasNotEqual;
	}

	public void setHasNotEqual(boolean hasNotEqual) {
		this.hasNotEqual = hasNotEqual;
	}

	public boolean isHasPredicateJoin() {
		return hasPredicateJoin;
	}

	public void setHasPredicateJoin(boolean hasPredicateJoin) {
		this.hasPredicateJoin = hasPredicateJoin;
	}
}
