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

import java.io.Serializable;
import java.util.List;

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.GraphQueryCompiler;
import org.jamocha.rete.Print;
import org.jamocha.rete.QueryCompiler;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.compiler.ConditionCompiler;


/**
 * @author Peter Lin
 *
 * Conditions are patterns. It may be a simple fact pattern, test function,
 * or an object pattern.
 */
public interface Condition extends Serializable, Print {
    /**
     * Method is used to compare the pattern to another pattern and
     * determine if they are equal.
     * @param cond
     * @return
     */
    boolean compare(Condition cond);
    /**
     * Get the nodes associated with the condition. In the case of
     * TestConditions, it should only be 1 node.
     * @return
     */    
    @SuppressWarnings("rawtypes")
	List getNodes();
    /**
     * When the rule is compiled, we add the node to the condition,
     * so that we can print out the matches for a given rule.
     * @param node
     */
    void addNode(BaseNode node);
    /**
     * if the rule's alpha nodes aren't shared, this method is
     * used to add the alphaNodes to the condition
     * @param node
     */
    void addNewAlphaNodes(BaseNode node);
    /**
     * clear the condition
     */
    void clear();
    /**
     * Get the last node in the Condition
     * @return
     */
    BaseNode getLastNode();
    /**
     * Get the bind Constraint List including BoundConstraint (isObjectBinding==false) 
     * and PredicateConstraint (isPredicateJoin==true)
     * @return
     */
    @SuppressWarnings("rawtypes")
	List getBindConstraints();
    /**
     * obtain the compiler this condition
     * @return
     */
    ConditionCompiler getCompiler(RuleCompiler ruleCompiler);
    /**
     * object the compiler for this condition
     * @param ruleCompiler
     * @return
     */
    ConditionCompiler getCompiler(QueryCompiler ruleCompiler);
    /**
     * 
     * @param ruleCompiler
     * @return
     */
    ConditionCompiler getCompiler(GraphQueryCompiler ruleCompiler);
}
