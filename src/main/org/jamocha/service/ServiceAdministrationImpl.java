package org.jamocha.service;

import java.util.Iterator;
import java.util.List;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.rete.Rete;

public class ServiceAdministrationImpl implements ServiceAdministration {

	private Logger log = LogFactory.createLogger(ServiceAdministrationImpl.class);
	private RuleServiceImpl ruleService = null;
	
	public ServiceAdministrationImpl(RuleServiceImpl service) {
		this.ruleService = service;
	}

	public RuleApplication getApplication(String applicationName, String version) {
		String key = applicationName + "::" + version;
		return (RuleApplication)this.ruleService.getRuleApplicationMap().get(key);
	}

	public int getEnginePoolCount(String ruleApplication, String version) {
		String key = ruleApplication + "::" + version;
		java.util.PriorityQueue<?> queue = (java.util.PriorityQueue<?>)this.ruleService.getEngineMap().get(key);
		return queue.size();
	}

	public List<?> getEngines(String ruleApplication, String version) {
		String key = ruleApplication + "::" + version;
		java.util.PriorityQueue<?> queue = (java.util.PriorityQueue<?>)this.ruleService.getEngineMap().get(key);
		return new java.util.ArrayList<Object>(queue);
	}

	public List<?> getRuleApplications() {
		return this.ruleService.getRuleApplications();
	}

	public ServiceConfiguration getServiceConfiguration() {
		return ruleService.getServiceConfiguration();
	}

	public void reinitialize(String ruleApplication, String version) {
		log.info("--- Start reinitializing rule application: " + ruleApplication + " " + version);
		String key = ruleApplication + "::" + version;
		java.util.PriorityQueue<Rete> queue = (java.util.PriorityQueue<Rete>)this.ruleService.getEngineMap().remove(key);
		// first close all the engine instances.
		Iterator<Rete> itr = queue.iterator();
		while (itr.hasNext()) {
			org.jamocha.rete.Rete engine = itr.next();
			engine.close();
		}
		queue.clear();
		
		// Now reload the RuleApplication and recreate the engine instances
		RuleApplicationImpl app = (RuleApplicationImpl)this.ruleService.getRuleApplicationMap().get(key);
		queue = new java.util.PriorityQueue<Rete>();
		this.ruleService.getEngineMap().put(ruleApplication, queue);
		for (int idx=0; idx < app.getInitialPool(); idx++) {
			org.jamocha.rete.Rete engine = new org.jamocha.rete.Rete();
			queue.add(engine);
			app.reinitializeEngine(engine);
		}
		log.info("--- Finished reinitializing rule application: " + ruleApplication + " " + version);
	}
	

	public boolean reloadFunctionPackage(String ruleApplication, String version) {
		log.info("--- Start reloading Function Package: " + ruleApplication + " " + version);
		boolean reload = false;
		String key = ruleApplication + "::" + version;
		RuleApplicationImpl app = (RuleApplicationImpl)this.ruleService.getRuleApplicationMap().get(key);
		java.util.PriorityQueue<?> queue = (java.util.PriorityQueue<?>)this.ruleService.getEngineMap().remove(key);
		Iterator<?> iterator = queue.iterator();
		while (iterator.hasNext()) {
			org.jamocha.rete.Rete engine = (org.jamocha.rete.Rete)iterator.next();
			reload = app.reloadFunctionGroups(engine);
			if (!reload) {
				break;
			}
		}
		log.info("--- Finished reloading Function Package: " + ruleApplication + " " + version);
		return reload;
	}

	public boolean reloadInitialData(String ruleApplication, String version) {
		log.info("--- Start reloading Initial Data: " + ruleApplication + " " + version);
		boolean reload = false;
		String key = ruleApplication + "::" + version;
		RuleApplicationImpl app = (RuleApplicationImpl)this.ruleService.getRuleApplicationMap().get(key);
		java.util.PriorityQueue<?> queue = (java.util.PriorityQueue<?>)this.ruleService.getEngineMap().remove(key);
		Iterator<?> iterator = queue.iterator();
		while (iterator.hasNext()) {
			org.jamocha.rete.Rete engine = (org.jamocha.rete.Rete)iterator.next();
			reload = app.reloadInitialData(engine);
			if (!reload) {
				break;
			}
		}
		log.info("--- Finished reloading Initial Data: " + ruleApplication + " " + version);
		return reload;
	}

	public boolean reloadRuleset(String ruleApplication, String version) {
		log.info("--- Start reloading Ruleset: " + ruleApplication + " " + version);
		boolean reload = false;
		String key = ruleApplication + "::" + version;
		RuleApplicationImpl app = (RuleApplicationImpl)this.ruleService.getRuleApplicationMap().get(key);
		java.util.PriorityQueue<?> queue = (java.util.PriorityQueue<?>)this.ruleService.getEngineMap().remove(key);
		Iterator<?> iterator = queue.iterator();
		while (iterator.hasNext()) {
			org.jamocha.rete.Rete engine = (org.jamocha.rete.Rete)iterator.next();
			reload = app.reloadRulesets(engine);
			if (!reload) {
				break;
			}
		}
		log.info("--- Finished reloading Ruleset: " + ruleApplication + " " + version);
		return reload;
	}

}
