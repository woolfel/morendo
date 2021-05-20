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
import java.util.List;

import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class AgentFunctions implements FunctionGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	private List funcs = new ArrayList();

	public AgentFunctions() {
		super();
	}

	public String getName() {
		return AgentFunctions.class.getSimpleName();
	}

	@SuppressWarnings("rawtypes")
	public List listFunctions() {
		return funcs;
	}

	@SuppressWarnings("unchecked")
	public void loadFunctions(Rete engine) {
		AddRuleStatusFunction addstatus = new AddRuleStatusFunction();
		engine.declareFunction(addstatus);
		funcs.add(addstatus);
		AgentPerfSummaryFunction perfsum = new AgentPerfSummaryFunction();
		engine.declareFunction(perfsum);
		funcs.add(perfsum);
		AgentStatusResponseFunction statResp = new AgentStatusResponseFunction();
		engine.declareFunction(statResp);
		funcs.add(statResp);
		RegisterAgentFunction register = new RegisterAgentFunction();
		engine.declareFunction(register);
		funcs.add(register);
		RemoveRuleStatusFunction removestatus = new RemoveRuleStatusFunction();
		engine.declareFunction(removestatus);
		funcs.add(removestatus);
		PingAgentFunction ping = new PingAgentFunction();
		engine.declareFunction(ping);
		funcs.add(ping);
		PrintAgentsFunction printagn = new PrintAgentsFunction();
		engine.declareFunction(printagn);
		funcs.add(printagn);
		UnregisterAgentFunction unreg = new UnregisterAgentFunction();
		engine.declareFunction(unreg);
		funcs.add(unreg);
	}

}
