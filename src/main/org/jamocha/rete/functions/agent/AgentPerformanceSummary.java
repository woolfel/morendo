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

import java.io.Serializable;

public class AgentPerformanceSummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ipaddress = null;
	private String hostname = null;
	private String application = null;
	private long averageRulesFired = 0;
	private long averageResponseTime = 0;
	private long requests = 0;
	private long totalRulesFired = 0;
	private long timestamp = 0;

	public AgentPerformanceSummary() {
		super();
	}

	public String getKey() {
		return this.ipaddress + "::" + this.hostname + "::" + this.application;
	}
	
	public String getIPAddress() {
		return ipaddress;
	}


	public void setIPAddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}


	public String getHostname() {
		return hostname;
	}


	public void setHostname(String hostname) {
		this.hostname = hostname;
	}


	public String getApplication() {
		return application;
	}


	public void setApplication(String application) {
		this.application = application;
	}


	public long getAverageRulesFired() {
		return averageRulesFired;
	}

	public void setAverageRulesFired(long averageRulesFired) {
		this.averageRulesFired = averageRulesFired;
	}

	public long getAverageResponseTime() {
		return averageResponseTime;
	}

	public void setAverageResponseTime(long averageResponseTime) {
		this.averageResponseTime = averageResponseTime;
	}

	public long getRequests() {
		return requests;
	}

	public void setRequests(long requests) {
		this.requests = requests;
	}

	public long getTotalRulesFired() {
		return totalRulesFired;
	}

	public void setTotalRulesFired(long totalRulesFired) {
		this.totalRulesFired = totalRulesFired;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
