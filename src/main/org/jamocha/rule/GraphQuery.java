/*
 * Copyright 2002-2010 Jamocha
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
package org.jamocha.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.RootNode;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.query.GraphResultNode;
import org.jamocha.rete.query.QueryBaseAlpha;
import org.jamocha.rete.query.QueryBaseJoin;
import org.jamocha.rete.query.QueryBaseNot;
import org.jamocha.rete.query.QueryFuncAlphaNode;
import org.jamocha.rete.query.QueryObjTypeNode;
import org.jamocha.rete.query.QueryParameterNode;
import org.jamocha.rete.query.QueryResultNode;

/**
 * @author Peter Lin
 *
 * 
 */
public class GraphQuery extends Defquery {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Fact[] graphData = null;
    
	/**
	 * 
	 */
	public GraphQuery() {
		super();
	}

    public GraphQuery(String name) {
        this();
        setName(name);
    }

    public void setGraphData(Fact[] data) {
    	this.graphData = data;
    }
    
    public void clearGraphData() {
    	this.graphData = null;
    }
    
    @Override
    public void setWatch(boolean watch) {
    	this.watch = watch;
    	for (int i=0; i < this.joins.size(); i++) {
    		QueryBaseJoin join = (QueryBaseJoin)this.joins.get(i);
    		join.setWatch(watch);
    	}
    	for (int i=0; i < this.conditions.size(); i++) {
    		Condition c = (Condition)this.conditions.get(i);
    		for (int n=0; n < c.getNodes().size(); n++) {
    			QueryBaseAlpha a = (QueryBaseAlpha)c.getNodes().get(n);
    			a.setWatch(watch);
    		}
    	}
    }
    
    /**
     * 
     */
    @Override
	public List executeQuery(Rete engine, WorkingMemory memory, Parameter[] parameters) {
		if (watch) {
			startTime = System.currentTimeMillis();
		}
		try {
			ArrayList params = new ArrayList(this.queryParameterNodeMap.values());
			for (int i=0; i < parameters.length; i++) {
				Object node = params.get(i);
				if (node instanceof QueryParameterNode) {
					QueryParameterNode pnode = (QueryParameterNode)node;
					pnode.setQueryParameterValue(parameters[i].getValue());
				} else if (node instanceof QueryFuncAlphaNode) {
					QueryFuncAlphaNode pnode = (QueryFuncAlphaNode)node;
					pnode.setQueryParameterValue(parameters[i].getValue());
				}
			}
			// first assert the facts
			for (int i=0; i < this.graphData.length; i++) {
				Fact f = (Fact)this.graphData[i];
				f.setFactId(engine);
				this.queryRoot.assertObject(f, engine, memory);
			}
		} catch (AssertException e) {
		}
		if (watch) {
			elapsedTime = System.currentTimeMillis() - startTime;
			engine.setQueryTime(this.name, this.elapsedTime);
		}
		return this.resultNode.getResults();
	}
    
	/**
	 * Each query should use a clone of the defquery.
	 */
	public GraphQuery clone(Rete engine) {
		GraphQuery clone = new GraphQuery(this.name);
		clone.setQueryParameters(this.queryParameterNodeMap);
		clone.bindings = this.bindings;
		clone.comment = this.comment;
		clone.conditions = this.conditions;
		clone.name = this.name;
		clone.watch = this.watch;
		clone.queryRoot = this.queryRoot.clone(engine, clone);
		QueryBaseJoin last = (QueryBaseJoin)clone.getLastNode();
		clone.resultNode = (QueryResultNode)last.getSuccessorNodes()[0];
		return clone;
	}

}
