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
import org.jamocha.rete.Binding;
import org.jamocha.rete.EqHashIndex;
import org.jamocha.rete.Fact;
import org.jamocha.rete.HashedAlphaMemoryImpl;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.util.NodeUtils;
import org.jamocha.rule.Defquery;

/**
 * QueryHashedEqNot is different than the RETE version. The main difference
 * is the query version just adds the fact(s) to the corresponding memory
 * and doesn't propogate. Instead executeJoin is used to perform an index
 * join and propogate the partial matches down the query network.
 * 
 * @author Peter Lin
 * 
 */
public class QueryHashedEqNot extends QueryBaseNot {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public QueryHashedEqNot(int id){
        super(id);
    }

    /**
     * assertLeft takes an array of facts. Since the next join may be
     * joining against one or more objects, we need to pass all
     * previously matched facts.
     * @param factInstance
     * @param engine
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
        Map leftmem = (Map) mem.getQueryBetaMemory(this);
        leftmem.put(linx, linx);
    }

    /**
	 * Assert from the right side is always going to be from an Alpha node.
	 * 
	 * @param factInstance
	 * @param engine
	 */
    public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
    throws AssertException
    {
        // get the memory for the node
		HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem.getQueryRightMemory(this);
		EqHashIndex inx = new EqHashIndex(NodeUtils.getRightValues(this.binds,rfact));
		rightmem.addPartialMatch(inx, rfact, engine);
    }
    
    /**
     * ExecuteJoin performs the join using EqHashIndex and propogates any partial
     * matches down the query network.
     */
    @SuppressWarnings("rawtypes")
	public void executeJoin(Rete engine, WorkingMemory mem) throws AssertException {
        Map leftmem = (Map) mem.getQueryBetaMemory(this);
    	Iterator iterator = leftmem.values().iterator();
    	while (iterator.hasNext()) {
    		Index index = (Index)iterator.next();
            EqHashIndex inx = new EqHashIndex(NodeUtils.getLeftValues(this.binds,index.getFacts()));
            HashedAlphaMemoryImpl rightmem = (HashedAlphaMemoryImpl) mem.getQueryRightMemory(this);
            if (rightmem.count(inx) == 0) {
                this.propogateAssert(index, engine, mem);
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
    public boolean evaluate(Fact[] leftlist, Fact right){
        boolean eval = true;
        // we iterate over the binds and evaluate the facts
        for (int idx=0; idx < this.binds.length; idx++){
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
     * method returns a simple format for the node
     */
    public String toString(){
        StringBuffer buf = new StringBuffer();
        buf.append("HashedEqNJoin- ");
        for (int idx=0; idx < this.binds.length; idx++){
            if (idx > 0){
                buf.append(" && ");
            }
            buf.append(this.binds[idx].toBindString());
        }
        return buf.toString();
    }

    /**
     * The current implementation is similar to BetaNode
     */
    public String toPPString(){
        StringBuffer buf = new StringBuffer();
        buf.append("HashedEqNJoin-" + this.nodeID + "> ");
        for (int idx=0; idx < this.binds.length; idx++){
            if (idx > 0){
                buf.append(" && ");
            }
            buf.append(this.binds[idx].toPPString());
        }
        return buf.toString();
    }

	public QueryHashedEqNot clone(Rete engine, Defquery query) {
		QueryHashedEqNot clone = new QueryHashedEqNot(engine.nextNodeId());
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
		query.addNotNode(clone);
		return clone;
	}
}
