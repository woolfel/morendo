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
	private static Map<String, AgentEntry> agentRegistry = new HashMap<String, AgentEntry>();
	private static Map<String, Queue<?>> agentSummaries = new HashMap<String, Queue<?>>();
	
	/**
	 * The registry will only add the AgentEntry if it doesn't 
	 * already exist. This means multiple calls will not override
	 * the existing entry.
	 * @param agent
	 */
	public static void registerAgent(AgentEntry agent) {
		if (!agentRegistry.containsKey(agent.getKey())) {
			agentRegistry.put(agent.getKey(), agent);
		}
	}
	
	public static AgentEntry getAgent(String key) {
		return agentRegistry.get(key);
	}
	
	public static List<AgentEntry> getAgents() {
		return new ArrayList<AgentEntry>(agentRegistry.values());
	}
	
	public static AgentEntry removeAgent(AgentEntry agent) {
		return agentRegistry.remove(agent.getKey());
	}
	
	@SuppressWarnings({ "unchecked" })
	public static void addSummary(AgentPerformanceSummary summary) {
		Queue<AgentPerformanceSummary> queue = (Queue<AgentPerformanceSummary>) agentSummaries.get(summary.getKey());
		if (queue == null) {
			queue = new PriorityQueue<AgentPerformanceSummary>(50);
			agentSummaries.put(summary.getKey(), queue);
		}
		if (queue.size() == 50) {
			queue.remove();
		}
		queue.offer(summary);
	}
	
	public Queue<?> getSummary(AgentPerformanceSummary summary) {
		Queue<?> queue = agentSummaries.get(summary.getKey());
		if (queue == null) {
			queue = new PriorityQueue<Object>(50);
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
	public static List<Queue<?>> getPerformanceSummaries() {
		return new ArrayList<Queue<?>>(agentSummaries.values());
	}
}
