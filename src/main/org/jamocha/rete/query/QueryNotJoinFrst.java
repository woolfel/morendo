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
import org.jamocha.rete.BetaMemory;
import org.jamocha.rete.BetaMemoryImpl;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Binding2;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Evaluate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defquery;

/**
 * 
 * NotJoinFrst is a special node for rules that have NOTCE for the first
 * CE. The node does not propogate the initial fact down the network. 
 * 
 * @author Peter Lin
 */
public class QueryNotJoinFrst extends QueryBaseNot {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public QueryNotJoinFrst(int id){
        super(id);
    }

    /**
     * clear will clear the lists
     */
	public void clear(WorkingMemory mem){
        Map<?, ?> rightmem = (Map<?, ?>)mem.getQueryRightMemory(this);
        Map<?, ?> leftmem = (Map<?, ?>)mem.getQueryBetaMemory(this);
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
     * assertLeft for the query node works different than the regular NotJoinFrst node.
     * If there's any memories in the right, it means there's a match. Since the node
     * is NOTCE, we don't propogate. The node only propogates when there's no memories
     * in the right side.
     * @param factInstance
     * @param engine
     */
    @SuppressWarnings({ "unchecked" })
	public void assertLeft(Index linx, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
        Map<Index, BetaMemory> leftmem = (Map<Index, BetaMemory>)mem.getQueryBetaMemory(this);
        BetaMemory bmem = new BetaMemoryImpl(linx, engine);
        leftmem.put(bmem.getIndex(),bmem);
    }

    /**
     * Assert from the right side is always going to be from an
     * Alpha node.
     * @param factInstance
     * @param engine
     */
    @SuppressWarnings({ "unchecked" })
	public void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
    throws AssertException
    {
        // we only proceed if the fact hasn't already entered
        // the join node
        Map<Fact, Fact> rightmem = (Map<Fact, Fact>)mem.getQueryRightMemory(this);
        rightmem.put(rfact,rfact);
    }
    
    /**
     * When the first conditional element is NOTCE for a query, we simply
     * check to see if there's any memories in the right. If there isn't,
     * we propogate down the network. If there is atleast 1 match, we
     * do not propogate.
     * @param engine
     * @param mem
     * @throws AssertException
     */
    public void executeJoin(Rete engine, WorkingMemory mem) throws AssertException {
        Map<?, ?> rightmem = (Map<?, ?>)mem.getQueryRightMemory(this);
        if (rightmem.size() == 0){
            Index index = new Index(new Fact[0]);
            this.propogateAssert(index,engine,mem);
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
     * Method will evaluate a single slot from the left against the right.
     * @param left
     * @param leftId
     * @param right
     * @param rightId
     * @return
     */
    public boolean evaluate(Fact left, int leftId, Fact right, int rightId, int opr){
        if (opr == Constants.NOTEQUAL) {
            return Evaluate.evaluateNotEqual(left.getSlotValue(leftId),
                    right.getSlotValue(rightId));
        } else {
            return Evaluate.evaluateEqual(left.getSlotValue(leftId),
                    right.getSlotValue(rightId));
        }
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

	public QueryNotJoinFrst clone(Rete engine, Defquery query) {
		QueryNotJoinFrst clone = new QueryNotJoinFrst(engine.nextNodeId());
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
