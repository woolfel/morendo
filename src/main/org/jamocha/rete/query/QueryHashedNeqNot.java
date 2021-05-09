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
 */
public class QueryHashedNeqNot extends QueryBaseNot {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QueryHashedNeqNot(int id){
        super(id);
    }

    /**
     * clear will clear the lists
     */
    public void clear(WorkingMemory mem){
        Map leftmem = (Map)mem.getQueryBetaMemory(this);
        HashedNeqAlphaMemory rightmem = 
        	(HashedNeqAlphaMemory)mem.getQueryRightMemory(this);
        Iterator itr = leftmem.keySet().iterator();
        // first we iterate over the list for each fact
        // and clear it.
        while (itr.hasNext()){
            BetaMemory bmem = (BetaMemory)leftmem.get(itr.next());
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
		HashedNeqAlphaMemory rightmem = (HashedNeqAlphaMemory) mem.getQueryRightMemory(this);
		NotEqHashIndex inx = new NotEqHashIndex(NodeUtils.getRightBindValues(this.binds,rfact));
		rightmem.addPartialMatch(inx, rfact, engine);
    }
    
    /**
     * ExecuteJoin will perform an index join and propogate the partial matches
     * down the query network.
     */
    public void executeJoin(Rete engine, WorkingMemory mem) throws AssertException {
        Map leftmem = (Map) mem.getQueryBetaMemory(this);
        Iterator iterator = leftmem.values().iterator();
        while (iterator.hasNext()) {
    		Index index = (Index)iterator.next();
    		NotEqHashIndex inx = new NotEqHashIndex(NodeUtils.getLeftBindValues(this.binds,index.getFacts()));
    		HashedNeqAlphaMemory rightmem = (HashedNeqAlphaMemory) mem.getQueryRightMemory(this);
    		if (rightmem.zeroMatch(inx)) {
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
    public String toString(){
        StringBuffer buf = new StringBuffer();
        for (int idx=0; idx < this.binds.length; idx++){
            if (idx > 0){
                buf.append(" && ");
            }
            buf.append(this.binds[idx].toBindString());
        }
        return buf.toString();
    }

    /**
     * returs the node name + id and bindings
     */
    public String toPPString(){
        StringBuffer buf = new StringBuffer();
        buf.append("HashedNotEqNJoin-" + this.nodeID + "> ");
        for (int idx=0; idx < this.binds.length; idx++){
            if (idx > 0){
                buf.append(" && ");
            }
            if (this.binds[idx] != null) {
                buf.append(this.binds[idx].toPPString());
            }
        }
        return buf.toString();
    }

	public QueryHashedNeqNot clone(Rete engine, Defquery query) {
		QueryHashedNeqNot clone = new QueryHashedNeqNot(engine.nextNodeId());
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
