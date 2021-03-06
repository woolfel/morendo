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
package org.jamocha.rete;

import java.util.Map;
import java.util.Iterator;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * ZJBetaNode is different than other BetaNodes in that it
 * has no bindings. We optimize the performance for those
 * cases by skipping evaluation and just propogate
 */
public class ZJBetaNode extends BaseJoin {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The operator for the join by default is equal. The the join
	 * doesn't comparing values, the operator should be set to -1.
	 */
	
	protected int operator = Constants.EQUAL;

	public ZJBetaNode(int id) {
		super(id);
	}

	/**
	 * Set the bindings for this join
	 * @param binds
	 */
	public void setBindings(Binding[] binds) {
	}

	/**
	 * clear will clear the lists
	 */
	public void clear(WorkingMemory mem) {
		Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
		Map<?, ?> rightmem = (Map<?, ?>) mem.getBetaRightMemory(this);
		Iterator<?> itr = leftmem.keySet().iterator();
		// first we iterate over the list for each fact
		// and clear it.
		while (itr.hasNext()) {
			BetaMemory bmem = (BetaMemory) leftmem.get(itr.next());
			bmem.clear();
		}
		// now that we've cleared the list for each fact, we
		// can clear the Map.
		leftmem.clear();
		rightmem.clear();
	}

	/**
	 * assertLeft takes an array of facts. Since the next join may be
	 * joining against one or more objects, we need to pass all
	 * previously matched facts.
	 * @param factInstance
	 * @param engine
	 */
	@SuppressWarnings({ "unchecked" })
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
			throws AssertException {
        Map<Index, Index> leftmem = (Map<Index, Index>) mem.getBetaLeftMemory(this);

        leftmem.put(linx, linx);
        Map<?, ?> rightmem = (Map<?, ?>) mem.getBetaRightMemory(this);
        Iterator<?> itr = rightmem.values().iterator();
        while (itr.hasNext()) {
            Fact rfcts = (Fact) itr.next();
            // now we propogate
            this.propagateAssert(linx.add(rfcts), engine, mem);
        }
	}

	/**
	 * Assert from the right side is always going to be from an Alpha node.
	 * 
	 * @param factInstance
	 * @param engine
	 */
	@SuppressWarnings({ "unchecked" })
	public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
			throws AssertException {
        Map<Fact, Fact> rightmem = (Map<Fact, Fact>) mem.getBetaRightMemory(this);
        rightmem.put(rfact, rfact);
        Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
        Iterator<?> itr = leftmem.values().iterator();
        while (itr.hasNext()) {
            Index bmem = (Index) itr.next();
            // now we propogate
            this.propagateAssert(bmem.add(rfact), engine, mem);
        }
	}

	/**
	 * Retracting from the left requires that we propogate the
	 * @param factInstance
	 * @param engine
	 */
	public void retractLeft(Index linx, Rete engine, WorkingMemory mem)
			throws RetractException {
        Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
        leftmem.remove(linx);
        Map<?, ?> rightmem = (Map<?, ?>) mem.getBetaRightMemory(this);
        Iterator<?> itr = rightmem.values().iterator();
        while (itr.hasNext()) {
            propagateRetract(linx.add((Fact) itr
                    .next()), engine, mem);
        }
	}

	/**
	 * Retract from the right works in the following order.
	 * 1. remove the fact from the right memory
	 * 2. check which left memory matched
	 * 3. propogate the retract
	 * @param factInstance
	 * @param engine
	 */
	public void retractRight(Fact rfact, Rete engine, WorkingMemory mem)
			throws RetractException {
        Map<?, ?> rightmem = (Map<?, ?>) mem.getBetaRightMemory(this);
        rightmem.remove(rfact);
        Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
        Iterator<?> itr = leftmem.values().iterator();
        while (itr.hasNext()) {
            Index bmem = (Index) itr.next();
            // now we propogate
            propagateRetract(bmem.add(rfact), engine, mem);
        }
	}

	/**
	 * Basic implementation will return string format of the betaNode
	 */
	public String toString() {
		return "ZJBetaNode";
	}

	/**
	 * implementation just returns the node id and the text
	 * zero-bind join.
	 */
	public String toPPString() {
		return "ZJBetaNode-" + this.nodeID + "> ";
	}
}
