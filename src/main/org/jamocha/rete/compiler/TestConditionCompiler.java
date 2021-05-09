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
import org.jamocha.rete.Binding;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.DefaultQueryCompiler;
import org.jamocha.rete.DefaultRuleCompiler;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.GraphQueryCompiler;
import org.jamocha.rete.NTestNode;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.QueryCompiler;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.TestNode;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.functions.ShellFunction;
import org.jamocha.rete.query.QueryBaseJoin;
import org.jamocha.rete.query.QueryNTestNode;
import org.jamocha.rete.query.QueryTestNode;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Query;
import org.jamocha.rule.Rule;
import org.jamocha.rule.TestCondition;

/**
 * 
 * @author HouZhanbin
 * @author Peter Lin
 * Oct 12, 2007 2:18:29 PM
 *
 */
public class TestConditionCompiler implements ConditionCompiler {
	
	public DefaultRuleCompiler ruleCompiler;
	public DefaultQueryCompiler queryCompiler;
	protected GraphQueryCompiler graphCompiler;
	
	public TestConditionCompiler(RuleCompiler ruleCompiler){
		this.ruleCompiler = (DefaultRuleCompiler)ruleCompiler;
	}

	public TestConditionCompiler(QueryCompiler queryCompiler){
		this.queryCompiler = (DefaultQueryCompiler)queryCompiler;
	}
	
	public TestConditionCompiler(GraphQueryCompiler queryCompiler){
		this.graphCompiler = queryCompiler;
	}
	
	public void compile(Condition condition, int position, Rule util, boolean alphaMemory) {
	}
	
	public void compile(Condition condition, int position, Query query) {
	}
	
	public void compileFirstJoin(Condition condition, Rule rule) throws AssertException {
	}
	
	public void compileFirstJoin(Condition condition, Query query) throws AssertException {
	}
	
	/**
	 * the method is responsible for compiling a TestCE pattern to a testjoin node.
     * It uses the globally declared prevCE and prevJoinNode
	 */
	public BaseJoin compileJoin(Condition condition, int position, Rule rule, Condition previousCond) {
		TestCondition tc = (TestCondition)condition;
        ShellFunction fn = (ShellFunction)tc.getFunction();
        fn.lookUpFunction(ruleCompiler.getEngine());
        Parameter[] oldpm = fn.getParameters();
        Parameter[] pms = new Parameter[oldpm.length];
        for (int ipm=0; ipm < pms.length; ipm++) {
            if (oldpm[ipm] instanceof ValueParam) {
                pms[ipm] = ((ValueParam)oldpm[ipm]).cloneParameter();
            } else if (oldpm[ipm] instanceof BoundParam) {
                BoundParam bpm = (BoundParam)oldpm[ipm];
                // now we need to resolve and setup the BoundParam
                Binding b = rule.getBinding(bpm.getVariableName());
                BoundParam newpm = new BoundParam(b.getLeftRow(),
                        b.getLeftIndex(),9,bpm.isObjectBinding());
                newpm.setVariableName(bpm.getVariableName());
                pms[ipm] = newpm;
            } else if (oldpm[ipm] instanceof FunctionParam2) {
            	FunctionParam2 fpm = (FunctionParam2)oldpm[ipm];
            	// resolve any bound parameter in the function parameter
            	fpm.configure(null, rule);
            	pms[ipm] = fpm;
            }
        }
        BaseJoin joinNode=null;
        if (tc.isNegated()) {
            joinNode = new NTestNode(ruleCompiler.getEngine().nextNodeId(),fn.getFunction(),pms);
        } else {
            joinNode = new TestNode(ruleCompiler.getEngine().nextNodeId(),fn.getFunction(),pms);
        }
        ((TestNode)joinNode).lookUpFunction(ruleCompiler.getEngine());
		return joinNode;
	}

	public QueryBaseJoin compileJoin(Condition condition, int position, Query query, Condition previousCond) {
		TestCondition tc = (TestCondition)condition;
        ShellFunction fn = (ShellFunction)tc.getFunction();
        fn.lookUpFunction(queryCompiler.getEngine());
        Parameter[] oldpm = fn.getParameters();
        Parameter[] pms = new Parameter[oldpm.length];
        for (int ipm=0; ipm < pms.length; ipm++) {
            if (oldpm[ipm] instanceof ValueParam) {
                pms[ipm] = ((ValueParam)oldpm[ipm]).cloneParameter();
            } else if (oldpm[ipm] instanceof BoundParam) {
                BoundParam bpm = (BoundParam)oldpm[ipm];
                // now we need to resolve and setup the BoundParam
                Binding b = query.getBinding(bpm.getVariableName());
                BoundParam newpm = new BoundParam(b.getLeftRow(),
                        b.getLeftIndex(),9,bpm.isObjectBinding());
                newpm.setVariableName(bpm.getVariableName());
                pms[ipm] = newpm;
            } else if (oldpm[ipm] instanceof FunctionParam2) {
            	FunctionParam2 fpm = (FunctionParam2)oldpm[ipm];
            	// resolve any bound parameter in the function parameter
            	fpm.configure(null, query);
            	pms[ipm] = fpm;
            }
        }
        QueryBaseJoin joinNode=null;
        if (tc.isNegated()) {
            joinNode = new QueryNTestNode(queryCompiler.getEngine().nextNodeId(),fn.getFunction(),pms);
            ((QueryNTestNode)joinNode).lookUpFunction(queryCompiler.getEngine());
        } else {
            joinNode = new QueryTestNode(queryCompiler.getEngine().nextNodeId(),fn.getFunction(),pms);
            ((QueryTestNode)joinNode).lookUpFunction(queryCompiler.getEngine());
        }
		return joinNode;
	}

	public RuleCompiler getRuleCompiler() {
		return ruleCompiler;
	}

	public QueryCompiler getQueryCompiler() {
		return this.queryCompiler;
	}

	public void connectJoinNode(Condition previousCondition,Condition condition, BaseJoin previousJoinNode, BaseJoin joinNode) throws AssertException {
        
        if (previousJoinNode != null) {
        	ruleCompiler.attachJoinNode(previousJoinNode,(BaseJoin)joinNode);
        } else {
        	ruleCompiler.attachJoinNode(previousCondition.getLastNode(),(BaseJoin)joinNode);
        }
		
	}

	public void connectJoinNode(Condition previousCondition,Condition condition, QueryBaseJoin previousJoinNode, QueryBaseJoin joinNode) throws AssertException {
        
        if (previousJoinNode != null) {
        	queryCompiler.attachJoinNode(previousJoinNode,(QueryBaseJoin)joinNode);
        } else {
        	queryCompiler.attachJoinNode(previousCondition.getLastNode(),(QueryBaseJoin)joinNode);
        }
		
	}

	@Override
	public void connectJoinNode(Condition previousCondition, Condition condition, QueryBaseJoin previousJoinNode,
			QueryBaseJoin joinNode, GraphQueryCompiler compiler) throws AssertException {
	}
	
	public void compileSingleCE(Rule rule) throws AssertException {
	}
	
	public void compileSingleCE(Query query) throws AssertException {
	}
}
