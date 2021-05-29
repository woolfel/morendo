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

/**
 * @author Peter Lin
 * 
 * OnlyPredJoin is special case of exists when there's only
 * 1 match and the constraint uses a function to compare
 * two values. 
 */
public class OnlyFuncJoin extends BaseJoin {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OnlyFuncJoin(int id) {
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
        Map<Index, BetaOnlyImpl> leftmem = (Map<Index, BetaOnlyImpl>) mem.getBetaLeftMemory(this);
        BetaOnlyImpl bmem = new BetaOnlyImpl(linx, engine);
        leftmem.put(linx, bmem);
        Map<?, ?> rightmem = (Map<?, ?>)mem.getBetaRightMemory(this);
        Iterator<?> itr = rightmem.keySet().iterator();
        if (itr != null) {
            while (itr.hasNext()) {
                Fact vl = (Fact) itr.next();
                // we have to evaluate the function
                if (vl != null && evaluate(linx.getFacts(),vl,engine)) {
                    bmem.addMatch(vl);
                }
            }
        }
        if (bmem.matchCount() == 1) {
        	Fact f = (Fact)bmem.iterateRightFacts().next();
        	bmem.setOnlyMatch(f);
            this.propagateAssert(linx.add(f), engine, mem);
        }
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
        Map<Fact, Fact> rightmem = (Map<Fact, Fact>)mem.getBetaRightMemory(this);
        rightmem.put(rfact, rfact);
        Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
        Iterator<?> itr = leftmem.values().iterator();
        while (itr.hasNext()) {
        	BetaOnlyImpl bmem = (BetaOnlyImpl) itr.next();
            if (this.evaluate(bmem.getLeftFacts(), rfact, engine)) {
                // now we propogate
                bmem.addMatch(rfact);
                if (bmem.matchCount() == 1) {
                	bmem.setOnlyMatch(rfact);
                    this.propagateAssert(bmem.getIndex().add(rfact), engine, mem);
                } else if (bmem.matchCount() == 2) {
                	try {
						this.propagateRetract(bmem.getIndex().add(bmem.getOnlyMatch()), engine, mem);
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
		Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
		BetaOnlyImpl bmem = (BetaOnlyImpl)leftmem.remove(linx);
		if (bmem != null) {
			Fact f = bmem.getOnlyMatch();
			if (f != null) {
				bmem.clear();
		        propagateRetract(linx.add(f), engine, mem);
			}
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
        Map<?, ?> rightmem = (Map<?, ?>)mem.getBetaRightMemory(this);
        rightmem.remove(rfact);
        Map<?, ?> leftmem = (Map<?, ?>)mem.getBetaLeftMemory(this);
        Iterator<?> itr = leftmem.values().iterator();
        while (itr.hasNext()){
        	BetaOnlyImpl bmem = (BetaOnlyImpl)itr.next();
            if (this.evaluate(bmem.getLeftFacts(), rfact, engine)){
            	if (rfact == bmem.getOnlyMatch()) {
            		propagateRetract(bmem.getIndex().add(rfact),engine,mem);
            	}
                bmem.removeMatch(rfact);
                if (bmem.matchCount() == 1) {
                	try {
                		Fact f = (Fact)bmem.iterateRightFacts().next();
                		bmem.setOnlyMatch(f);
						propagateAssert(bmem.getIndex().add(f),engine,mem);
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
	public boolean evaluate(Fact[] leftlist, Fact right, Rete engine) {
        boolean eval = true;
        // we iterate over the binds and evaluate the facts
        for (int idx = 0; idx < this.binds.length; idx++) {
            Binding bnd = (Binding) binds[idx];
            if (bnd instanceof Binding2) {
                eval = ((Binding2)bnd).evaluate(leftlist, right, engine);
            } else {
                eval = bnd.evaluate(leftlist, right);
            }
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
		buf.append("ExistPredJoin - ");
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
