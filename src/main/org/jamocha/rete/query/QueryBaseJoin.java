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
import org.jamocha.rete.Binding;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defquery;

/**
 * @author Peter Lin
 *
 * BaseJoin is the abstract base for all join node classes.
 */
public abstract class QueryBaseJoin extends BaseNode {

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
	public QueryBaseJoin(int id) {
		super(id);
	}

	/**
	 * Subclasses must implement this method. assertLeft takes
	 * inputs from left input adapter nodes and join nodes.
	 * @param lfacts
	 * @param engine
	 */
	public abstract void assertLeft(Index lfacts, Rete engine, WorkingMemory mem) throws AssertException;

	/**
	 * Subclasses must implement this method. assertRight takes
	 * input from alpha nodes.
	 * @param rfact
	 * @param engine
	 */
	public abstract void assertRight(Fact rfact, Rete engine, WorkingMemory mem)
			throws AssertException;

    /**
     * Set the bindings for this join
     * 
     * @param binds
     */
    public void setBindings(Binding[] binds) {
        this.binds = binds;
    }

    public void addSuccessorNode(BaseNode node, Rete engine, WorkingMemory mem)
    {
		addNode(node);
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
	protected void propogateAssert(Index inx, Rete engine, WorkingMemory mem)
			throws AssertException {
        for (int idx=0; idx < this.successorNodes.length; idx++) {
            BaseNode node = this.successorNodes[idx];
			if (node instanceof QueryBaseJoin) {
				((QueryBaseJoin) node).assertLeft(inx, engine, mem);
			} else if (node instanceof QueryResultNode) {
				((QueryResultNode) node).addResult(inx, engine, mem);
			}
		}
	}
	
	public abstract QueryBaseJoin clone(Rete engine, Defquery query);
}
