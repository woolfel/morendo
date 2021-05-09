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

import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.GraphQueryCompiler;
import org.jamocha.rete.QueryCompiler;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.query.QueryBaseJoin;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Query;
import org.jamocha.rule.Rule;
/**
 * 
 * @author HouZhanbin
 * Oct 12, 2007 9:32:31 AM
 *
 */
public interface ConditionCompiler {
	
	/**
	 * 
	 * @param condition
	 * @param position 
	 * @param util
	 * @param alphaMemory 
	 */
    void compile(Condition condition, int position, Rule util, boolean alphaMemory);
    /**
     * Compile method for queries. Queries do not have alpha memories, so the method
     * does not take a boolean flag like rules.
     * @param condition
     * @param position
     * @param query
     */
    void compile(Condition condition, int position, Query query);
    
    /**
     * 
     * @param condition
     * @param rule
     */
    void compileFirstJoin(Condition condition,Rule rule)throws AssertException;
    
    void compileFirstJoin(Condition condition,Query query)throws AssertException;
    
    /**
     * 
     * @return
     */
    RuleCompiler getRuleCompiler();
    
    /**
     * 
     * @return
     */
    QueryCompiler getQueryCompiler();
    
    /**
     * 
     * @param condition
     * @param position
     * @param rule
     * @return
     */
    BaseJoin compileJoin(Condition condition,int position,Rule rule,Condition previousCond);
    
    QueryBaseJoin compileJoin(Condition condition,int position,Query query,Condition previousCond);
    
    /**
     * 
     * @param condition
     * @param joinNode
     */
    void connectJoinNode(Condition previousCondition, Condition condition, BaseJoin previousJoinNode, BaseJoin joinNode) throws AssertException;

    void connectJoinNode(Condition previousCondition, Condition condition, QueryBaseJoin previousJoinNode, QueryBaseJoin joinNode) throws AssertException;
    
    void connectJoinNode(Condition previousCondition, Condition condition, QueryBaseJoin previousJoinNode, QueryBaseJoin joinNode, GraphQueryCompiler compiler) throws AssertException;
    /**
     * compile the only CE in the rule which has only one CE.
     * @param rule
     */
    void compileSingleCE(Rule rule)throws AssertException;
    
    void compileSingleCE(Query query)throws AssertException;
}
