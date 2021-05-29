/*
 * Copyright 2002-2010 Peter Lin
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

package org.jamocha.service.servlet;

// import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jamocha.logging.ServletLogger;
import org.jamocha.rete.Rete;
import org.jamocha.service.EngineContext;
import org.jamocha.service.RuleApplication;
import org.jamocha.service.RuleApplicationBean;
import org.jamocha.service.RuleApplicationImpl;
import org.jamocha.service.RuleService;
import org.jamocha.service.ServiceAdministration;
import org.jamocha.service.ServiceConfiguration;

// import com.fasterxml.jackson.core.JsonParseException;
// import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RuleStartupService implements ServletContextListener, RuleService {

	public static final String CONFIGURATION_FILE = "Ruleconfig.json";
	private long totalResponseTime = 0;
	private long averageResponseTime = 0;
	private long averageRulesFired = 0;
	private long requests = 0;
	private long totalRulesFired = 0;
	private String serviceName = null;
	private List<RuleApplicationImpl> applications = new ArrayList<RuleApplicationImpl>();
	private Map<String, RuleApplication> applicationMap = new HashMap<String, RuleApplication>();
	private Map<String, List<Rete>> engineMap = new HashMap<String, List<Rete>>();
	protected ServiceConfiguration serviceConfiguration = null;
	private ServletServiceAdmin administration = null;
	protected ServletContext servletContext = null;
	private static ObjectMapper mapper = new ObjectMapper();
	
	public RuleStartupService() {
		applications = new ArrayList<RuleApplicationImpl>();
		administration = new ServletServiceAdmin(this);
	}

	public void contextDestroyed(ServletContextEvent context) {
		this.close();
	}

	public void contextInitialized(ServletContextEvent context) {
		this.servletContext = context.getServletContext();
		org.jamocha.logging.LogFactory.setServletContext(this.servletContext);
		administration.setServletContext(this.servletContext);
		this.serviceConfiguration = this.loadConfiguration();
		servletContext.log("--- configuration loaded from Ruleconfig.json ---");
		this.readRuleApps();
		this.serviceName = this.serviceConfiguration.getServiceName();
		this.initialize();
		context.getServletContext().setAttribute(this.serviceName, this);
		servletContext.log("--- RuleService has been initialized ---");
	}
	
	private void readRuleApps() {
		for (RuleApplicationBean rab: this.serviceConfiguration.getApplications()) {
			RuleApplicationImpl rai = new RuleApplicationImpl();
			rai.readBean(rab);
			this.applications.add(rai);
		}
	}

	public void close() {
		servletContext.log("--- Start closing RuleService ---");
		Iterator<String> itr = this.engineMap.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			List<?> queue = this.engineMap.remove(key);
			// first close all the engine instances.
			Iterator<?> queueItr = queue.iterator();
			while (queueItr.hasNext()) {
				org.jamocha.rete.Rete engine = (org.jamocha.rete.Rete)queueItr.next();
				engine.close();
			}
			queue.clear();
		}
		itr = this.applicationMap.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			RuleApplication app = this.applicationMap.remove(key);
			app.close();
		}
		this.applicationMap.clear();
		servletContext.log("--- End closing RuleService ---");
	}

	public long getAverageResponseTime() {
		return averageResponseTime;
	}

	public long getAverageRulesFired() {
		return averageRulesFired;
	}

	public EngineContext getEngine(String applicationName, String version) {
		String key = applicationName + "::" + version;
		List<?> queue = this.engineMap.get(key);
		if (queue != null) {
			org.jamocha.rete.Rete engine = null;
			if (queue.size() > 0 && (engine = (org.jamocha.rete.Rete)queue.remove(0))!= null) {
				EngineContext context = new ServletEngineContext(this, engine, applicationName, version, this.servletContext);
				return context;
			} else {
				// there isn't any engine in the pool. Check to see if we've reached the
				// max pool number. If we are below the max, create a new engine instance
				// and return a new EngineContext.
				RuleApplication application = this.applicationMap.get(key);
				if (application.getCurrentPoolCount() < application.getMaxPool()) {
					engine = new org.jamocha.rete.Rete();
					application.initializeEngine(engine);
					application.setCurrentPoolCount(application.getCurrentPoolCount() + 1);
					EngineContext context = new ServletEngineContext(this, engine, applicationName, version, this.servletContext);
					this.servletContext.log("New engine instance created. Current engine pool count is " + application.getCurrentPoolCount());
					return context;
				} else {
					this.servletContext.log("The Rule service has reached the maximum pool number. Try increasing the configuration.");
				}
				return null;
			}
		} else {
			return null;
		}
	}

	public long getRequests() {
		return requests;
	}

	public List<RuleApplicationImpl> getRuleApplications() {
		return applications;
	}

	public ServiceAdministration getServiceAdmin() {
		return administration;
	}

	public String getServiceName() {
		return serviceName;
	}

	public long getTotalRulesFired() {
		return totalRulesFired;
	}

	public void initialize() {
		this.servletContext.log("--- Start initializing RuleService ---");
		for (int idx=0; idx < applications.size(); idx++) {
			RuleApplication application = this.applications.get(idx);
			((RuleApplicationImpl)application).setServletContext(this.servletContext);
			String key = application.getName() + "::" + application.getVersion();
			List<Rete> queue = new ArrayList<Rete>();
			this.applicationMap.put(key, application);
			this.engineMap.put(key, queue);
			int initialCount = application.getInitialPool();
			for (int c=0; c < initialCount; c++) {
				org.jamocha.rete.Rete engine = new org.jamocha.rete.Rete(new ServletLogger(this.servletContext));
				engine.setWatch(Rete.WATCH_ALL);
				application.initializeEngine(engine);
				application.setCurrentPoolCount(c + 1);
				engine.setUnWatch(Rete.WATCH_ALL);
				queue.add(engine);
			}
		}
		this.servletContext.log("--- End initializing RuleService ---");
	}

	public void reinitialize() {
		close();
		initialize();
	}

	public void setServiceName(String name) {
		this.serviceName = name;
	}

	public void updateStatistics(long time, int rulesFired) {
		this.requests++;
		this.totalResponseTime += time;
		this.averageResponseTime = this.totalResponseTime/this.requests;
		this.totalRulesFired += rulesFired;
		this.averageRulesFired = this.totalRulesFired/this.requests;
	}

	public void queueEngine(String application, String version, org.jamocha.rete.Rete engine) {
		String key = application + "::" + version;
		List<Rete> queue = this.engineMap.get(key);
		queue.add(engine);
	}

	public Map<String, RuleApplication> getRuleApplicationMap() {
		return this.applicationMap;
	}
	
	public Map<String, List<Rete>> getEngineMap() {
		return this.engineMap;
	}
	
	public ServiceConfiguration getServiceConfiguration() {
		return serviceConfiguration;
	}
	
	public void setServiceConfiguration(ServiceConfiguration config) {
		this.serviceConfiguration = config;
	}
	
	protected ServiceConfiguration loadConfiguration() {
		if (this.serviceConfiguration == null) {
			String path = "/WEB-INF/" + CONFIGURATION_FILE;
			this.servletContext.log("Path: " + path);
			InputStream input = this.servletContext.getResourceAsStream(path);
			ServiceConfiguration config;
			try {
				config = mapper.readValue(input, ServiceConfiguration.class);
				this.serviceConfiguration = config;
			} catch (Exception e) {
				this.servletContext.log(" -- loadConfiguration -- " + e.getMessage());
			}
			return this.serviceConfiguration;
		} else {
			return this.serviceConfiguration;
		}
	}
}
