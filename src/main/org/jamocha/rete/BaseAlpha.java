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
package org.jamocha.rete;

import java.util.Iterator;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 * 
 * BaseAlpha is the baseAlpha node for all 1-input nodes.
 */
public abstract class BaseAlpha extends BaseNode {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * The operator to compare two values
     */
    protected int operator = Constants.EQUAL;


    public BaseAlpha(int id){
        super(id);
    }
    
    /**
     * Alpha nodes must implement this method
     * @param factInstance
     * @param engine
     */
    public abstract void assertFact(Fact factInstance, Rete engine, WorkingMemory mem) 
    throws AssertException;

    /**
     * Alpha nodes must implement this method. Retract should remove
     * a fact from the node and propogate through the RETE network.
     * @param factInstance
     * @param engine
     */
    public abstract void retractFact(Fact factInstance, Rete engine, WorkingMemory mem) 
    throws RetractException;
    
    public int successorCount() {
    	return this.successorNodes.length;
    }
    
    /**
     * method for propogating the retract
     * @param fact
     * @param engine
     */
    protected void propogateRetract(Fact fact, Rete engine, WorkingMemory mem)
    throws RetractException
    {
        for (int idx=0; idx < this.successorNodes.length; idx++) {
            Object nNode = this.successorNodes[idx];
            if (nNode instanceof BaseAlpha) {
                BaseAlpha next = (BaseAlpha) nNode;
                next.retractFact(fact,engine,mem);
            } else if (nNode instanceof BaseJoin) {
            	BaseJoin next = (BaseJoin) nNode;
                // AlphaNodes always call retractRight in the
                // BetaNode
                next.retractRight(fact,engine,mem);
            } else if (nNode instanceof TerminalNode) {
                Index inx = new Index(new Fact[]{fact});
            	((TerminalNode)nNode).retractFacts(inx,engine,mem);
            }
        }
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
            if (nNode instanceof BaseAlpha) {
                BaseAlpha next = (BaseAlpha) nNode;
                next.assertFact(fact, engine, mem);
            } else if (nNode instanceof BaseJoin) {
            	BaseJoin next = (BaseJoin) nNode;
                next.assertRight(fact,engine,mem);
            } else if (nNode instanceof TerminalNode) {
                TerminalNode next = (TerminalNode)nNode;
                Index inx = new Index(new Fact[]{fact});
                next.assertFacts(inx,engine,mem);
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
        if (addNode(node)) {
            // if there are matches, we propogate the facts to 
            // the new successor only
            AlphaMemory alpha = (AlphaMemory)mem.getAlphaMemory(this);
            if (alpha.size() > 0){
                Iterator itr = alpha.iterator();
                while (itr.hasNext()){
                    if (node instanceof BaseAlpha) {
                        BaseAlpha next = (BaseAlpha) node;
                        next.assertFact((Fact)itr.next(),engine,mem);
                    } else if (node instanceof BaseJoin) {
                        BaseJoin next = (BaseJoin) node;
                        next.assertRight((Fact)itr.next(),engine,mem);
                    } else if (node instanceof TerminalNode) {
                    	TerminalNode next = (TerminalNode)node;
                        Index inx = new Index(new Fact[]{(Fact)itr.next()});
                    	next.assertFacts(inx,engine,mem);
                    }
                }
            }
        }
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
        if (removeNode(node)) {
            // we retract the memories first, before removing the node
            AlphaMemory alpha = (AlphaMemory)mem.getAlphaMemory(this);
            if (alpha.size() > 0) {
                Iterator itr = alpha.iterator();
                while (itr.hasNext()) {
                    if (node instanceof BaseAlpha) {
                        BaseAlpha next = (BaseAlpha)node;
                        next.retractFact((Fact)itr.next(),engine,mem);
                    } else if (node instanceof BaseJoin) {
                        BaseJoin next = (BaseJoin)node;
                        next.retractRight((Fact)itr.next(),engine,mem);
                    }
                }
            }
        }
    }
    
    /**
     * Get the list of facts that have matched the node
     * @return
     */
    public AlphaMemory getMemory(WorkingMemory mem){
        return (AlphaMemory)mem.getAlphaMemory(this);
    }
    
    /**
     * implementation simply clear the arraylist
     */
    public void clear(WorkingMemory mem) {
        getMemory(mem).clear();
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
     * Subclasses need to implement this method. The hash string
     * should be the slotId + operator + value
     */
    public abstract String hashString();
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
        this.useCount = 0;
	}
}