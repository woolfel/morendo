package org.jamocha.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RuleServiceImpl implements RuleService {

	private Logger log = LogFactory.createLogger(RuleServiceImpl.class);
	private long totalResponseTime = 0;
	private long averageResponseTime = 0;
	private long averageRulesFired = 0;
	private long requests = 0;
	private long totalRulesFired = 0;
	private String serviceName = null;
	private List<RuleApplicationImpl> applications = new ArrayList<RuleApplicationImpl>();
	private Map applicationMap = new HashMap();
	private Map engineMap = new HashMap();
	private ServiceConfiguration serviceConfiguration = null;
	private ServiceAdministration administration = null;
	private static ObjectMapper mapper = new ObjectMapper();
	
	public RuleServiceImpl() {
		applications = new ArrayList();
		administration = new ServiceAdministrationImpl(this);
	}

	public ServiceAdministration getServiceAdmin() {
		return this.administration;
	}
	
	public long getAverageResponseTime() {
		return this.averageResponseTime;
	}

	public long getAverageRulesFired() {
		return this.averageRulesFired;
	}

	public String getServiceName() {
		return this.serviceName;
	}

	public long getRequests() {
		return this.requests;
	}

	public long getTotalRulesFired() {
		return this.totalRulesFired;
	}

	public void initialize() {
		log.info("--- Start initializing RuleService ---");
		for (int idx=0; idx < applications.size(); idx++) {
			RuleApplication application = (RuleApplication)this.applications.get(idx);
			String key = application.getName() + "::" + application.getVersion();
			java.util.PriorityQueue queue = new java.util.PriorityQueue();
			this.applicationMap.put(key, application);
			this.engineMap.put(key, queue);
			
			int initialCount = application.getInitialPool();
			for (int c=0; c < initialCount; c++) {
				org.jamocha.rete.Rete engine = new org.jamocha.rete.Rete();
				application.initializeEngine(engine);
				application.setCurrentPoolCount(c + 1);
				queue.add(engine);
			}
		}
		log.info("--- End initializing RuleService ---");
	}

	/**
	 * Reinitialize calls close and then initialize.
	 */
	public void reinitialize() {
		close();
		initialize();
	}
	
	/**
	 * Close method iterates over all the engine instances and calls Rete.close()
	 */
	public void close() {
		log.info("--- Start closing RuleService ---");
		Iterator itr = this.engineMap.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String)itr.next();
			java.util.PriorityQueue queue = (java.util.PriorityQueue)this.engineMap.remove(key);
			// first close all the engine instances.
			Iterator queueItr = queue.iterator();
			while (queueItr.hasNext()) {
				org.jamocha.rete.Rete engine = (org.jamocha.rete.Rete)queueItr.next();
				engine.close();
			}
			queue.clear();
		}
		itr = this.applicationMap.keySet().iterator();
		while (itr.hasNext()) {
			String key = (String)itr.next();
			RuleApplicationImpl app = (RuleApplicationImpl)this.applicationMap.remove(key);
			app.close();
		}
		this.applicationMap.clear();
		log.info("--- End closing RuleService ---");
	}

	public EngineContext getEngine(String applicationName, String version) {
		String key = applicationName + "::" + version;
		java.util.PriorityQueue queue = (java.util.PriorityQueue)this.engineMap.get(key);
		if (queue != null) {
			org.jamocha.rete.Rete engine = (org.jamocha.rete.Rete)queue.remove();
			if (engine != null) {
				EngineContext context = new EngineContextImpl(this, engine, applicationName, version);
				return context;
			} else {
				// there isn't any engine in the pool. Check to see if we've reached the
				// max pool number. If we are below the max, create a new engine instance
				// and return a new EngineContext.
				RuleApplication application = (RuleApplication)this.applicationMap.get(key);
				if (application.getCurrentPoolCount() < application.getMaxPool()) {
					engine = new org.jamocha.rete.Rete();
					application.initializeEngine(engine);
					application.setCurrentPoolCount(application.getCurrentPoolCount() + 1);
					EngineContext context = new EngineContextImpl(this, engine, applicationName, version);
					return context;
				}
				return null;
			}
		} else {
			return null;
		}
	}
	
	public void setAverageResponseTime(long milliseconds) {
		this.averageResponseTime = milliseconds;
	}

	public void setAverageRulesFired(long average) {
		this.averageRulesFired = average;
	}

	public void setServiceName(String name) {
		this.serviceName = name;
	}

	public void setRequests(long requests) {
		this.requests = requests;
	}

	public void setTotalRulesFired(long count) {
		this.totalRulesFired = count;
	}
	
	public List<RuleApplicationImpl> getRuleApplications() {
		return applications;
	}
	
	public void queueEngine(String application, String version, org.jamocha.rete.Rete engine) {
		String key = application + "::" + version;
		java.util.PriorityQueue queue = (java.util.PriorityQueue)this.engineMap.get(key);
		queue.add(engine);
	}
	
	public void updateStatistics(long time, int rulesFired) {
		this.requests++;
		this.totalResponseTime += time;
		this.averageResponseTime = this.totalResponseTime/this.requests;
		this.totalRulesFired += rulesFired;
		this.averageRulesFired = this.totalRulesFired/this.requests;
	}
	
	public static RuleService createInstance(String filepath) {
		FileReader reader;
		try {
			reader = new FileReader(filepath);
			ServiceConfiguration config = mapper.readValue(reader, ServiceConfiguration.class);
			RuleServiceImpl ruleService = new RuleServiceImpl();
			//ruleService.applications = config.getApplications();
			ruleService.setServiceName( config.getServiceName() );
			ruleService.serviceConfiguration = config;
			return ruleService;
		} catch (Exception e) {
			Logger log = LogFactory.createLogger(RuleApplicationImpl.class);
			log.fatal(e);
		}
		return null;
	}
	
	public static void saveConfiguration(String filename, ServiceConfiguration configuration) {
		File output = new File(filename.substring(0, filename.lastIndexOf('/')));
		output.mkdirs();
		FileWriter writer;
		try {
			writer = new FileWriter(filename);
			mapper.writeValue(writer, configuration);
			writer.close();
		} catch (IOException e) {
			Logger log = LogFactory.createLogger(RuleApplicationImpl.class);
			log.fatal(e);
		}
	}
	
	public Map getRuleApplicationMap() {
		return this.applicationMap;
	}
	
	public Map getEngineMap() {
		return this.engineMap;
	}

	public ServiceConfiguration getServiceConfiguration() {
		return serviceConfiguration;
	}

	public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
		this.serviceConfiguration = serviceConfiguration;
	}
}
