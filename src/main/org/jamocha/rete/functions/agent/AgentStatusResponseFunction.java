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

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

/**
 * AgentStatusResponse prints the information from an agent
 * when a ping is sent by Morendo. The function calculates
 * the elapsed time by subtracting the sent time from the
 * current time. It also prints the time it took to send the
 * ping message to the agent. This allows us to see if there
 * are any delays in the network.
 * 
 * The function prints out the message and returns a string
 * representing the information. This is so that we can get
 * that information and display it in a UI.
 * 
 * @author Peter Lin
 *
 */
public class AgentStatusResponseFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String AGENT_STATUS_RESPONSE = "agent-status-response";

	public AgentStatusResponseFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		String response = "";
		if (params != null && params.length >= 2) {
			long sent = params[0].getLongValue();
			long agentTs = params[1].getLongValue();
			long et = System.currentTimeMillis() - sent;
			response += "Ping response time: " + et + Constants.LINEBREAK + "Agent received time: " + (agentTs - sent) + Constants.LINEBREAK;
			if (params.length == 5) {
				response += "IP Address: " + params[2].getStringValue() + Constants.LINEBREAK;
				response += "Hostname: " + params[3].getStringValue() + Constants.LINEBREAK;
				response += "Application: " + params[4].getStringValue() + Constants.LINEBREAK;
			}
			engine.writeMessage(response, "t");
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rval = new DefaultReturnValue(Constants.STRING_TYPE, response);
		ret.addReturnValue(rval);
		return ret;
	}

	public String getName() {
		return AGENT_STATUS_RESPONSE;
	}

	public Class[] getParameter() {
		return new Class[]{long.class, long.class};
	}

	public int getReturnType() {
		return Constants.STRING_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(" + AGENT_STATUS_RESPONSE + " <sent timestamp> <agent timestamp>)";
	}

}
