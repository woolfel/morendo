/*
 * Copyright 2002-2010 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.query;

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.CompositeIndex;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnValue;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.Slot;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defquery;

/**
 * QueryFuncAlphaNode is similar to AlphaNode with the difference that
 * this node calls a function. The function must return boolean type.
 * In other words, the function has to evaluate to true or false.
 * for <, >, <=, >= operator, it's handled by a different node
 * 
 * @author Peter Lin
 */
public class QueryFuncAlphaNode extends QueryBaseAlphaCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The function to call
	 */
	protected Function function = null;

	protected String hashstring = null;

	protected Parameter[] params = null;
	
	protected String parameterName = null;

	protected CompositeIndex compIndex = null;

	/**
	 * The default constructor takes a Node id and function
	 * @param id
	 * @param func
	 */
	public QueryFuncAlphaNode(int id, Function func, Parameter[] params, Slot slot) {
		super(id);
		this.function = func;
		this.params = params;
		this.slot = slot;
	}

	public void setSlot(Slot sl) {
		this.slot = sl;
	}
	
	public void setOperator(int opr) {
		this.operator = opr;
	}
	
	public void setParameterName(String name) {
		parameterName = name;
	}
	
	public String getParameterName() {
		return this.parameterName;
	}
	
	public void setQueryParameterValue(Object value) {
		this.slot.setValue(value);
	}
	
	public void assertFact(Fact fact, Rete engine, WorkingMemory mem)
			throws AssertException {
		if (evaluate(fact, engine)) {
			propogateAssert(fact, engine, mem);
		}
	}

	/**
	 * The method uses the function to evaluate the fact
	 * @param factInstance
	 * @return
	 */
	public boolean evaluate(Fact factInstance, Rete engine) {
		for (int idx=0; idx < params.length; idx++) {
			if (params[idx] instanceof BoundParam) {
				((BoundParam)params[idx]).setFact(new Fact[] {factInstance});
			} else if (params[idx] instanceof FunctionParam2) {
				((FunctionParam2)params[idx]).setFacts(new Fact[]{factInstance});
			}
		}
		ReturnVector rv = this.function.executeFunction(engine, this.params);
		ReturnValue rval = rv.firstReturnValue();
		return rval.getBooleanValue();
	}

	public String hashString() {
		if (this.hashstring == null) {
			this.hashstring = this.slot.getId() + ":" + this.operator + ":"
					+ String.valueOf(this.slot.getValue());
		}
		return this.hashstring;
	}

	public String toString() {
		return "slot(" + this.slot.getId() + ") " + 
			ConversionUtils.getPPOperator(this.operator) + " "
			+ this.slot.getValue().toString() + " - useCount="
			+ this.useCount;
	}

	public String toPPString() {
		if (function != null) {
			StringBuffer buf = new StringBuffer();
			buf.append("node-" + this.nodeID + "> slot(" + this.slot.getName() + ") ");
			buf.append(function.toPPString(params, 1));
			buf.append(" - useCount=" + this.useCount);
			return buf.toString();
		} else {
			return "node-" + this.nodeID + "> slot(" + this.slot.getName() + ") "
			+ ConversionUtils.getPPOperator(this.operator) + " "
			+ ConversionUtils.formatSlot(this.slot.getValue())
			+ " - useCount=" + this.useCount;
		}
	}

	public QueryBaseAlpha clone(Rete engine, Defquery query) {
		// first clone the parameters
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
		QueryFuncAlphaNode clone = new QueryFuncAlphaNode(engine.nextNodeId(), this.function, cloneParams, (Slot)this.slot.clone());
		clone.operator = this.operator;
		clone.parameterName = this.parameterName;
		clone.useCount = this.useCount;
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
		query.addQueryFuncNode(clone);
		return clone;
	}
}
