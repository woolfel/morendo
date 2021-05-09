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
 * OnlyNeqJoin is a special case for exists join when there's only 1
 * match. It uses NotEqualHashIndex for conditional elements that
 * have constraints that use not equal to.
 */
public class OnlyNeqJoin extends BaseJoin {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OnlyNeqJoin(int id) {
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
        BetaMemory bmem = new BetaMemoryImpl(linx, engine);
        leftmem.put(bmem.getIndex(), bmem);
        NotEqHashIndex inx = new NotEqHashIndex(NodeUtils.getLeftBindValues(this.binds,linx.getFacts()));
        HashedNeqAlphaMemory rightmem = (HashedNeqAlphaMemory) mem.getBetaRightMemory(this);
        Object[] objs = rightmem.iterator(inx);
        // if the right side has 1 match, we propogate the original
        // index down the network. We don't add any facts to the index
        if (objs != null && objs.length == 1) {
        	bmem.addMatch((Fact)objs[0]);
            this.propagateAssert(linx.add((Fact)objs[0]), engine, mem);
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
        int after = rightmem.count(inx);
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        if (after == 1) {
            Iterator itr = leftmem.values().iterator();
            while (itr.hasNext()) {
                BetaMemory bmem = (BetaMemory) itr.next();
                if (this.evaluate(bmem.getIndex().getFacts(), rfact)) {
                	bmem.addMatch(rfact);
                    this.propagateAssert(bmem.getIndex().add(rfact), engine, mem);
                }
            }
        } else if (after == 2) {
            Iterator itr = leftmem.values().iterator();
            while (itr.hasNext()) {
                BetaMemory bmem = (BetaMemory) itr.next();
                if (this.evaluate(bmem.getIndex().getFacts(), rfact)) {
                	try {
                		// we need to get the one right fact that matched
                		Iterator matchedItr = bmem.iterateRightFacts();
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
	public void retractLeft(Index linx, Rete engine, WorkingMemory mem)
			throws RetractException {
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        BetaMemory bmem = (BetaMemory)leftmem.remove(linx);
        if (bmem.matchCount() == 1) {
        	Iterator rightItr = bmem.iterateRightFacts();
        	Fact matchedFact = (Fact)rightItr.next();
            propagateRetract(linx.add(matchedFact), engine, mem);
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
        NotEqHashIndex inx = new NotEqHashIndex(NodeUtils.getRightBindValues(this.binds,rfact));
        HashedNeqAlphaMemory rightmem = (HashedNeqAlphaMemory)mem.getBetaRightMemory(this);
        // remove the fact from the right
        int after = rightmem.removePartialMatch(inx,rfact);
        Map leftmem = (Map)mem.getBetaLeftMemory(this);
        // first we check to see if the fact is the single match for any partial matches on the left
        Iterator leftItr = leftmem.values().iterator();
        while (leftItr.hasNext()) {
        	BetaMemory bmem = (BetaMemory)leftItr.next();
        	if (bmem.matched(rfact)) {
        		this.propagateRetract(bmem.getIndex().add(rfact), engine, mem);
        	}
        }
        if (after == 1) {
        	// there's only 1 match, so we have to propagate it down the network
        	Object[] factArray = rightmem.iterator(inx);
        	Fact f = (Fact)factArray[0];
        	Iterator valueItr = leftmem.values().iterator();
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
}
