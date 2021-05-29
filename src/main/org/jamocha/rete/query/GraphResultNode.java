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

import org.jamocha.rete.Constants;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rule.Defquery;
import org.jamocha.rule.GraphQuery;

/**
 * GraphResultNode takes the last fact in the graph query and adds it to 
 * the list
 * 
 * @author peter
 *
 */
public class GraphResultNode extends QueryResultNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private volatile GraphQuery gquery = null;
	
	public GraphResultNode(int id) {
		super(id);
	}

	public Defquery getGraphQuery() {
		return gquery;
	}

	public void setGraphQuery(GraphQuery query) {
		this.gquery = query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addResult(Index facts, Rete engine, WorkingMemory mem) {
		if (this.watch) {
    		engine.writeMessage("GraphResultNode (" + this.nodeID + ") :: " + 
    				facts.toPPString() + Constants.LINEBREAK);

		}
		Fact[] result = facts.getFacts();
		if (result.length > 0) {
			results.add(result[result.length - 1]);
		}
	}
	
	public GraphResultNode clone(Rete engine, GraphQuery query) {
		GraphResultNode clone = new GraphResultNode(engine.nextNodeId());
		clone.gquery = query;
		query.setQueryResultNode(clone);
		return clone;
	}
}
