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

import java.util.Iterator;

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.Slot2;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defquery;

/**
 * 
 * @author Peter Lin
 */
public class QueryAndAlphaNode extends QueryBaseAlphaCondition {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

	/**
	 * The useCount is used to keep track of how many times
	 * an Alpha node is shared. This is needed so that we
	 * can dynamically remove a rule at run time and remove
	 * the node from the network. If we didn't keep count,
	 * it would be harder to figure out if we can remove the node.
	 */
	protected int useCount = 0;

	/**
	 * 
	 */
	public QueryAndAlphaNode(int id) {
		super(id);
	}

	/**
	 * the first time the RETE compiler makes the node shared,
	 * it needs to increment the useCount.
	 * @param share
	 */
	public boolean isShared() {
		if (this.useCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Set the slot id. The slot id is the deftemplate slot id
	 * @param id
	 */
	public void setSlot(Slot sl) {
		this.slot = (Slot2) sl;
	}

	/**
	 * return the times the node is shared
	 * @return
	 */
	public int getUseCount() {
		return this.useCount;
	}

	/**
	 * every time the node is shared, the method
	 * needs to be called so we keep an accurate count.
	 */
	public void incrementUseCount() {
		this.useCount++;
	}

	/**
	 * every time a rule is removed from the network
	 * we need to decrement the count. Once the count
	 * reaches zero, we can remove the node by calling
	 * it's finalize.
	 */
	public void decrementUseCount() {
		this.useCount--;
	}

	/**
	 * the implementation will first check to see if the fact already matched.
	 * If it did, the fact stops and doesn't go any further. If it doesn't,
	 * it will attempt to evaluate it and add the fact if it matches.
	 * @param factInstance
	 * @param engine
	 */
	public void assertFact(Fact fact, Rete engine, WorkingMemory mem)
			throws AssertException {
		if (evaluate(fact)) {
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
		boolean equal = true;
		Iterator<?> itr = ((Slot2)this.slot).getEqualList().iterator();
		Object fval = factInstance.getSlotValue(this.slot.getId());
		while (itr.hasNext()) {
			Object mv = itr.next();
			if (!mv.equals(fval)) {
				equal = false;
				break;
			}
		}
		if (equal) {
			itr = ((Slot2)this.slot).getNotEqualList().iterator();
			while (itr.hasNext()) {
				Object mv = itr.next();
				if (mv.equals(fval)) {
					equal = false;
					break;
				}
			}
		}
		return equal;
	}

	/**
	 * method is not implemented, since it doesn't apply
	 */
	public void setOperator(int opr) {
	}

	/**
	 * method returns toString() for the hash
	 */
	public String hashString() {
		return toString();
	}

	/**
	 * Method returns the string format of the node's condition. later on
	 * this should be cleaned up.
	 */
	public String toString() {
		return "slot(" + this.slot.getName() + ") " + ((Slot2)this.slot).toString("&")
				+ " - useCount=" + this.useCount;
	}

	/**
	 * Method returns the pretty printer formatted string of the node's
	 * condition. For now, the method just replaces the operator. It might
	 * be nice to replace the slot id with the slot name.
	 * @return
	 */
	public String toPPString() {
		return "and node-" + this.nodeID + "> slot(" + this.slot.getName()
				+ ") " + ((Slot2)this.slot).toString("&") + " - useCount="
				+ this.useCount;
	}

	public QueryBaseAlpha clone(Rete engine, Defquery query) {
		QueryAndAlphaNode clone = new QueryAndAlphaNode(engine.nextNodeId());
		clone.operator = this.operator;
		clone.slot = (Slot)this.slot.clone();
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