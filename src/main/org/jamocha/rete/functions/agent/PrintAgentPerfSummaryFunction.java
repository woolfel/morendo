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

import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.functions.math.Log;

public class PrintAgentPerfSummaryFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PRINT_AGENT_PERF = "pprint-agent-perf-summary";

	public PrintAgentPerfSummaryFunction() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		List summaries = AgentRegistry.getPerformanceSummaries();
		Iterator iterator = summaries.iterator();
		while (iterator.hasNext()) {
			Queue queue = (Queue)iterator.next();
			Iterator perfItr = queue.iterator();
			String text = null;
			int x = 0;
			while (perfItr.hasNext()) {
				AgentPerformanceSummary sum = (AgentPerformanceSummary)perfItr.next();
				text = "";
				if (x == 0) {
					text += "IP Address: " + sum.getIPAddress();
					text += " Hostname: " + sum.getHostname();
					text += " Application: " + sum.getApplication();
					text += Constants.LINEBREAK;
				}
				text += "    Ave Response: " + sum.getAverageResponseTime();
				text += "    Ave Fired: " + sum.getAverageRulesFired();
				text += "    Total Requests: " + sum.getRequests();
				text += "    Total Rules Fired: " + sum.getTotalRulesFired();
				text += Constants.LINEBREAK;
			}
			engine.writeMessage(text);  // TODO verify this is the correct action
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {  
		return PRINT_AGENT_PERF;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[0];
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(" + PRINT_AGENT_PERF + ")";
	}

}
