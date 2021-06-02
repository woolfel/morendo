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

import java.util.Iterator;
import java.util.Map;

import org.jamocha.rete.BetaMemory;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defquery;

/**
 * @author Peter Lin
 * 
 * QueryOnlyFrst extends QueryBaseNote, since it works the same as
 * Not CE in defqueries. In the case where a defquery has a only
 * CE for the first conditional element, the left input is a dummy
 * and never get a real fact. If there's only 1 match on the right
 * side, the node will propagate a Index with an empty Factp[]
 * array. This is because only doesn't pass any facts down the
 * network.
 */
public class QueryOnlyFrst extends QueryBaseNot {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public QueryOnlyFrst(int id) {
		super(id);
	}

	/**
	 * clear will clear the lists
	 */
	public void clear(WorkingMemory mem) {
		Map<?, ?> rightmem = (Map<?, ?>) mem.getBetaRightMemory(this);
		Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaRightMemory(this);
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
	 * assertLeft is a dummy, since we don't need an initial
	 * fact or LeftInputAdapater.
	 * @param factInstance
	 * @param engine
	 */
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
			throws AssertException {
	}

	/**
	 * Assert from the right side is always going to be from an
	 * Alpha node.
	 * @param factInstance
	 * @param engine
	 */
	@SuppressWarnings({ "unchecked" })
	public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
			throws AssertException {
		// we only proceed if the fact hasn't already entered
		// the join node
		Index inx = new Index(new Fact[] { rfact });
		Map<Index, Fact> rightmem = (Map<Index, Fact>) mem.getBetaRightMemory(this);
		if (!rightmem.containsKey(inx)) {
			rightmem.put(inx, rfact);
		}
	}

	public void executeJoin(Rete engine, WorkingMemory mem) throws AssertException {
		Map<?, ?> rightmem = (Map<?, ?>) mem.getBetaRightMemory(this);
		// now that we've added the facts to the list, we
		// proceed with evaluating the fact
		if (rightmem.size() == 1) {
			Index inx = new Index(new Fact[0]);
			this.propogateAssert(inx, engine, mem);
		}
	}
	
	/**
	 * method returns string format for the node
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Exist - (no bindings)");
		return buf.toString();
	}

	/**
	 * The current implementation is similar to BetaNode
	 */
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("<node-" + this.nodeID + "> Exist - ");
        buf.append(" (no bindings) ");
		return buf.toString();
	}

	@Override
	public QueryBaseJoin clone(Rete engine, Defquery query) {
		// TODO Auto-generated method stub
		return null;
	}
}
