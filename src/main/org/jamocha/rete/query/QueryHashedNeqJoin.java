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
 * 
 * QueryHashedNeqJoin indexes the right input for joins that use
 * not equal to. It uses 2 levels of indexing. The first is the bindings
 * for equal to, the second is not equal to.
 * 
 * @author Peter Lin
 */
public class QueryHashedNeqJoin extends QueryBaseJoin {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QueryHashedNeqJoin(int id){
        super(id);
    }

    /**
     * clear will clear the lists
     */
	public void clear(WorkingMemory mem){
        Map<?, ?> leftmem = (Map<?, ?>)mem.getQueryBetaMemory(this);
        HashedNeqAlphaMemory rightmem = 
        	(HashedNeqAlphaMemory)mem.getQueryRightMemory(this);
        Iterator<?> itr = leftmem.keySet().iterator();
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
	@SuppressWarnings("unchecked")
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
        Map<Index, Index> leftmem = (Map<Index, Index>) mem.getQueryBetaMemory(this);

		leftmem.put(linx, linx);
		// need to think the getLeftValues through better to
		// account for cases when a join has no bindings
		NotEqHashIndex inx = new NotEqHashIndex(NodeUtils.getLeftBindValues(this.binds,linx.getFacts()));
		HashedNeqAlphaMemory rightmem = (HashedNeqAlphaMemory) mem.getQueryRightMemory(this);
		Object[] objs = rightmem.iterator(inx);
		if (objs != null && objs.length > 0) {
			for (int idx = 0; idx < objs.length; idx++) {
				Fact rfcts = (Fact) objs[idx];
				// now we propogate
				this.propogateAssert(linx.add(rfcts), engine, mem);
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
    throws AssertException
    {
        // get the memory for the node
		HashedNeqAlphaMemory rightmem = (HashedNeqAlphaMemory) mem.getQueryRightMemory(this);
		NotEqHashIndex inx = new NotEqHashIndex(NodeUtils.getRightBindValues(this.binds,rfact));

		rightmem.addPartialMatch(inx, rfact, engine);
		// now that we've added the facts to the list, we
		// proceed with evaluating the fact
		// else we compare the fact to all facts in the left
		Map<?, ?> leftmem = (Map<?, ?>) mem.getQueryBetaMemory(this);
		// since there may be key collisions, we iterate over the
		// values of the HashMap. If we used keySet to iterate,
		// we could encounter a ClassCastException in the case of
		// key collision.
		Iterator<?> itr = leftmem.values().iterator();
		while (itr.hasNext()) {
			Index linx = (Index) itr.next();
			if (this.evaluate(linx.getFacts(), rfact)) {
				// now we propogate
				this.propogateAssert(linx.add(rfact), engine, mem);
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
        buf.append("HashedNotEqBNode-" + this.nodeID + "> ");
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

	public QueryHashedNeqJoin clone(Rete engine, Defquery query) {
		QueryHashedNeqJoin clone = new QueryHashedNeqJoin(engine.nextNodeId());
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
