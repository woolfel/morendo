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
 * 
 */
public class AlphaNodeOr extends BaseAlpha2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The use of Slot(s) is similar to CLIPS design
	 */
	protected Slot2 slot = null;

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
	public AlphaNodeOr(int id) {
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
		this.slot = (Slot2)sl;
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
    throws AssertException
    {
		if (evaluate(fact)) {
            AlphaMemory alpha = (AlphaMemory) mem.getAlphaMemory(this);
			// we set the time of the last match
			alpha.addPartialMatch(fact);
			// if watch is on, we notify the engine. Rather than
			// create an event class here, we let Rete do that.
			propogateAssert(fact, engine, mem);
		}
	}

    /**
	 * Retract a fact from the node
	 * 
	 * @param factInstance
	 * @param engine
	 */
    public void retractFact(Fact fact, Rete engine, WorkingMemory mem) 
    throws RetractException
    {
        AlphaMemory alpha = (AlphaMemory)mem.getAlphaMemory(this);
        if (alpha.removePartialMatch(fact) != null) {
            propogateRetract(fact,engine,mem);
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
		boolean not = this.slot.EvaluateNotList(
                factInstance.getSlotValue(this.slot.getId()));
		boolean eq = this.slot.getEqualList().contains(
                factInstance.getSlotValue(this.slot.getId()));
		if (eq || not){
            return true;
        } else {
            return false;
        }
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
		return "slot(" + this.slot.getName() + ") "	+ 
        this.slot.toString("|") + 
		" - useCount=" + this.useCount;
	}

	/**
	 * Method returns the pretty printer formatted string of the node's
	 * condition. For now, the method just replaces the operator. It might
	 * be nice to replace the slot id with the slot name.
	 * @return
	 */
	public String toPPString() {
		return "or node-" + this.nodeID + "> slot(" + this.slot.getName() + ") "	+ 
        this.slot.toString("|") + 
		" - useCount=" + this.useCount;
	}
}