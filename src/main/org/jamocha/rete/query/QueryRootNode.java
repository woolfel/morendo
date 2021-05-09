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
import java.util.Map;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Fact;
import org.jamocha.rete.ObjectTypeNode;
import org.jamocha.rete.Rete;
import org.jamocha.rete.RootNode;
import org.jamocha.rete.Template;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rule.Defquery;
import org.jamocha.rule.GraphQuery;

/**
 * 
 * QueryRootNode does not extend BaseNode like all other RETE nodes. This is
 * done for a couple of reasons.<br/>
 * <ul>
 * <li> RootNode doesn't need to have a memory </li>
 * <li> RootNode only has QueryObjectTypeNode for successors</li>
 * <li> RootNode doesn't need the toPPString and other string methods</li>
 * </ul>
 * In the future, the design may change. For now, I've decided to keep
 * it as simple as necessary.
 * 
 * @author Peter Lin
 */
public class QueryRootNode implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected Map queryObjTypeNodeMap = null;
    protected RootNode root = null;
    protected QueryObjTypeNode initialFactObjTypeNode = null;

    /**
	 * 
	 */
	public QueryRootNode(Rete engine, RootNode root) {
		super();
		queryObjTypeNodeMap = engine.newMap();
		this.root = root;
	}
    
	public RootNode getRootNode() {
		return this.root;
	}
	
    /**
     * Add a new ObjectTypeNode. The implementation will check to see
     * if the node already exists. It will only add the node if it
     * doesn't already exist in the network.
     * @param node
     */
    public void addQueryObjTypeNode(QueryObjTypeNode node) {
    	if (node.getDeftemplate().getName().equals(Constants.INITIAL_FACT)) {
    		this.initialFactObjTypeNode = node;
    	} else {
            if (!this.queryObjTypeNodeMap.containsKey(node.getDeftemplate()) ) {
                this.queryObjTypeNodeMap.put(node.getDeftemplate(),node);
            }
    	}
    }
    
    /**
     * The current implementation just removes the ObjectTypeNode
     * and doesn't prevent the removal. The method should be called
     * with care, since removing the ObjectTypeNode can have serious
     * negative effects. This would generally occur when an undeftemplate
     * occurs.
     */
    public void removeQueryObjTypeNode(QueryObjTypeNode node) {
        this.queryObjTypeNodeMap.remove(node.getDeftemplate());
    }

    /**
     * Return the HashMap with all the ObjectTypeNodes
     * @return
     */
    public Map getQueryObjTypeNodes() {
        return this.queryObjTypeNodeMap;
    }
    
    /**
     * Method returns the QueryObjTypeNode so the Query compiler can add
     * child nodes to it.
     * @param template
     * @return
     */
    public QueryObjTypeNode findQueryObjTypeNode(Template template) {
    	return (QueryObjTypeNode)this.queryObjTypeNodeMap.get(template);
    }
    
    /**
     * Important note, QueryRootNode doesn't actually propogate the fact down
     * the query network. The QueryObjTypeNodes get the list of facts from
     * the corresponding ObjectTypeNode.
     * @param fact
     * @param engine
     * @param mem
     * @throws AssertException
     */
    public synchronized void assertObject(Fact fact, Rete engine, WorkingMemory mem)
    throws AssertException
    {
    	if (fact != null) {
    		QueryObjTypeNode qotn = this.findQueryObjTypeNode(fact.getDeftemplate());
    		if (qotn != null) {
                qotn.assertFact(fact,engine,mem);
    		}
    	} else {
        	Iterator iterator = this.queryObjTypeNodeMap.keySet().iterator();
        	while (iterator.hasNext()) {
        		Template template = (Template)iterator.next();
            	QueryObjTypeNode qotn = (QueryObjTypeNode)this.queryObjTypeNodeMap.get(template);
                if (qotn != null) {
                    qotn.assertFact(null,engine,mem);
                }
                if (template.getParent() != null) {
                    assertObjectParent(template.getParent(),engine,mem);
                }
        	}
    	}
    }
    
    /**
     * Method will get the deftemplate's parent and do a lookup
     * @param fact
     * @param templates
     * @throws AssertException
     */
    public synchronized void assertObjectParent(Template template, 
            Rete engine, WorkingMemory mem)
    throws AssertException
    {
    	QueryObjTypeNode otn = (QueryObjTypeNode)this.queryObjTypeNodeMap.get(template);
        if (otn != null) {
            otn.assertFact(null,engine,mem);
        }
        if (template.getParent() != null) {
            assertObjectParent(template.getParent(),engine,mem);
        }
    }
    
    /**
     * Retract an object from the Working memory
     * @param objInstance
     */
    public synchronized void retractObject(Fact fact, Rete engine, WorkingMemory mem)
    throws RetractException
    {
    }
    
    /**
     * Method will get the deftemplate's parent and do a lookup
     * @param fact
     * @param templates
     * @throws AssertException
     */
    public synchronized void retractObjectParent(Fact fact, Template template, 
            Rete engine, WorkingMemory mem)
    throws RetractException
    {
    }
    
    public synchronized void clear() {
        Iterator itr = this.queryObjTypeNodeMap.values().iterator();
        while (itr.hasNext()) {
            ObjectTypeNode otn = (ObjectTypeNode)itr.next();
            otn.clearSuccessors();
        }
    	this.queryObjTypeNodeMap.clear();
    }
    
    /**
     * Clone method takes the engine and the new clone of the existing Defquery instance.
     * This is because we need to add the nodes to the clone instance during the clone
     * process.
     * @param engine
     * @param query
     * @return
     */
    public QueryRootNode clone(Rete engine, Defquery query) {
    	QueryRootNode clone = new QueryRootNode(engine, this.root);
    	Iterator iterator = this.queryObjTypeNodeMap.values().iterator();
    	while (iterator.hasNext()) {
    		QueryObjTypeNode qotn = (QueryObjTypeNode)iterator.next();
    		clone.addQueryObjTypeNode(qotn.clone(engine, query));
    	}
    	clone.initialFactObjTypeNode = this.initialFactObjTypeNode;
    	return clone;
    }
}
