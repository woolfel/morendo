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
package org.jamocha.rete.query;

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.CompositeIndex;
import org.jamocha.rete.Constants;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Evaluate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defquery;

/**
 * @author Peter Lin
 *
 * QueryAlphaNode is an alpha node for defqueries. Like no memory alpha nodes,
 * it does not have any memory. This is because queries are fundamentally
 * different and don't need alpha memories.<br/>
 * <br/>
 * 
 */
public class QueryParameterNode extends QueryBaseAlphaCondition {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
	 * The use of Slot(s) is similar to CLIPS design
	 */
	protected Slot slot = null;

	protected CompositeIndex compIndex = null;
	
	protected String parameterName = null;

	/**
	 * 
	 */
	public QueryParameterNode(int id) {
		super(id);
	}

	/**
	 * Set the operator using the int value
	 * @param opr
	 */
	public void setOperator(int opr) {
		this.operator = opr;
	}

	/**
	 * Set the slot id. The slot id is the deftemplate slot id
	 * @param id
	 */
	public void setSlot(Slot sl) {
		this.slot = sl;
	}

	public void setQueryParameterValue(Object value) {
		this.slot.setValue(value);
	}
	
	public void setParameterName(String name) {
		parameterName = name;
	}
	
	public String getParameterName() {
		return this.parameterName;
	}
	
	/**
	 * return the times the node is shared
	 * @return
	 */
	public int getUseCount() {
		return this.useCount;
	}

	/**
	 * 
	 * @param factInstance
	 * @param engine
	 */
	public void assertFact(Fact fact, Rete engine, WorkingMemory mem)
			throws AssertException {
		if (evaluate(fact)) {
			if (watch) {
				engine.writeMessage("QueryParameterNode (" + this.nodeID + ") :: " + 
	    				fact.toFactString() + Constants.LINEBREAK);
			}
			propogateAssert(fact, engine, mem);
		}
	}

	/**
	 * evaluate the node's value against the slot's value. The method
	 * uses Evaluate class to perform the evaluation
	 * @param factInstance
	 * @param engine
	 * @return
	 */
	public boolean evaluate(Fact factInstance) {
		return Evaluate.evaluate(this.operator, factInstance
				.getSlotValue(this.slot.getId()), this.slot.getValue());
	}

	/**
	 * Method returns the string format of the node's condition. later on
	 * this should be cleaned up.
	 */
	public String toString() {
		return "slot(" + this.slot.getId() + ") " + 
			ConversionUtils.getPPOperator(this.operator) + " "
			+ this.slot.getValue().toString() + " - useCount="
			+ this.useCount;
	}

	public CompositeIndex getHashIndex() {
		if (compIndex == null) {
			compIndex = new CompositeIndex(slot.getName(), this.operator, 
					slot.getValue());
		}
		return compIndex;
	}

	/**
	 * Method returns the pretty printer formatted string of the node's
	 * condition. For now, the method just replaces the operator. It might
	 * be nice to replace the slot id with the slot name.
	 * @return
	 */
	public String toPPString() {
		return "<node-" + this.nodeID + "> slot(" + this.slot.getName() + ") "
				+ ConversionUtils.getPPOperator(this.operator) + " "
				+ ConversionUtils.formatSlot(this.slot.getValue())
				+ " - useCount=" + this.useCount;
	}
	
	public QueryParameterNode clone(Rete engine, Defquery query) {
		QueryParameterNode clone = new QueryParameterNode(engine.nextNodeId());
		clone.operator = this.operator;
		clone.parameterName = this.parameterName;
		clone.slot = (Slot)this.slot.clone();
		clone.useCount = this.useCount;
		query.addQueryParameterNode(clone);
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