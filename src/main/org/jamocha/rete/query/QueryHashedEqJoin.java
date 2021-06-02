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

import java.util.Map;
import java.util.Iterator;

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Constants;
import org.jamocha.rete.EqHashIndex;
import org.jamocha.rete.Fact;
import org.jamocha.rete.HashedAlphaMemoryImpl;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.util.NodeUtils;
import org.jamocha.rule.Defquery;

/**
 * 
 * @author Peter Lin
 */
public class QueryHashedEqJoin extends QueryBaseJoin {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public QueryHashedEqJoin(int id) {
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
	@SuppressWarnings("unchecked")
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem)
            throws AssertException {
        Map<Index, Index> leftmem = (Map<Index, Index>) mem.getQueryBetaMemory(this);
        leftmem.put(linx, linx);
        EqHashIndex inx = new EqHashIndex(NodeUtils.getLeftValues(this.binds,linx.getFacts()));
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem.getQueryRightMemory(this);
        Iterator<?> itr = rightmem.iterator(inx);
    	if (this.watch) {
    		engine.writeMessage("QueryHashedEqJoin (" + this.nodeID + ") - assertLeft:: " + 
    				linx.toPPString() + Constants.LINEBREAK);
    	}
        if (itr != null) {
            while (itr.hasNext()) {
                Fact vl = (Fact) itr.next();
                if (vl != null) {
                    this.propogateAssert(linx.add(vl), engine, mem);
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
        HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem.getQueryRightMemory(this);
        EqHashIndex inx = new EqHashIndex(NodeUtils.getRightValues(this.binds,rfact));

        rightmem.addPartialMatch(inx, rfact, engine);
        // now that we've added the facts to the list, we
        // proceed with evaluating the fact
        Map<?, ?> leftmem = (Map<?, ?>) mem.getQueryBetaMemory(this);
        // since there may be key collisions, we iterate over the
        // values of the HashMap. If we used keySet to iterate,
        // we could encounter a ClassCastException in the case of
        // key collision.
        Iterator<?> itr = leftmem.values().iterator();
    	if (this.watch) {
    		engine.writeMessage("QueryHashedEqJoin (" + this.nodeID + ") - assertRight::Rmem:: " + 
    				rfact.toFactString() + Constants.LINEBREAK);
    		engine.writeMessage("QueryHashedEqJoin (" + this.nodeID + ") - assertRight::Lmem-count:: " + 
    				leftmem.size() + Constants.LINEBREAK);
    	}
        while (itr.hasNext()) {
            Index linx = (Index) itr.next();
            if (this.evaluate(linx.getFacts(), rfact)) {
                // now we propogate
                this.propogateAssert(linx.add(rfact), engine, mem);
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
    
    public void garbageCollect(Rete engine, WorkingMemory memory) {
    	
    }
    
	public QueryHashedEqJoin clone(Rete engine, Defquery query) {
		QueryHashedEqJoin clone = new QueryHashedEqJoin(engine.nextNodeId());
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
