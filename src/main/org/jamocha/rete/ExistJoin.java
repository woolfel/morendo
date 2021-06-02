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

import java.util.Iterator;
import java.util.Map;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.util.NodeUtils;

/**
 * @author Peter Lin
 * 
 * ExistJoin is implemented differently than how CLIPS does it. According
 * to CLIPS beginners guide, Exist is convert to (not (not (blah) ) ).
 * Rather than do that, I'm experimenting with a specialized Existjoin
 * node instead. The benefit is reduce memory and fewer nodes in the 
 * network. 
 */
public class ExistJoin extends BaseJoin {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ExistJoin(int id) {
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
        EqHashIndex inx = new EqHashIndex(NodeUtils.getLeftValues(this.binds,linx.getFacts()));
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                .getBetaRightMemory(this);
        if (rightmem.count(inx) > 0) {
            this.propagateAssert(linx, engine, mem);
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
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                .getBetaRightMemory(this);
        EqHashIndex inx = new EqHashIndex(NodeUtils.getRightValues(this.binds,rfact));
        int after = rightmem.addPartialMatch(inx, rfact, engine);
        Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
        Iterator<?> itr = leftmem.values().iterator();
        while (itr.hasNext()) {
            Index linx = (Index) itr.next();
            if (this.evaluate(linx.getFacts(), rfact)) {
                if (after == 1) {
                    this.propagateAssert(linx, engine, mem);
                }
            }
        }
    }

	/**
	 * Retracting from the left is different than retractRight for couple
	 * of reasons.
	 * <ul>
	 * <li> NotJoin will only propogate the facts from the left</li>
	 * <li> NotJoin never needs to merge the left and right</li>
	 * </ul>
	 * @param factInstance
	 * @param engine
	 */
	public void retractLeft(Index inx, Rete engine, WorkingMemory mem)
			throws RetractException {
		Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
        leftmem.remove(inx);
        propagateRetract(inx, engine, mem);
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
        HashedAlphaMemoryImpl rightmem = 
            (HashedAlphaMemoryImpl)mem.getBetaRightMemory(this);
        EqHashIndex inx = new EqHashIndex(NodeUtils.getRightValues(this.binds,rfact));
        // remove the fact from the right
        int after = rightmem.removePartialMatch(inx,rfact);
        if (after == 0){
            // now we see the left memory matched and remove it also
            Map<?, ?> leftmem = (Map<?, ?>)mem.getBetaLeftMemory(this);
            Iterator<?> itr = leftmem.values().iterator();
            while (itr.hasNext()){
                Index linx = (Index)itr.next();
                if (this.evaluate(linx.getFacts(), rfact)){
                    propagateRetract(linx,engine,mem);
                }
            }
            inx = null;
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
		buf.append("Exist - ");
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
		buf.append("<node-" + this.nodeID + "> Exist - ");
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
}
