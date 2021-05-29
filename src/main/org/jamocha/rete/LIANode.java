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

import java.util.Iterator;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 *
 * LIANode stands for Left Input Adapter Node. Left input adapter node
 * can only only have 1 alphaNode above it. Left input adapater nodes are
 * not shared by multiple branches of the network, so it doesn't have any
 * memory.
 */
public class LIANode extends BaseAlpha {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public LIANode(int id){
        super(id);
    }
    
    /**
     * the implementation just propogates the assert down the network
     */
    public void assertFact(Fact fact, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
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
            if (nNode instanceof BaseJoin) {
                BaseJoin next = (BaseJoin) nNode;
                Fact[] newf = {fact};
                next.assertLeft(new Index(newf),engine,mem);
            } else if (nNode instanceof TerminalNode) {
                Fact[] newf = {fact};
                TerminalNode tn = (TerminalNode)nNode;
                tn.assertFacts(new Index(newf),engine,mem);
            }
        }
    }
    
    /**
     * Retract simply propogates it down the network
     */
    public void retractFact(Fact fact, Rete engine, WorkingMemory mem)
    throws RetractException
    {
        propogateRetract(fact, engine, mem);
	}

    /**
	 * propogate the retract
	 * 
	 * @param fact
	 * @param engine
	 */
    protected void propogateRetract(Fact fact, Rete engine, WorkingMemory mem)
    throws RetractException
    {
        for (int idx = 0; idx < this.successorNodes.length; idx++) {
            BaseNode nNode = this.successorNodes[idx];
            if (nNode instanceof BaseJoin) {
                BaseJoin next = (BaseJoin) nNode;
                Fact[] newf = { fact };
                next.retractLeft(new Index(newf), engine, mem);
            } else if (nNode instanceof TerminalNode) {
                TerminalNode next = (TerminalNode) nNode;
                Fact[] newf = { fact };
                next.retractFacts(new Index(newf), engine, mem);
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
                Iterator<?> itr = alpha.iterator();
                while (itr.hasNext()){
                    if (node instanceof BaseAlpha) {
                        BaseAlpha next = (BaseAlpha) node;
                        next.assertFact((Fact)itr.next(),engine,mem);
                    } else if (node instanceof BaseJoin) {
                        BaseJoin next = (BaseJoin) node;
                        Index inx = new Index(new Fact[]{(Fact)itr.next()});
                        next.assertLeft(inx,engine,mem);
                    }
                }
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
}
