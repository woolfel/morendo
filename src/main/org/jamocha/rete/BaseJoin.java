/*
 * Copyright 2002-2008 Peter Lin
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

import java.util.Map;
import java.util.Iterator;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * @author Peter Lin
 *
 * BaseJoin is the abstract base for all join node classes.
 */
public abstract class BaseJoin extends BaseNode {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * binding for the join
     */
    protected Binding[] binds = null;

    /**
	 * @param id
	 */
	public BaseJoin(int id) {
		super(id);
	}

	/**
	 * Subclasses must implement this method. assertLeft takes
	 * inputs from left input adapter nodes and join nodes.
	 * @param lfacts
	 * @param engine
	 */
	public abstract void assertLeft(Index lfacts, Rete engine,
			WorkingMemory mem) throws AssertException;

	/**
	 * Subclasses must implement this method. assertRight takes
	 * input from alpha nodes.
	 * @param rfact
	 * @param engine
	 */
	public abstract void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
			throws AssertException;

	/**
	 * Subclasses must implement this method. retractLeft takes
	 * input from left input adapter nodes and join nodes.
	 * @param lfacts
	 * @param engine
	 */
	public abstract void retractLeft(Index lfacts, Rete engine,
			WorkingMemory mem) throws RetractException;

	/**
	 * Subclasses must implement this method. retractRight takes
	 * input from alpha nodes.
	 * @param rfact
	 * @param engine
	 */
	public abstract void retractRight(Fact rfact, Rete engine, WorkingMemory mem)
			throws RetractException;

    /**
     * Set the bindings for this join
     * 
     * @param binds
     */
    public void setBindings(Binding[] binds) {
        this.binds = binds;
    }

    public void addSuccessorNode(BaseNode node, Rete engine, WorkingMemory mem)
    throws AssertException {
        if (node instanceof BaseJoin) {
            addSuccessorNode((BaseJoin)node,engine,mem);
        } else {
            addSuccessorNode((TerminalNode)node,engine,mem);
        }
    }

	/**
	 * When new Successor nodes are added, we propogate the facts that matched to
	 * the new join node.
	 * @param node
	 * @param engine
	 * @param mem
	 * @throws AssertException
	 */
	public void addSuccessorNode(BaseJoin node, Rete engine, WorkingMemory mem)
			throws AssertException {
        if (addNode(node)) {
			// first, we get the memory for this node
			Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
			// now we iterate over the entry set
			Iterator<?> itr = leftmem.entrySet().iterator();
			while (itr.hasNext()) {
				BetaMemory bmem = (BetaMemory) itr.next();
				Index left = bmem.getIndex();
				// iterate over the matches
                Map<?, ?> rightmem = (Map<?, ?>) mem.getBetaRightMemory(this);
				Iterator<?> ritr = rightmem.keySet().iterator();
				while (ritr.hasNext()) {
					Fact rfcts = (Fact) ritr.next();
					// now assert in the new join node
					node.assertLeft(left.add(rfcts), engine, mem);
				}
			}
		}
	}

	/**
	 * it's unlikely 2 rules are identical, except for the name. The implementation
	 * gets the current memory and propogates, but I wonder how much sense this
	 * makes in a real production environment. An user really shouldn't be deploying
	 * identical rules with different rule name.
	 * @param node
	 * @param engine
	 * @param mem
	 * @throws AssertException
	 */
	public void addSuccessorNode(TerminalNode node, Rete engine,
			WorkingMemory mem) throws AssertException {
        if (addNode(node)) {
			// first, we get the memory for this node
			Map<?, ?> leftmem = (Map<?, ?>) mem.getBetaLeftMemory(this);
			// now we iterate over the entry set
			Iterator<?> itr = leftmem.values().iterator();
			while (itr.hasNext()) {
				Object omem = itr.next();
				if (omem instanceof BetaMemory) {
					BetaMemory bmem = (BetaMemory) omem;
					Index left = bmem.getIndex();
					// iterate over the matches
                    Map<?, ?> rightmem = (Map<?, ?>) mem.getBetaRightMemory(this);
                    Iterator<?> ritr = rightmem.keySet().iterator();
					while (ritr.hasNext()) {
						Fact rfcts = (Fact) ritr.next();
						// merge the left and right fact into a new Array
						node.assertFacts(left.add(rfcts), engine, mem);
					}
				}
			}
		}
	}

	/**
	 * Method is used to decompose the network and make sure
	 * the nodes are detached from each other.
	 */
	public void removeAllSuccessors() {
		for (int idx=0; idx < this.successorNodes.length; idx++) {
			BaseNode bn = (BaseNode) this.successorNodes[idx];
			bn.removeAllSuccessors();
		}
		this.successorNodes = new BaseNode[0];
        this.useCount = 0;
	}
	
	/**
	 * Method is used to pass a fact to the successor nodes
	 * @param fact
	 * @param engine
	 */
	protected void propagateAssert(Index inx, Rete engine, WorkingMemory mem)
			throws AssertException {
        for (int idx=0; idx < this.successorNodes.length; idx++) {
            BaseNode node = this.successorNodes[idx];
			if (node instanceof BaseJoin) {
				((BaseJoin) node).assertLeft(inx, engine, mem);
			} else if (node instanceof TerminalNode) {
				((TerminalNode) node).assertFacts(inx, engine, mem);
			}
		}
	}

	/**
	 * method for propogating the retract
	 * @param fact
	 * @param engine
	 */
	protected void propagateRetract(Index inx, Rete engine, WorkingMemory mem)
			throws RetractException {
        for (int idx=0; idx < this.successorNodes.length; idx++) {
            BaseNode node = this.successorNodes[idx];
			if (node instanceof BaseJoin) {
				((BaseJoin) node).retractLeft(inx, engine, mem);
			} else if (node instanceof TerminalNode) {
				((TerminalNode) node).retractFacts(inx, engine, mem);
			}
		}
	}
}
