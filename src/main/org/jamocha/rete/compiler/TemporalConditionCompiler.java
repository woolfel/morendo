/*
 * Copyright 2002-2010 Peter Lin
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
package org.jamocha.rete.compiler;

import java.math.BigDecimal;

import org.jamocha.rete.AbstractTemporalNode;
import org.jamocha.rete.BaseAlpha;
import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Function;
import org.jamocha.rete.GraphQueryCompiler;
import org.jamocha.rete.LIANode;
import org.jamocha.rete.NotJoin;
import org.jamocha.rete.ObjectTypeNode;
import org.jamocha.rete.QueryCompiler;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.Template;
import org.jamocha.rete.TemporalEqNode;
import org.jamocha.rete.TemporalIntervalNode;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.AndLiteralConstraint;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.OrLiteralConstraint;
import org.jamocha.rule.PredicateConstraint;
import org.jamocha.rule.Rule;
import org.jamocha.rule.TemporalCondition;

/**
 * 
 * @author HouZhanbin
 * @author Peter Lin
 * Oct 12, 2007 9:42:15 AM
 *
 */
public class TemporalConditionCompiler extends ObjectConditionCompiler {
	
	public TemporalConditionCompiler(RuleCompiler ruleCompiler){
        super(ruleCompiler);
	}
	
	public TemporalConditionCompiler(QueryCompiler queryCompiler){
        super(queryCompiler);
	}
	
	public TemporalConditionCompiler(GraphQueryCompiler queryCompiler){
        super(queryCompiler);
	}
	
	/**
	 * Compile a single ObjectCondition and create the alphaNodes and/or Bindings
	 */
	public void compile(Condition condition, int position, Rule rule, boolean alphaMemory) {
		TemporalCondition cond = (TemporalCondition)condition;
        ObjectTypeNode otn = ruleCompiler.findObjectTypeNode(cond.getTemplateName());
        // we set remember match to false, since the rule is temporal
        boolean switchMatch = false;
        if (rule.getRememberMatch()) {
            rule.setRememberMatch(false);
            switchMatch = true;
        }
        if (otn != null) {
            BaseAlpha first = null;
            BaseAlpha previous = null;
            BaseAlpha current = null;
            Template templ = cond.getTemplate();

            Constraint[] constrs = cond.getConstraints();
            for (int idx=0; idx < constrs.length; idx++) {
                Constraint cnstr = constrs[idx];
                if (cnstr instanceof LiteralConstraint) {
                    current = 
                    	ruleCompiler.compileConstraint((LiteralConstraint)cnstr, templ, rule);
                } else if (cnstr instanceof AndLiteralConstraint) {
                    current = 
                    	ruleCompiler.compileConstraint((AndLiteralConstraint)cnstr, templ, rule);
                } else if (cnstr instanceof OrLiteralConstraint) {
                    current = 
                    	ruleCompiler.compileConstraint((OrLiteralConstraint)cnstr, templ, rule);
                } else if (cnstr instanceof BoundConstraint) {
                	ruleCompiler.compileConstraint((BoundConstraint)cnstr, templ, rule, position);
                } else if (cnstr instanceof PredicateConstraint) {
                    current = 
                    	ruleCompiler.compileConstraint((PredicateConstraint)cnstr, templ, rule, position);
                }
                // we add the node to the previous
                if (first == null) {
                    first = current;
                    previous = current;
                } else if (current != previous){
                    try {
                        previous.addSuccessorNode(current,ruleCompiler.getEngine(),ruleCompiler.getMemory());
                        // now set the previous to current
                        previous = current;
                    } catch (AssertException e) {
                        // send an event
                    }
                }
            }
            if (first != null) {
            	attachAlphaNode(otn,first,cond);
            }
        }
        
        if (!cond.getNegated()) {
        	position++;
        }
        if (switchMatch) {
            rule.setRememberMatch(true);
        }
	}
    
    public void compileFirstJoin(Condition condition, Rule rule) throws AssertException{
        ObjectCondition cond = (ObjectCondition) condition;
        ObjectTypeNode otn = ruleCompiler.findObjectTypeNode(cond.getTemplateName());
        // the LeftInputAdapterNode is the first node to propogate to
        // the first joinNode of the rule
        LIANode node = new LIANode(ruleCompiler.getEngine().nextNodeId());
        // if the condition doesn't have any nodes, we want to add it to
        // the objectType node if one doesn't already exist.
        // otherwise we add it to the last AlphaNode
        if (cond.getNodes().size() == 0) {
            // try to find the existing LIANode for the given ObjectTypeNode
            // if we don't do this, we end up with multiple LIANodes
            // descending directly from the ObjectTypeNode
            LIANode existingLIANode = ruleCompiler.findLIANode(otn);
            if (existingLIANode == null) {
                otn.addSuccessorNode(node, ruleCompiler.getEngine(), ruleCompiler.getMemory());
                cond.addNode(node);
            } else {
                existingLIANode.incrementUseCount();
                cond.addNode(existingLIANode);
            }
        } else {
            // add the LeftInputAdapterNode to the last alphaNode
            // In the case of node sharing, the LIANode could be the last
            // alphaNode, so we have to check and only add the node to 
            // the condition if it isn't a LIANode
            BaseAlpha old = (BaseAlpha) cond.getLastNode();
            //if the last node of condition has a LIANode successor,
            //the LIANode should be shared with the new CE followed by another CE.
            // Houzhanbin,10/16/2007
                BaseNode[] successors=(BaseNode[])old.getSuccessorNodes();
                for(int i=0;i<successors.length;i++){
                    if(successors[i] instanceof LIANode){
                        cond.addNode(successors[i]);
                        return;
                    }
                }

            if (!(old instanceof LIANode)) {
                old.addSuccessorNode(node, ruleCompiler.getEngine(), ruleCompiler.getMemory());
                cond.addNode(node);
            }
        }
    }
    
    public BaseJoin compileJoin(Condition condition, int position, Rule rule, Condition previousCond) {
        
        Binding[] binds = getBindings(condition,rule,position);
        TemporalCondition tc = (TemporalCondition)condition;
        AbstractTemporalNode joinNode = null;
        //deal with the CE which is not NOT CE.
        if ( !tc.getNegated() ) {
            if (binds.length > 0 && tc.getIntervalTime() > 0) {
                joinNode = new TemporalIntervalNode(ruleCompiler.getEngine().nextNodeId(), ruleCompiler.getEngine());
                ((TemporalIntervalNode)joinNode).setInterval(tc.getIntervalTime() * 1000);
                // lookup the function
                Function f = ruleCompiler.getEngine().findFunction(tc.getFunction());
                if (f != null) {
                	((TemporalIntervalNode)joinNode).setFunction(f);
                	BigDecimal count = tc.getParameters()[0].getBigDecimalValue();
                	((TemporalIntervalNode)joinNode).setCount(count);
                }
            } else if (binds.length > 0) {
                joinNode = new TemporalEqNode(ruleCompiler.getEngine().nextNodeId());
            }
        }
        
        joinNode.setBindings(binds);
        joinNode.setRightElapsedTime(tc.getRelativeTime() * 1000);
        if (previousCond != null && previousCond instanceof TemporalCondition) {
            joinNode.setLeftElapsedTime( ((TemporalCondition)previousCond).getRelativeTime() * 1000);
        }
        return joinNode;
    }
    
    public void compileSingleCE(Rule rule) throws AssertException{
        Condition[] conds=rule.getConditions();
        ObjectCondition oc = (ObjectCondition)conds[0];
        if (oc.getNegated()) {
            // the ObjectCondition is negated, so we need to
            // handle it appropriate. This means we need to
            // add a LIANode to _IntialFact and attach a NOTNode
            // to the LIANode.
            ObjectTypeNode otn = (ObjectTypeNode)this.ruleCompiler.getInputnodes().get(ruleCompiler.getEngine().getInitFact());
            LIANode lianode = ruleCompiler.findLIANode(otn);
            NotJoin njoin = new NotJoin(ruleCompiler.getEngine().nextNodeId());
            njoin.setBindings(new Binding[0]);
            lianode.addSuccessorNode(njoin,ruleCompiler.getEngine(),ruleCompiler.getMemory());
            // add the join to the rule object
            rule.addJoinNode(njoin);
            oc.getLastNode().addSuccessorNode(njoin,ruleCompiler.getEngine(), ruleCompiler.getMemory());
        } else if (oc.getNodes().size() == 0){
            // this means the rule has a binding, but no conditions
            ObjectTypeNode otn = ruleCompiler.findObjectTypeNode(oc.getTemplateName());
            LIANode lianode = new LIANode(ruleCompiler.getEngine().nextNodeId());
            otn.addSuccessorNode(lianode, ruleCompiler.getEngine(), ruleCompiler.getMemory());
            rule.getConditions()[0].addNode(lianode);
        }
        
    }
}
