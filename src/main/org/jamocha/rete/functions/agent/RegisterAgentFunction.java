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
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class RegisterAgentFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String REGISTER_AGENT = "register-agent";

	public RegisterAgentFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		if (params != null && params.length == 6) {
			AgentEntry entry = new AgentEntry();
			entry.setIPAddress(params[0].getStringValue());
			entry.setHostname(params[1].getStringValue());
			entry.setApplication(params[2].getStringValue());
			entry.setAgentApplicationName(params[3].getStringValue());
			entry.setAgentApplicationVersion(params[4].getStringValue());
			long time = params[5].getLongValue();
			entry.setTimestamp(time);
			AgentRegistry.registerAgent(entry);
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return REGISTER_AGENT;
	}

	public Class<?>[] getParameter() {
		return new Class[]{String[].class};
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(register-agent <ipaddress> <hostname> <application> <agent app name> <agent app version>)";
	}

}
