/*
 * Copyright 2002-2008 Peter Lin
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
package org.jamocha.rete;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 *
 * AlphaNodePredConstr is similar to AlphaNode with the difference that
 * this node calls a function. The function must return boolean type.
 * In other words, the function has to evaluate to true or false.
 * for <, >, <=, >= operator, it's handled by a different node
 */
public class AlphaNodePredConstr extends BaseAlpha {

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

	/**
	 * The use of Slot(s) is similar to CLIPS design
	 */
	protected Slot slot = null;

	protected CompositeIndex compIndex = null;

	/**
	 * The default constructor takes a Node id and function
	 * @param id
	 * @param func
	 */
	public AlphaNodePredConstr(int id, Function func, Parameter[] params) {
		super(id);
		this.function = func;
		this.params = params;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.BaseAlpha#assertFact(woolfel.engine.rete.Fact, woolfel.engine.rete.Rete, woolfel.engine.rete.WorkingMemory)
	 */
	public void assertFact(Fact fact, Rete engine, WorkingMemory mem)
			throws AssertException {
		if (evaluate(fact, engine)) {
            AlphaMemory alpha = (AlphaMemory) mem.getAlphaMemory(this);
			alpha.addPartialMatch(fact);
			propogateAssert(fact, engine, mem);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see woolfel.engine.rete.BaseAlpha#retractFact(woolfel.engine.rete.Fact,
	 *      woolfel.engine.rete.Rete, woolfel.engine.rete.WorkingMemory)
	 */
	public void retractFact(Fact fact, Rete engine, WorkingMemory mem)
			throws RetractException {
		AlphaMemory alpha = (AlphaMemory) mem.getAlphaMemory(this);
		if (alpha.removePartialMatch(fact) != null) {
			propogateRetract(fact, engine, mem);
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

	public CompositeIndex getHashIndex() {
		if (compIndex == null) {
			compIndex = new CompositeIndex(slot.getName(), this.operator, 
					slot.value);
		}
		return compIndex;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.BaseNode#hashString()
	 */
	public String hashString() {
		if (this.hashstring == null) {
			this.hashstring = this.slot.getId() + ":" + this.operator + ":"
					+ String.valueOf(this.slot.value);
		}
		return this.hashstring;
	}

	public String toString() {
		return "slot(" + this.slot.getId() + ") " + 
			ConversionUtils.getPPOperator(this.operator) + " "
			+ this.slot.value.toString() + " - useCount="
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
			+ ConversionUtils.formatSlot(this.slot.value)
			+ " - useCount=" + this.useCount;
		}
	}
}
