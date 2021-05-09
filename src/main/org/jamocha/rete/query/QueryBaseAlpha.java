/*
 * Copyright 2002-2010 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
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
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rule.Defquery;

/**
 * @author Peter Lin
 * 
 * BaseAlpha is the baseAlpha node for all 1-input nodes.
 */
public abstract class QueryBaseAlpha extends BaseNode {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The operator to compare two values
     */
    protected int operator = Constants.EQUAL;
	/**
	 * The use of Slot(s) is similar to CLIPS design
	 */
	protected Slot slot = null;

    public QueryBaseAlpha(int id){
        super(id);
    }
    
    /**
     * Alpha nodes must implement this method
     * @param factInstance
     * @param engine
     */
    public abstract void assertFact(Fact factInstance, Rete engine, WorkingMemory mem) 
    throws AssertException;

    public int successorCount() {
    	return this.successorNodes.length;
    }
    
    /**
     * Method is used to pass a fact to the successor nodes
     * @param fact
     * @param engine
     */
    protected void propogateAssert(Fact fact, Rete engine, WorkingMemory mem)
    throws AssertException
    {
        for (int idx=0; idx < this.successorNodes.length; idx++) {
            Object nNode = this.successorNodes[idx];
            if (nNode instanceof QueryBaseAlpha) {
                QueryBaseAlpha next = (QueryBaseAlpha) nNode;
                next.assertFact(fact, engine, mem);
            } else if (nNode instanceof QueryBaseJoin) {
            	QueryBaseJoin next = (QueryBaseJoin) nNode;
                next.assertRight(fact,engine,mem);
            } else if (nNode instanceof QueryResultNode) {
            	QueryResultNode next = (QueryResultNode)nNode;
                Index inx = new Index(new Fact[]{fact});
                next.addResult(inx,engine,mem);
            }
        }
    }

    /**
     * Set the next node in the sequence of 1-input nodes.
     * The next node can be an AlphaNode or a LIANode.
     * @param node
     */
    public void addSuccessorNode(BaseNode node, Rete engine, WorkingMemory mem) 
    throws AssertException 
    {
    	addNode(node);
    }
    
    /**
     * Remove a successor node. It does not recursively tear down the network.
     * @param node
     * @param engine
     * @param mem
     * @throws AssertException
     */
    public void removeSuccessorNode(BaseNode node, Rete engine, WorkingMemory mem) 
    throws RetractException
    {
    	removeNode(node);
    }
    
	/**
	 * Method is used to decompose the network and make sure
	 * the nodes are detached from each other.
	 */
	public void removeAllSuccessors() {
		for (int idx=0; idx < this.successorNodes.length; idx++) {
			BaseNode bn = (BaseNode)this.successorNodes[idx];
			bn.removeAllSuccessors();
		}
		this.successorNodes = new BaseNode[0];
	}
    
    /**
     * Abstract implementation returns an int code for the
     * operator. To get the string representation, it should
     * be converted.
     */
    public int getOperator() {
        return this.operator;
    }
    
    /**
     * subclasses need to implement PrettyPrintString and print
     * out user friendly representation fo the node
     */
    public abstract String toPPString();
    /**
     * subclasses need to implement the toString and return a textual
     * form representation of the node.
     */
    public abstract String toString();
    
	public abstract QueryBaseAlpha clone(Rete engine, Defquery query);
}