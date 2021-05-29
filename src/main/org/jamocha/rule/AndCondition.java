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

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Constants;
import org.jamocha.rete.GraphQueryCompiler;
import org.jamocha.rete.QueryCompiler;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.compiler.CompilerProvider;
import org.jamocha.rete.compiler.ConditionCompiler;


/**
 * @author Peter Lin
 *
 * AndCondition is specifically created to handle and conjunctions. AndConditions
 * are compiled to a BetaNode.
 */
public class AndCondition implements Condition {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
	protected List<Object> nestedCE = new ArrayList<Object>();
    protected BaseJoin reteNode = null;
    
	/**
	 * 
	 */
	public AndCondition() {
		super();
	}

	public boolean compare(Condition cond) {
		return false;
	}

    public void addNestedConditionElement(Object ce) {
        this.nestedCE.add(ce);
    }
    
    public void addAll(java.util.List<?> list) {
        if (list != null) {
            this.nestedCE.addAll(list);
        }
    }
    
	public List<Object> getNestedConditionalElement() {
        return this.nestedCE;
    }
    
    public Condition[] getConditions() {
        Condition[] conditions = new Condition[this.nestedCE.size()];
        return this.nestedCE.toArray(conditions);
    }
    
	public List<?> getNodes() {
		return new ArrayList<Object>();
	}

    /**
     * the method doesn't apply and isn't implemented currently
     */
	public void addNode(BaseNode node) {
	}
    
    /**
     * not implemented currently
     */
    public void addNewAlphaNodes(BaseNode node) {
        
    }

	public BaseNode getLastNode() {
		return reteNode;
	}
	
	
    
    public void clear() {
    	reteNode = null;
    }
    
	public String toPPString() {
        StringBuffer buf = new StringBuffer();
        String pad = "  ";
        buf.append(pad + "(and" + Constants.LINEBREAK);
        for (int idx=0; idx < this.nestedCE.size(); idx++) {
            Condition c = (Condition)nestedCE.get(idx);
            if (c instanceof TestCondition) {
                buf.append(pad + c.toPPString());
            } else if (c instanceof ObjectCondition) {
                ObjectCondition oc = (ObjectCondition)c;
                buf.append(oc.toPPString(2));
            }
        }
        buf.append(pad + ")" + Constants.LINEBREAK);
		return buf.toString();
	}

	public ConditionCompiler getCompiler(RuleCompiler ruleCompiler) {
		CompilerProvider.getInstance(ruleCompiler);
		return CompilerProvider.andConditionCompiler;
	}

	public ConditionCompiler getCompiler(QueryCompiler ruleCompiler) {
		CompilerProvider.getInstance(ruleCompiler);
		return CompilerProvider.andConditionCompiler;
	}
	
	public ConditionCompiler getCompiler(GraphQueryCompiler ruleCompiler) {
		CompilerProvider.getInstance(ruleCompiler);
		return CompilerProvider.andConditionCompiler;
	}
	
	public List<?> getBindConstraints() {
		return null;
	}
}
