/*
 * Copyright 2002-200 Peter Lin
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
import org.jamocha.rete.BetaMemory;
import org.jamocha.rete.BetaMemoryImpl;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Binding2;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defquery;

/**
 * QueryNotJoin is different than the normal RETE version. This is
 * because of how queries work. The NOTCE nodes for queries have
 * to call an additional method named executeJoin to propogate down
 * the network after all the alpha nodes have been evaluated.
 * @author Peter Lin
 * 
 */
public class QueryNotJoin extends QueryBaseNot {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public QueryNotJoin(int id){
        super(id);
    }

    /**
     * clear will clear the lists
     */
    public void clear(WorkingMemory mem){
        Map rightmem = (Map)mem.getQueryRightMemory(this);
        Map leftmem = (Map)mem.getQueryBetaMemory(this);
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
     * For queries, do not propogate during assert. 
     * @param factInstance
     * @param engine
     */
    public void assertLeft(Index linx, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
        Map leftmem = (Map)mem.getQueryBetaMemory(this);
        // we create a new list for storing the matches.
        // any fact that isn't in the list will be evaluated.
        BetaMemory bmem = new BetaMemoryImpl(linx, engine);
        leftmem.put(bmem.getIndex(),bmem);
        Map rightmem = (Map)mem.getQueryRightMemory(this);
        Iterator itr = rightmem.values().iterator();
        while (itr.hasNext()){
            Fact rfcts = (Fact)itr.next();
            if (this.evaluate(linx.getFacts(),rfcts,engine)){
                // it matched, so we add it to the beta memory
                bmem.addMatch(rfcts);
            }
        }
    }

    /**
     * Assert from the right side is always going to be from an
     * Alpha node.
     * @param factInstance
     * @param engine
     */
    public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
    throws AssertException
    {
        // we only proceed if the fact hasn't already entered
        // the join node
        Map rightmem = (Map)mem.getQueryRightMemory(this);
        rightmem.put(rfact,rfact);
        // now that we've added the facts to the list, we
        // proceed with evaluating the fact
        Map leftmem = (Map)mem.getQueryBetaMemory(this);
        Iterator itr = leftmem.values().iterator();
        while (itr.hasNext()){
            BetaMemory bmem = (BetaMemory)itr.next();
            Index linx = bmem.getIndex();
            if (this.evaluate(linx.getFacts(),rfact,engine)){
                bmem.addMatch(rfact);
            }
        }
    }

    /**
     * ExecuteJoin is responsible for 
     * @param engine
     * @param mem
     * @throws AssertException
     */
    public void executeJoin(Rete engine, WorkingMemory mem) throws AssertException {
        Map leftmem = (Map)mem.getBetaLeftMemory(this);
    	Iterator iterator = leftmem.values().iterator();
    	while (iterator.hasNext()) {
    		BetaMemory bmem = (BetaMemory)iterator.next();
    		if (bmem.matchCount() == 0) {
    			this.propogateAssert(bmem.getIndex(), engine, mem);
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
    public boolean evaluate(Fact[] leftlist, Fact right, Rete engine){
        boolean eval = true;
        // we iterate over the binds and evaluate the facts
        for (int idx=0; idx < this.binds.length; idx++){
            Binding bnd = binds[idx];
            if (bnd instanceof Binding) {
                eval = ((Binding2)bnd).evaluate(leftlist, right,engine);
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
     * 
     */
    public String toString(){
        StringBuffer buf = new StringBuffer();
        buf.append("NOT CE - ");
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
        buf.append("node-" + this.nodeID + "> NOT CE - ");
        for (int idx=0; idx < this.binds.length; idx++){
            if (idx > 0){
                buf.append(" && ");
            }
            buf.append(this.binds[idx].toPPString());
        }
        return buf.toString();
    }

	public QueryNotJoin clone(Rete engine, Defquery query) {
		QueryNotJoin clone = new QueryNotJoin(engine.nextNodeId());
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