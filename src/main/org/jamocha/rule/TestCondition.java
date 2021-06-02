/*
 * Copyright 2002-2009 Jamocha
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

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.GraphQueryCompiler;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.QueryCompiler;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.TestNode;
import org.jamocha.rete.compiler.CompilerProvider;
import org.jamocha.rete.compiler.ConditionCompiler;
import org.jamocha.rete.functions.ShellFunction;



/**
 * @author Peter Lin
 *
 * A TestCondition is a pattern that uses a function. For example,
 * in CLIPS, (test (> ?var1 ?var2) )
 */
public class TestCondition implements Condition {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Function func = null;
    protected TestNode node = null;
	protected ArrayList<Object> binds = new ArrayList<Object>();
    protected boolean negated = false;

    
	public TestCondition() {
		super();
	}

    public TestCondition(Function function) {
        this.func = function;
    }
    
    public Function getFunction() {
        return this.func;
    }
    
    public void setFunction(Function function) {
        this.func = function;
    }
    
    public boolean executeFunction(Rete engine, Parameter[] params) {
        ReturnVector rv = func.executeFunction(engine,params);
        // we return the first ReturnValue
        return rv.firstReturnValue().getBooleanValue();
    }
    
    public boolean compare(Condition cond) {
        return false;
    }
    
    public void setTestNode(TestNode node) {
        this.node = node;
    }
    
    public TestNode getTestNode() {
        return this.node;
    }
    
    /**
     * the current implementation creates a new ArrayList, adds the
     * TestNode to it and returns the list.
     */
	public List<TestNode> getNodes() {
        List<TestNode> n = new ArrayList<TestNode>();
        n.add(node);
        return n;
    }    
    
    /**
     * The current implementation checks to make sure the node is a
     * TestNode. If it is, it will set the node. If not, it will ignore
     * it.
     */
    public void addNode(BaseNode node) {
        if (node instanceof TestNode) {
            this.node = (TestNode)node;
        }
    }
    
    public void addNewAlphaNodes(BaseNode node) {
        addNode(node);
    }
    
    public BaseNode getLastNode() {
        return this.node;
    }
    
//    /**
//     * the implementation will look at the parameters for
//     * the function and see if it takes BoundParam
//     */
//    public boolean hasVariables() {
//        if (this.func.getParameter() != null) {
//            Class[] pms = func.getParameter();
//            for (int idx=0; idx < pms.length; idx++) {
//                if (pms[idx] == BoundParam.class) {
//                    binds.add(pms[idx]);
//                }
//            }
//            if (binds.size() > 0) {
//                return true;
//            } else {
//                return true;
//            }
//        } else {
//            return false;
//        }
//    }
    
    /**
     * return an List of the bindings. in the case of TestCondition, the
     * bindings are BoundParam
     */
	public List<Object> getBindConstraints() {
		return binds;
	}
    
    public boolean isNegated() {
        return this.negated;
    }
    
    public void setNegated(boolean negate) {
        this.negated = negate;
    }
    
    public void clear() {
    	node = null;
    }
    
    public String toPPString() {
    	StringBuffer buf = new StringBuffer();
    	String pad = "  ";
    	buf.append(pad + "(test (" + this.func.getName());
    	if (this.func instanceof ShellFunction) {
        	Parameter[] p = ((ShellFunction)this.func).getParameters();
        	for (int idx=0; idx < p.length; idx++) {
        		if (p[idx] instanceof BoundParam) {
            		buf.append(" ?" + ((BoundParam)p[idx]).getVariableName() );
        		} else if (p[idx] instanceof FunctionParam) {
        			FunctionParam fp = (FunctionParam)p[idx];
        			buf.append(" " + fp.toString() + " ");
        		} else if (p[idx] instanceof FunctionParam2) {
        			FunctionParam2 fp2 = (FunctionParam2)p[idx];
        			buf.append(" " + fp2.toPPString());
        		} else {
            		buf.append(" " + ConversionUtils.formatSlot(p[idx].getValue()));
        		}
        	}
    	}
    	buf.append(") )" + Constants.LINEBREAK);
    	return buf.toString();
    }

	@SuppressWarnings("static-access")
	public ConditionCompiler getCompiler(RuleCompiler ruleCompiler) {
		return CompilerProvider.getInstance(ruleCompiler).testConditionCompiler;
	}

	@SuppressWarnings("static-access")
	public ConditionCompiler getCompiler(QueryCompiler ruleCompiler) {
		return CompilerProvider.getInstance(ruleCompiler).testConditionCompiler;
	}
	
	@SuppressWarnings("static-access")
	public ConditionCompiler getCompiler(GraphQueryCompiler ruleCompiler) {
		return CompilerProvider.getInstance(ruleCompiler).testConditionCompiler;
	}
}
