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

import java.util.ArrayList;
import java.util.List;

public class AgentEntry {
	private String hostname;
	private String IPAddress;
	private String application;
	private String agentApplicationName;
	private String agentApplicationVersion;
	private long timestamp;
	private List ruleNames = new ArrayList();
	
	public AgentEntry() {
		super();
	}

	public String getKey() {
		return this.IPAddress + "::" + this.hostname +
		"::" + this.application + "::" + this.agentApplicationName +
		"::" + this.agentApplicationVersion;
	}
	
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getIPAddress() {
		return IPAddress;
	}

	public void setIPAddress(String iPAddress) {
		IPAddress = iPAddress;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getAgentApplicationName() {
		return agentApplicationName;
	}

	public void setAgentApplicationName(String agentApplicationName) {
		this.agentApplicationName = agentApplicationName;
	}

	public String getAgentApplicationVersion() {
		return agentApplicationVersion;
	}

	public void setAgentApplicationVersion(String agentApplicationVersion) {
		this.agentApplicationVersion = agentApplicationVersion;
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
	
	public void setTimestamp(long ms) {
		this.timestamp = ms;
	}
	
	public List getRules() {
		return this.ruleNames;
	}
	
	public void removeRule(String name) {
		this.ruleNames.remove(name);
	}
	
	public void addRule(String name) {
		this.ruleNames.add(name);
	}
	
	public void removeAllRules() {
		this.ruleNames.clear();
	}
}
