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

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defquery;

/**
 *
 * LIANode stands for Left Input Adapter Node. Left input adapter node
 * can only only have 1 alphaNode above it. Left input adapater nodes are
 * not shared by multiple branches of the network, so it doesn't have any
 * memory.
 * 
 * @author Peter Lin
 */
public class QueryLIANode extends QueryBaseAlpha {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public QueryLIANode(int id){
        super(id);
    }
    
    /**
     * the implementation just propogates the assert down the network
     */
    public void assertFact(Fact fact, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
    	if (this.watch) {
    		engine.writeMessage("QLIANode (" + this.nodeID + ") :: " + 
    				fact.toFactString() + Constants.LINEBREAK);
    	}
        propogateAssert(fact, engine, mem);
	}

    /**
	 * Propogate the assert to the successor nodes
	 * @param fact
	 * @param engine
	 */
    protected void propogateAssert(Fact fact, Rete engine, WorkingMemory mem)
    throws AssertException
    {
        for (int idx=0; idx < this.successorNodes.length; idx++) {
            BaseNode nNode = this.successorNodes[idx];
            if (nNode instanceof QueryBaseJoin) {
            	QueryBaseJoin next = (QueryBaseJoin) nNode;
                Fact[] newf = {fact};
                next.assertLeft(new Index(newf),engine,mem);
            } else if (nNode instanceof QueryResultNode) {
                Fact[] newf = {fact};
                QueryResultNode tn = (QueryResultNode)nNode;
                tn.addResult(new Index(newf),engine,mem);
            }
        }
    }
    
    public String hashString() {
        return toString();
    }
    
    /**
     * the Left Input Adapter Node returns zero length string
     */
    public String toString(){
        return "";
    }

    /**
     * the Left input Adapter Node returns zero length string
     */
    public String toPPString(){
        return "";
    }

    public QueryBaseAlpha clone(Rete engine, Defquery query) {
		QueryLIANode clone = new QueryLIANode(engine.nextNodeId());
		clone.operator = this.operator;
		if (this.slot != null) {
			clone.slot = (Slot)this.slot.clone();
		}
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
