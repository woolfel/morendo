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

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class PrintAgentsFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PRINT_AGENTS = "pprint-agents";

	public PrintAgentsFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		List<?> agents = AgentRegistry.getAgents();
		Iterator<?> iterator = agents.iterator();
		while (iterator.hasNext()) {
			AgentEntry agent = (AgentEntry)iterator.next();
			String message = "Agent: " + Constants.LINEBREAK +
			"  ip address: " + agent.getIPAddress() + Constants.LINEBREAK +
			"  hostname: " + agent.getHostname() + Constants.LINEBREAK + 
			"  Application: " + agent.getApplication() + Constants.LINEBREAK +
			"  Agent Application Name: " + agent.getAgentApplicationName() + Constants.LINEBREAK +
			"  Agent Application Version: " + agent.getAgentApplicationVersion() + Constants.LINEBREAK +
			"  Timestamp: " + new Date(agent.getTimestamp()) + Constants.LINEBREAK;
			engine.writeMessage(message, "t");
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return PRINT_AGENTS;
	}

	public Class<?>[] getParameter() {
		return new Class[0];
	}

	public int getReturnType() {
		return 0;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(pprint-agents)";
	}

}
