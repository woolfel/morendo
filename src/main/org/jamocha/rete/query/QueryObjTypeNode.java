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

import java.io.Serializable;
import java.util.Iterator;

import org.jamocha.rete.AlphaMemory;
import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Fact;
import org.jamocha.rete.ObjectTypeNode;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.Defquery;
import org.jamocha.rule.GraphQuery;

/**
 * 
 * 
 * @author Peter Lin
 */
public class QueryObjTypeNode extends QueryBaseAlpha implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The Class that defines object type
     */
    private Template deftemplate = null;
    
    protected ObjectTypeNode objectTypeNode = null;
    /**
     * It's useful to keep track of the fact count, so that we can use it
     * at execution time. If the rule has joins that involve the template
     * type, but the count is zero we can avoid running the query. It's
     * also good for statistics.
     */
    protected int factCount = 0;
    
	/**
	 * 
	 */
	public QueryObjTypeNode(Rete engine, ObjectTypeNode objTypeNode) {
		super(engine.nextNodeId());
        this.deftemplate = objTypeNode.getDeftemplate();
        this.objectTypeNode = objTypeNode;
	}

    public Template getDeftemplate(){
        return this.deftemplate;
    }
    
    /**
     * AssertFact works differently than the normal ObjectTypeNode. Rather than
     * iterate over all the facts in the working memory, the implementation
     * gets the memories for the corresponding ObjectTypeNode. Once it has that,
     * it propagates the facts down the network.
     * A QueryObjTypeNode must have a valid ObjectTypeNode. Since the network
     * will propagate the facts to the ObjectTypeNode, we can avoid iterating
     * over the facts to figure out which ones match. 
     * @param fact
     * @param engine
     */
    @SuppressWarnings("rawtypes")
	public void assertFact(Fact fact, Rete engine, WorkingMemory mem)
    throws AssertException
    {
    	if (fact == null) {
        	AlphaMemory alphaMemory = (AlphaMemory)mem.getAlphaMemory(this.objectTypeNode);
        	factCount = alphaMemory.size();
        	Iterator iterator = alphaMemory.iterator();
        	while (iterator.hasNext()) {
        		Fact f = (Fact)iterator.next();
        		this.propogateAssert(f, engine, mem);
        	}
    	} else {
    		this.propogateAssert(fact, engine, mem);
    	}
    }
    
    /**
     * For the ObjectTypeNode, the method just returns toString
     */
    public String hashString() {
        return toString();
    }
    
    /**
     * this returns name of the deftemplate
     */
    public String toString(){
        return "QueryObjTypeNode( " + this.deftemplate.getName() + " ) -";
    }

    /**
     * this returns name of the deftemplate
     */
    public String toPPString(){
        return " Template( " + this.deftemplate.getName() + " )";
    }
    
    public Object[] getSuccessorNodes() {
    	return this.successorNodes;
    }
    
    public int getFactCount() {
    	return this.factCount;
    }
    
    public QueryObjTypeNode clone(Rete engine, Defquery query) {
    	QueryObjTypeNode clone = new QueryObjTypeNode(engine, this.objectTypeNode);
    	clone.deftemplate = this.deftemplate;
    	clone.factCount = this.factCount;
    	clone.successorNodes = new BaseNode[this.successorNodes.length];
    	for (int i=0; i < this.successorNodes.length; i++) {
    		if (this.successorNodes[i] instanceof QueryBaseAlpha) {
    			clone.successorNodes[i] = ((QueryBaseAlpha)this.successorNodes[i]).clone(engine, query);
    		} else if (this.successorNodes[i] instanceof QueryBaseJoin) {
    			clone.successorNodes[i] = ((QueryBaseJoin)this.successorNodes[i]).clone(engine, query);
    			if (query instanceof GraphQuery) {
    				query.addJoinNode((QueryBaseJoin)clone.successorNodes[i]);
    			}
    		}
    	}
    	return clone;
    }
}
