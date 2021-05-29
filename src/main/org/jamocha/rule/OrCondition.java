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
import org.jamocha.rete.GraphQueryCompiler;
import org.jamocha.rete.QueryCompiler;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.compiler.ConditionCompiler;


/**
 * @author Peter Lin
 *
 * AndCondition is specifically created to handle and conjunctions. AndConditions
 * are compiled to a BetaNode.
 */
public class OrCondition implements Condition {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected List<Object> nestedCE = new ArrayList<Object>();
    protected BaseJoin reteNode = null;
    
	/**
	 * 
	 */
	public OrCondition() {
		super();
	}

	public boolean compare(Condition cond) {
		if (!(cond instanceof OrCondition)) {
			return false;
		}
		OrCondition orc = (OrCondition)cond;
		if (orc.getNestedConditionalElement().size() == this.nestedCE.size()) {
			return true;
		} else {
			return false;
		}
	}

    public void addNestedConditionElement(Object ce) {
        this.nestedCE.add(ce);
    }
    
    public List<Object> getNestedConditionalElement() {
        return this.nestedCE;
    }
    
	public List<?> getNodes() {
		return new ArrayList<Object>();
	}

    /**
     * not implemented yet
     */
	public void addNode(BaseNode node) {
	}
    
    /**
     * not implemented yet
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
		return "";
	}

	public ConditionCompiler getCompiler(RuleCompiler ruleCompiler) {
		// TODO Auto-generated method stub
		return null;
	}

	public ConditionCompiler getCompiler(QueryCompiler ruleCompiler) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ConditionCompiler getCompiler(GraphQueryCompiler ruleCompiler) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<?> getBindConstraints() {
		// TODO Auto-generated method stub
		return null;
	}
}
