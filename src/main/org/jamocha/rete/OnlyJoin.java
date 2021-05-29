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
 * OnlyJoin is a specialized version of exists join that is satified
 * if only 1 match is found. 
 */
public class OnlyJoin extends BaseJoin {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OnlyJoin(int id) {
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
        while (itr.hasNext()){
            BetaMemory bmem = (BetaMemory)leftmem.get(itr.next());
            bmem.clear();
        }
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
        Map<Index, BetaMemory> leftmem = (Map<Index, BetaMemory>) mem.getBetaLeftMemory(this);
        BetaMemory bmem = new BetaMemoryImpl(linx, engine);
        leftmem.put(bmem.getIndex(), bmem);
        EqHashIndex inx = new EqHashIndex(NodeUtils.getLeftValues(this.binds,linx.getFacts()));
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                .getBetaRightMemory(this);
        if (rightmem.count(inx) == 1) {
        	Iterator<?> iterator = rightmem.iterator(inx);
        	Fact f = (Fact)iterator.next();
        	bmem.addMatch(f);
            this.propagateAssert(linx.add(f), engine, mem);
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
        if (after == 1) {
        	// we propagate assert
            Iterator<?> itr = leftmem.values().iterator();
            while (itr.hasNext()) {
            	BetaMemory bmem = (BetaMemory) itr.next();
                if (this.evaluate(bmem.getLeftFacts(), rfact)) {
                	bmem.addMatch(rfact);
                    this.propagateAssert(bmem.getIndex().add(rfact), engine, mem);
                }
            }
        } else if (after == 2) {
        	// we propagate retract
            Iterator<?> itr = leftmem.values().iterator();
            while (itr.hasNext()) {
            	BetaMemory bmem = (BetaMemory) itr.next();
                if (this.evaluate(bmem.getLeftFacts(), rfact)) {
                	try {
                		// we need to get the one right fact that matched
                		Iterator<?> matchedItr = bmem.iterateRightFacts();
                		Fact matchedFact = (Fact)matchedItr.next();
                		bmem.removeMatch(matchedFact);
						this.propagateRetract(bmem.getIndex().add(matchedFact), engine, mem);
					} catch (RetractException e) {
					}
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
        BetaMemory bmem = (BetaMemory)leftmem.remove(inx);
        if (bmem.matchCount() == 1) {
        	Iterator<?> rightItr = bmem.iterateRightFacts();
        	Fact matchedFact = (Fact)rightItr.next();
            propagateRetract(inx.add(matchedFact), engine, mem);
        }
	}

	/**
	 * 
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
        Map<?, ?> leftmem = (Map<?, ?>)mem.getBetaLeftMemory(this);
        // first we check to see if the fact is the single match for any partial matches on the left
        Iterator<?> leftItr = leftmem.values().iterator();
        while (leftItr.hasNext()) {
        	BetaMemory bmem = (BetaMemory)leftItr.next();
        	if (bmem.matched(rfact)) {
        		this.propagateRetract(bmem.getIndex().add(rfact), engine, mem);
        	}
        }
        if (after == 1) {
        	// there's only 1 match, so we have to propagate it down the network
        	Iterator<?> rightItr = rightmem.iterator(inx);
        	Fact f = (Fact)rightItr.next();
        	Iterator<?> valueItr = leftmem.values().iterator();
        	while (valueItr.hasNext()) {
        		BetaMemory bmem = (BetaMemory)valueItr.next();
        		if (this.evaluate(bmem.getLeftFacts(), f)) {
            		try {
            			bmem.addMatch(f);
    					this.propagateAssert(bmem.getIndex().add(f), engine, mem);
    				} catch (AssertException e) {
    				}
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
		buf.append("Only - ");
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
		buf.append("<node-" + this.nodeID + "> Only - ");
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
