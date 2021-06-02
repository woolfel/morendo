/*
 * Copyright 2002-2009 Jamocha
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

public class AgentPerfSummaryFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String AGENT_PERF_SUMMARY = "agent-perf-summary";

	public AgentPerfSummaryFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		if (params != null && params.length == 8) {
			String ipaddr = params[0].getStringValue();
			String hostname = params[1].getStringValue();
			String app = params[2].getStringValue();
			String aveRespTxt = params[3].getStringValue();
			String aveRulesTxt = params[4].getStringValue();
			String reqTxt = params[5].getStringValue();
			String totalRulesTxt = params[6].getStringValue();
			String tstampTxt = params[7].getStringValue();
			
			AgentPerformanceSummary summary = new AgentPerformanceSummary();
			summary.setIPAddress(ipaddr);
			summary.setHostname(hostname);
			summary.setApplication(app);
			long aveResp = Long.parseLong(aveRespTxt.substring(aveRespTxt.indexOf("=") + 1));
			summary.setAverageResponseTime(aveResp);
			long aveRules = Long.parseLong(aveRulesTxt.substring(aveRulesTxt.indexOf("=") + 1));
			summary.setAverageRulesFired(aveRules);
			long requests = Long.parseLong(reqTxt.substring(reqTxt.indexOf("=") + 1));
			summary.setRequests(requests);
			long totalRules = Long.parseLong(totalRulesTxt.substring(totalRulesTxt.indexOf("=") + 1));
			summary.setTotalRulesFired(totalRules);
			long time = Long.parseLong(tstampTxt.substring(tstampTxt.indexOf("=") + 1));
			summary.setTimestamp(time);
			
			AgentRegistry.addSummary(summary);
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return AGENT_PERF_SUMMARY;
	}

	public Class<?>[] getParameter() {
		return new Class[]{String[].class};
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(agent-perf-summary <ipaddress> <hostname> <application> <average_response_time> " + 
		"<average_rules_fired> <requests> <total_rules_fired> <timestamp>)";
	}

}
