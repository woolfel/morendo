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

import org.jamocha.messaging.MessageClient;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

/**
 * PingAgent is similar to DOS/Unix ping command. The function
 * can ping one agent, or all active agents. The message to the
 * agents have a timestamp. The return message contains two
 * timestamps: sent timestamp and received timestamp.
 * 
 * The return message from the agent calls agent-status-response
 * function, which prints out the information.
 * 
 * @author Peter Lin
 *
 */
public class PingAgentFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PING_AGENT = "ping-agent";

	public PingAgentFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		if (params != null && params.length == 4) {
			Object value = engine.getDefglobalValue(params[0].getStringValue());
			if (value != null && value instanceof MessageClient) {
				MessageClient client = (MessageClient)value;
				String ipaddr = params[1].getStringValue();
				String hostname = params[2].getStringValue();
				String app = params[3].getStringValue();
				if (!ipaddr.equals("all") && !hostname.equals("all")) {
					String message = "(get-agent-status \"" + ipaddr + "\" \"" + hostname + "\" \"" + 
					app + "\" " + System.currentTimeMillis() + ")";
					client.publish(message);
				} else {
					String message = "(get-agent-status \"all\" \"all\" \"all\" " + System.currentTimeMillis() + ")";
					client.publish(message);
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return PING_AGENT;
	}

	public Class[] getParameter() {
		return new Class[]{String.class};
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(" + PING_AGENT + " <*client instance name*> <ipaddress> <hostname> <application name>)";
	}

}
