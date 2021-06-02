/*
 * Copyright 2002-2008 Peter Lin
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
package org.jamocha.rete.query;

import java.util.Map;

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.BetaMemory;
import org.jamocha.rete.BetaMemoryImpl;
import org.jamocha.rete.Binding;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Index;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.functions.ShellFunction;
import org.jamocha.rule.Defquery;

/**
 * @author Peter Lin
 *
 * TestNode extends BaseJoin. TestNode is used to evaluate functions.
 * It may use values or bindings as parameters for the functions. The
 * left input is where the facts would enter. The right input is a
 * dummy input, since no facts actually enter.
 */
public class QueryTestNode extends QueryBaseJoin {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
	 * TestNode can only have 1 top level function
	 */
	protected Function func = null;

	/**
	 * the parameters to pass to the function
	 */
	protected Parameter[] params = null;

	/**
	 * by default the string is null, until the first time
	 * toPPString is called.
	 */
	private String ppstring = null;
	
	/**
	 * @param id
	 */
	public QueryTestNode(int id, Function func, Parameter[] parameters) {
		super(id);
		this.func = func;
		this.params = parameters;
	}

	public void lookUpFunction(Rete engine) {
		if (func instanceof ShellFunction) {
			ShellFunction sf = (ShellFunction) func;
			sf.lookUpFunction(engine);
			if (sf.getFunction() != null) {
				this.func = sf.getFunction();
			}
		}
	}

	/**
	 * Assert will first pass the facts to the parameters. Once the
	 * parameters are set, it should call execute to get the result.
	 */
	@SuppressWarnings({ "unchecked" })
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
			throws AssertException {
		Map<Index, BetaMemory> leftmem = (Map<Index, BetaMemory>) mem.getQueryBetaMemory(this);
		if (!leftmem.containsKey(linx)) {
			this.setParameters(linx.getFacts());
			ReturnVector rv = this.func.executeFunction(engine, this.params);
			if (rv.firstReturnValue().getBooleanValue()) {
				BetaMemory bmem = new BetaMemoryImpl(linx, engine);
				leftmem.put(bmem.getIndex(), bmem);
				propogateAssert(linx, engine, mem);
			}
		}
	}

	/**
	 * Since the assertRight is a dummy, it doesn't do anything.
	 */
	public void assertRight(Fact rfact, Rete engine, WorkingMemory mem) {
	}

	/**
	 * for TestNode, setbindings does not apply
	 */
	public void setBindings(Binding[] binds) {
	}

	/**
	 * clear the memory
	 */
	public void clear(WorkingMemory mem) {
		((Map<?, ?>) mem.getBetaLeftMemory(this)).clear();
	}

	protected void setParameters(Fact[] facts) {
		for (int idx = 0; idx < this.params.length; idx++) {
			if (params[idx] instanceof BoundParam) {
				((BoundParam) params[idx]).setFact(facts);
			} else if (params[idx] instanceof FunctionParam) {
				((FunctionParam) params[idx]).setFacts(facts);
			} else if (params[idx] instanceof FunctionParam2) {
				((FunctionParam2) params[idx]).setFacts(facts);
			}
		}
	}

	/**
	 * Still need to implement the method to return string
	 * format of the node
	 */
	public String toString() {
		return "(test (" + this.func.getName() + ") )";
	}

	/**
	 * 
	 */
	public String toPPString() {
		if (ppstring == null) {
			StringBuffer buf = new StringBuffer();
			buf.append("TestNode-" + this.nodeID + "> (test (" + this.func.getName());
			for (int idx = 0; idx < this.params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					buf.append(" ?" + bp.getVariableName());
				} else if (params[idx] instanceof ValueParam) {
					ValueParam vp = (ValueParam) params[idx];
					buf.append(" " + vp.getStringValue());
				}
			}
			buf.append(") )");
			ppstring = buf.toString();
		}
		return ppstring;
	}

	public QueryTestNode clone(Rete engine, Defquery query) {
		Parameter[] cloneParams = new Parameter[this.params.length];
		for (int i=0; i < this.params.length; i++) {
			if (params[i] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[i];
				cloneParams[i] = bp.clone();
			} else if (params[i] instanceof ValueParam) {
				ValueParam vp = (ValueParam)params[i];
				cloneParams[i] = vp.cloneParameter();
			} else if (params[i] instanceof FunctionParam) {
				FunctionParam fp = (FunctionParam)params[i];
				cloneParams[i] = fp.clone();
			} else if (params[i] instanceof FunctionParam2) {
				FunctionParam2 fp = (FunctionParam2)params[i];
				cloneParams[i] = fp.clone();
			}
		}
		QueryTestNode clone = new QueryTestNode(engine.nextNodeId(), this.func, cloneParams);
		Binding[] cloneBinding = new Binding[this.binds.length];
		for (int i=0; i < this.binds.length; i++) {
			cloneBinding[i] = (Binding)this.binds[i].clone();
		}
		clone.binds = cloneBinding;
		clone.successorNodes = new BaseNode[this.successorNodes.length];
		for (int i=0; i < this.successorNodes.length; i++) {
    		if (this.successorNodes[i] instanceof QueryBaseAlpha) {
    			clone.successorNodes[i] = ((QueryBaseAlpha)this.successorNodes[i]).clone(engine, query);
    		} else if (this.successorNodes[i] instanceof QueryBaseJoin) {
    			clone.successorNodes[i] = ((QueryBaseJoin)this.successorNodes[i]).clone(engine, query);
    		} else if (this.successorNodes[i] instanceof QueryResultNode) {
    			clone.successorNodes[i] = ((QueryResultNode)this.successorNodes[i]).clone(engine, query);
    		}
		}
		return clone;
	}
}
