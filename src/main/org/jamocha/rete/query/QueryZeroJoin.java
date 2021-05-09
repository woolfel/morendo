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
import java.util.Iterator;

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.BetaMemory;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defquery;

/**
 * @author Peter Lin
 * 
 * ZJBetaNode is different than other BetaNodes in that it
 * has no bindings. We optimize the performance for those
 * cases by skipping evaluation and just propogate
 */
public class QueryZeroJoin extends QueryBaseJoin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QueryZeroJoin(int id) {
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
		Map leftmem = (Map) mem.getQueryBetaMemory(this);
		Map rightmem = (Map) mem.getQueryRightMemory(this);
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
	 * assertLeft takes an array of facts. Since the next join may be
	 * joining against one or more objects, we need to pass all
	 * previously matched facts.
	 * @param factInstance
	 * @param engine
	 */
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
			throws AssertException {
        Map leftmem = (Map) mem.getBetaLeftMemory(this);

        leftmem.put(linx, linx);
        Map rightmem = (Map) mem.getBetaRightMemory(this);
        Iterator itr = rightmem.values().iterator();
        while (itr.hasNext()) {
            Fact rfcts = (Fact) itr.next();
            // now we propogate
            this.propogateAssert(linx.add(rfcts), engine, mem);
        }
	}

	/**
	 * Assert from the right side is always going to be from an Alpha node.
	 * 
	 * @param factInstance
	 * @param engine
	 */
	public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
			throws AssertException {
        Map rightmem = (Map) mem.getBetaRightMemory(this);
        rightmem.put(rfact, rfact);
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        Iterator itr = leftmem.values().iterator();
        while (itr.hasNext()) {
            Index bmem = (Index) itr.next();
            // now we propogate
            this.propogateAssert(bmem.add(rfact), engine, mem);
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
	
	public QueryZeroJoin clone(Rete engine, Defquery query) {
		QueryZeroJoin clone = new QueryZeroJoin(engine.nextNodeId());
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
