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
import java.util.Map;

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.BetaMemory;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defquery;

/**
 * QueryExistFrst is a special implementation for situations
 * when the first Conditional Element is an Exists. The main
 * difference is the left input is a dummy and doesn't do
 * anything. This gets around the need for an InitialFact
 * when the first conditional element is Exists. 
 * 
 * @author Peter Lin
 */
public class QueryExistFrst extends QueryBaseJoin {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public QueryExistFrst(int id) {
		super(id);
	}

	/**
	 * clear will clear the lists
	 */
	public void clear(WorkingMemory mem) {
		Map rightmem = (Map) mem.getQueryRightMemory(this);
		Map leftmem = (Map) mem.getQueryBetaMemory(this);
		Iterator itr = leftmem.keySet().iterator();
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
	public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
			throws AssertException {
		// we only proceed if the fact hasn't already entered
		// the join node
		Index inx = new Index(new Fact[] { rfact });
		Map rightmem = (Map) mem.getQueryRightMemory(this);
		if (!rightmem.containsKey(inx)) {
			int count = rightmem.size();
			rightmem.put(inx, rfact);
			// now that we've added the facts to the list, we
			// proceed with evaluating the fact
			if (count == 0 && rightmem.size() == 1) {
				this.propogateAssert(inx, engine, mem);
			}
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
	
	public QueryExistFrst clone(Rete engine, Defquery query) {
		QueryExistFrst clone = new QueryExistFrst(engine.nextNodeId());
		
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
