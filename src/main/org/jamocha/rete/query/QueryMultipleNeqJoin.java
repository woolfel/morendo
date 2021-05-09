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
import org.jamocha.rete.Binding;
import org.jamocha.rete.Fact;
import org.jamocha.rete.HashedNeqAlphaMemory;
import org.jamocha.rete.Index;
import org.jamocha.rete.NotEqHashIndex;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.util.NodeUtils;
import org.jamocha.rule.Defquery;

/**
 * @author Peter Lin
 * 
 * MultipleNeqJoin is a special case for exists join when there's
 * multiple matches. It uses NotEqualHashIndex for conditional
 * elements that have constraints that use not equal to.
 */
public class QueryMultipleNeqJoin extends QueryBaseJoin {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public QueryMultipleNeqJoin(int id) {
		super(id);
	}

	/**
	 * clear will clear the lists
	 */
	public void clear(WorkingMemory mem) {
		Map rightmem = (Map) mem.getBetaRightMemory(this);
		Map leftmem = (Map) mem.getBetaRightMemory(this);
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
        NotEqHashIndex inx = new NotEqHashIndex(NodeUtils.getLeftBindValues(this.binds,linx.getFacts()));
        HashedNeqAlphaMemory rightmem = (HashedNeqAlphaMemory) mem.getBetaRightMemory(this);
        Object[] objs = rightmem.iterator(inx);
        // if the right side has 1 match, we propogate the original
        // index down the network. We don't add any facts to the index
        if (objs != null && objs.length > 1) {
            this.propogateAssert(linx, engine, mem);
        }
	}

	/**
	 * Assert from the right side is always going to be from an
	 * Alpha node.
	 * @param factInstance
	 * @param engine
	 */
	public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
			throws AssertException {
        HashedNeqAlphaMemory rightmem = (HashedNeqAlphaMemory) mem.getBetaRightMemory(this);
        NotEqHashIndex inx = new NotEqHashIndex(NodeUtils.getRightBindValues(this.binds,rfact));

        rightmem.addPartialMatch(inx, rfact, engine);
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        Iterator itr = leftmem.values().iterator();
        int after = rightmem.count(inx);
        while (itr.hasNext()) {
            Index linx = (Index) itr.next();
            if (this.evaluate(linx.getFacts(), rfact)) {
                if (after > 1) {
                    this.propogateAssert(linx, engine, mem);
                }
            }
        }
    }

	/**
	 * Method will use the right binding to perform the evaluation
	 * of the join. Since we are building joins similar to how
	 * CLIPS and other rule engines handle it, it means 95% of the
	 * time the right fact list only has 1 fact.
	 * @param leftlist
	 * @param right
	 * @return
	 */
	public boolean evaluate(Fact[] leftlist, Fact right) {
		boolean eval = true;
		// we iterate over the binds and evaluate the facts
		for (int idx = 0; idx < this.binds.length; idx++) {
            Binding bnd = binds[idx];
            eval = bnd.evaluate(leftlist, right);
            if (!eval) {
                break;
            }
		}
		return eval;
	}

	/**
	 * simple implementation for toString. may need to change the format
	 * later so it looks nicer.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("OnlyNeqJoin - ");
		for (int idx = 0; idx < this.binds.length; idx++) {
			if (idx > 0) {
				buf.append(" && ");
			}
			buf.append(this.binds[idx].toBindString());
		}
		return buf.toString();
	}

	/**
	 * The current implementation is similar to BetaNode
	 */
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("<node-" + this.nodeID + "> OnlyNeqJoin - ");
        if (binds != null && binds.length > 0) {
            for (int idx = 0; idx < this.binds.length; idx++) {
                if (idx > 0) {
                    buf.append(" && ");
                }
                buf.append(this.binds[idx].toPPString());
            }
        } else {
            buf.append(" no joins ");
        }
		return buf.toString();
	}

	@Override
	public QueryBaseJoin clone(Rete engine, Defquery query) {
		// TODO Auto-generated method stub
		return null;
	}
}
