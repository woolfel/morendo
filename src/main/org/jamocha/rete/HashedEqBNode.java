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
import org.jamocha.rete.util.NodeUtils;

/**
 * @author Peter Lin
 * 
 * HashedBetaNode indexes the right input to improve cross product performance.
 */
public class HashedEqBNode extends BaseJoin {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public HashedEqBNode(int id) {
        super(id);
    }

    /**
     * assertLeft takes an array of facts. Since the next join may be joining
     * against one or more objects, we need to pass all previously matched
     * facts.
     * 
     * @param factInstance
     * @param engine
     */
    public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
            throws AssertException {
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        leftmem.put(linx, linx);
        EqHashIndex inx = new EqHashIndex(NodeUtils.getLeftValues(this.binds,linx.getFacts()));
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                .getBetaRightMemory(this);
        Iterator itr = rightmem.iterator(inx);
        if (itr != null) {
            while (itr.hasNext()) {
                Fact vl = (Fact) itr.next();
                if (vl != null) {
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
    public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
            throws AssertException {
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                .getBetaRightMemory(this);
        EqHashIndex inx = new EqHashIndex(NodeUtils.getRightValues(this.binds,rfact));

        rightmem.addPartialMatch(inx, rfact, engine);
        // now that we've added the facts to the list, we
        // proceed with evaluating the fact
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        // since there may be key collisions, we iterate over the
        // values of the HashMap. If we used keySet to iterate,
        // we could encounter a ClassCastException in the case of
        // key collision.
        Iterator itr = leftmem.values().iterator();
        while (itr.hasNext()) {
            Index linx = (Index) itr.next();
            if (this.evaluate(linx.getFacts(), rfact)) {
                // now we propogate
                this.propagateAssert(linx.add(rfact), engine, mem);
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
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        leftmem.remove(linx);
        EqHashIndex eqinx = new EqHashIndex(NodeUtils.getLeftValues(this.binds,linx.getFacts()));
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                .getBetaRightMemory(this);

        // now we propogate the retract. To do that, we have
        // merge each item in the list with the Fact array
        // and call retract in the successor nodes
        Iterator itr = rightmem.iterator(eqinx);
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
        EqHashIndex inx = new EqHashIndex(NodeUtils.getRightValues(this.binds,rfact));
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem
                .getBetaRightMemory(this);
        // first we remove the fact from the right
        rightmem.removePartialMatch(inx, rfact);
        // now we see the left memory matched and remove it also
        Map leftmem = (Map) mem.getBetaLeftMemory(this);
        Iterator itr = leftmem.values().iterator();
        while (itr.hasNext()) {
            Index linx = (Index) itr.next();
            if (this.evaluate(linx.getFacts(), rfact)) {
                propagateRetract(linx.add(rfact), engine, mem);
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
    public boolean evaluate(Fact[] leftlist, Fact right) {
        boolean eval = true;
        // we iterate over the binds and evaluate the facts
        for (int idx = 0; idx < this.binds.length; idx++) {
            // we got the binding
            Binding bnd = binds[idx];
            eval = bnd.evaluate(leftlist, right);
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
        buf.append("HashedEqBNode-" + this.nodeID + "> ");
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
