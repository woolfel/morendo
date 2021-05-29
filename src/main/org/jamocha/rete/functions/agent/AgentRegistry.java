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
package org.jamocha.rete.functions.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * For now the AgentRegistry is a static Map of the agents. The key
 * is defined by AgentEntry and AgentPerformanceSummary classes.
 * Since AgentEntry is specific to the AgentApplications, a single
 * webapp could have multiple entries.
 * In contrast, the performance summary is at the Agent AdminService
 * level, which means per webapp or ejb, there's only 1 instance.
 * 
 * @author Peter Lin
 */
public class AgentRegistry {
	@SuppressWarnings("rawtypes")
	private static Map agentRegistry = new HashMap();
	@SuppressWarnings("rawtypes")
	private static Map agentSummaries = new HashMap();
	
	/**
	 * The registry will only add the AgentEntry if it doesn't 
	 * already exist. This means multiple calls will not override
	 * the existing entry.
	 * @param agent
	 */
	@SuppressWarnings("unchecked")
	public static void registerAgent(AgentEntry agent) {
		if (!agentRegistry.containsKey(agent.getKey())) {
			agentRegistry.put(agent.getKey(), agent);
		}
	}
	
	public static AgentEntry getAgent(String key) {
		return (AgentEntry)agentRegistry.get(key);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getAgents() {
		return new ArrayList(agentRegistry.values());
	}
	
	public static AgentEntry removeAgent(AgentEntry agent) {
		return (AgentEntry)agentRegistry.remove(agent.getKey());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addSummary(AgentPerformanceSummary summary) {
		Queue queue = (Queue)agentSummaries.get(summary.getKey());
		if (queue == null) {
			queue = new PriorityQueue(50);
			agentSummaries.put(summary.getKey(), queue);
		}
		if (queue.size() == 50) {
			queue.remove();
		}
		queue.offer(summary);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Queue getSummary(AgentPerformanceSummary summary) {
		Queue queue = (Queue)agentSummaries.get(summary.getKey());
		if (queue == null) {
			queue = new PriorityQueue(50);
			agentSummaries.put(summary.getKey(), queue);
		}
		return queue;
	}
	
	/**
	 * Return a list of all the queues. Each queue has the performance summary
	 * for a given application deployment. It is at the level of the agent
	 * administration service.
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getPerformanceSummaries() {
		return new ArrayList(agentSummaries.values());
	}
}
