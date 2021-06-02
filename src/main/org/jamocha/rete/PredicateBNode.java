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
 * HashedBetaNode indexes the right input to improve cross product performance.
 */
public class PredicateBNode extends BaseJoin {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public PredicateBNode(int id) {
        super(id);
    }

    /**
     * Set the bindings for this join
     * 
     * @param binds
     */
    public void setBindings(Binding[] binds) {
        this.binds = binds;
    }

    /**
     * clear will clear the lists
     */
	public void clear(WorkingMemory mem) {
        Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                .getBetaRightMemory(this);
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
     * assertLeft takes an array of facts. Since the next join may be joining
     * against one or more objects, we need to pass all previously matched
     * facts.
     * 
     * @param factInstance
     * @param engine
     */
    @SuppressWarnings({ "unchecked" })
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
            throws AssertException {
        Map<Index, BetaMemory> leftmem = (Map<Index, BetaMemory>) mem.getBetaLeftMemory(this);
        BetaMemory bmem = new BetaMemoryImpl(linx, engine);
        leftmem.put(linx, bmem);
        Map<?, ?> rightmem = (Map<?, ?>)mem.getBetaRightMemory(this);
        Iterator<?> itr = rightmem.keySet().iterator();
        if (itr != null) {
            while (itr.hasNext()) {
                Fact vl = (Fact) itr.next();
                // we have to evaluate the function
                if (vl != null && evaluate(linx.getFacts(),vl,engine)) {
                    bmem.addMatch(vl);
                    this.propagateAssert(linx.add(vl), engine, mem);
                }
            }
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
        Map<Fact, Fact> rightmem = (Map<Fact, Fact>)mem.getBetaRightMemory(this);
        rightmem.put(rfact, rfact);
        Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
        Iterator<?> itr = leftmem.values().iterator();
        while (itr.hasNext()) {
            BetaMemory bmem = (BetaMemory) itr.next();
            if (this.evaluate(bmem.getLeftFacts(), rfact, engine)) {
                // now we propogate
                bmem.addMatch(rfact);
                this.propagateAssert(bmem.getIndex().add(rfact), engine, mem);
            }
        }
    }

    /**
     * Retracting from the left requires that we propogate the
     * 
     * @param factInstance
     * @param engine
     */
    public void retractLeft(Index linx, Rete engine, WorkingMemory mem)
            throws RetractException {
        Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
        leftmem.remove(linx);
        Map<?, ?> rightmem = (Map<?, ?>)mem.getBetaRightMemory(this);
        Iterator<?> itr = rightmem.keySet().iterator();
        if (itr != null) {
            while (itr.hasNext()) {
                propagateRetract(linx.add((Fact) itr.next()), engine, mem);
            }
        }
    }

    /**
     * Retract from the right works in the following order. 1. remove the fact
     * from the right memory 2. check which left memory matched 3. propogate the
     * retract
     * 
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
            BetaMemory bmem = (BetaMemory)itr.next();
            if (this.evaluate(bmem.getLeftFacts(), rfact, engine)){
                bmem.removeMatch(rfact);
                propagateRetract(bmem.getIndex().add(rfact),engine,mem);
            }
        }
    }

    /**
     * Method will use the right binding to perform the evaluation of the join.
     * Since we are building joins similar to how CLIPS and other rule engines
     * handle it, it means 95% of the time the right fact list only has 1 fact.
     * 
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
     * Basic implementation will return string format of the betaNode
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int idx = 0; idx < this.binds.length; idx++) {
            if (idx > 0) {
                buf.append(" && ");
            }
            buf.append(this.binds[idx].toBindString());
        }
        return buf.toString();
    }

    /**
     * returns the node named + node id and the bindings in a string format
     */
    public String toPPString() {
        StringBuffer buf = new StringBuffer();
        buf.append("PredicateBNode-" + this.nodeID + "> ");
        for (int idx = 0; idx < this.binds.length; idx++) {
            if (idx > 0) {
                buf.append(" && ");
            }
            if (this.binds[idx] != null) {
                buf.append(this.binds[idx].toPPString());
            }
        }
        return buf.toString();
    }
}
