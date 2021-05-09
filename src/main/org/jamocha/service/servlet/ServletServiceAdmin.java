package org.jamocha.service.servlet;

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.jamocha.service.RuleApplication;
import org.jamocha.service.ServiceAdministration;
import org.jamocha.service.ServiceConfiguration;

public class ServletServiceAdmin implements ServiceAdministration {

	private RuleStartupService ruleService = null;
	private ServletContext servletContext = null;
	
	public ServletServiceAdmin(RuleStartupService service) {
		this.ruleService = service;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public RuleApplication getApplication(String applicationName, String version) {
		String key = applicationName + "::" + version;
		return (RuleApplication)this.ruleService.getRuleApplicationMap().get(key);	}

	public int getEnginePoolCount(String ruleApplication, String version) {
		String key = ruleApplication + "::" + version;
		java.util.PriorityQueue queue = (java.util.PriorityQueue)this.ruleService.getEngineMap().get(key);
		return queue.size();
	}

	public List getEngines(String applicationName, String version) {
		String key = applicationName + "::" + version;
		java.util.PriorityQueue queue = (java.util.PriorityQueue)this.ruleService.getEngineMap().get(key);
		return new java.util.ArrayList(queue);
	}

	public List getRuleApplications() {
		return this.ruleService.getRuleApplications();
	}

	public ServiceConfiguration getServiceConfiguration() {
		return ruleService.getServiceConfiguration();
	}

	public void reinitialize(String ruleApplication, String version) {
		servletContext.log("--- Start reinitializing rule application: " + ruleApplication + " " + version);
		String key = ruleApplication + "::" + version;
		java.util.PriorityQueue queue = (java.util.PriorityQueue)this.ruleService.getEngineMap().remove(key);
		// first close all the engine instances.
		Iterator itr = queue.iterator();
		while (itr.hasNext()) {
			org.jamocha.rete.Rete engine = (org.jamocha.rete.Rete)itr.next();
			engine.close();
		}
		queue.clear();
		
		// Now reload the RuleApplication and recreate the engine instances
		RuleApplication app = (RuleApplication)this.ruleService.getRuleApplicationMap().get(key);
		queue = new java.util.PriorityQueue();
		this.ruleService.getEngineMap().put(ruleApplication, queue);
		for (int idx=0; idx < app.getInitialPool(); idx++) {
			org.jamocha.rete.Rete engine = new org.jamocha.rete.Rete();
			queue.add(engine);
			app.reinitializeEngine(engine);
		}
		servletContext.log("--- Finished reinitializing rule application: " + ruleApplication + " " + version);
	}

	public boolean reloadFunctionPackage(String ruleApplication, String version) {
		servletContext.log("--- Start reloading Function Package: " + ruleApplication + " " + version);
		boolean reload = false;
		String key = ruleApplication + "::" + version;
		RuleApplication app = (RuleApplication)this.ruleService.getRuleApplicationMap().get(key);
		java.util.PriorityQueue queue = (java.util.PriorityQueue)this.ruleService.getEngineMap().remove(key);
		Iterator iterator = queue.iterator();
		while (iterator.hasNext()) {
			org.jamocha.rete.Rete engine = (org.jamocha.rete.Rete)iterator.next();
			reload = app.reloadFunctionGroups(engine);
			if (!reload) {
				break;
			}
		}
		servletContext.log("--- Finished reloading Function Package: " + ruleApplication + " " + version);
		return reload;
	}

	public boolean reloadInitialData(String ruleApplication, String version) {
		servletContext.log("--- Start reloading Initial Data: " + ruleApplication + " " + version);
		boolean reload = false;
		String key = ruleApplication + "::" + version;
		RuleApplication app = (RuleApplication)this.ruleService.getRuleApplicationMap().get(key);
		java.util.PriorityQueue queue = (java.util.PriorityQueue)this.ruleService.getEngineMap().remove(key);
		Iterator iterator = queue.iterator();
		while (iterator.hasNext()) {
			org.jamocha.rete.Rete engine = (org.jamocha.rete.Rete)iterator.next();
			reload = app.reloadInitialData(engine);
			if (!reload) {
				break;
			}
		}
		servletContext.log("--- Finished reloading Initial Data: " + ruleApplication + " " + version);
		return reload;
	}

	public boolean reloadRuleset(String ruleApplication, String version) {
		servletContext.log("--- Start reloading Ruleset: " + ruleApplication + " " + version);
		boolean reload = false;
		String key = ruleApplication + "::" + version;
		RuleApplication app = (RuleApplication)this.ruleService.getRuleApplicationMap().get(key);
		java.util.PriorityQueue queue = (java.util.PriorityQueue)this.ruleService.getEngineMap().remove(key);
		Iterator iterator = queue.iterator();
		while (iterator.hasNext()) {
			org.jamocha.rete.Rete engine = (org.jamocha.rete.Rete)iterator.next();
			reload = app.reloadRulesets(engine);
			if (!reload) {
				break;
			}
		}
		servletContext.log("--- Finished reloading Ruleset: " + ruleApplication + " " + version);
		return reload;
	}

}
