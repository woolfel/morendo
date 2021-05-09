package org.jamocha.service;

import java.util.List;

/**
 * RuleService defines common statistics and methods for starting,
 * reloading and stopping a rule service.
 * 
 * @author Peter Lin
 *
 */
public interface RuleService {
	/**
	 * Get a handle to the Service administration for the service.
	 * @return
	 */
	ServiceAdministration getServiceAdmin();
	/**
	 * The concrete class needs to implement the necessary logic
	 * to initialize the rule service. That means starting up
	 * the Rule Applications defined in the deployment descriptor.
	 */
	void initialize();

	/**
	 * reinitialize should reload the rule service and refresh it.
	 * The assumption is the entire service needs to be reloaded.
	 * Concrete implementations should reset all the statistics
	 * when the service is reinitialized.
	 */
	void reinitialize();
	
	/**
	 * clean everything up and close the rule service.
	 */
	void close();
	
	/**
	 * Method returns an instance of a rule with a specific
	 * rule application name.
	 * @param applicationName
	 * @return
	 */
	EngineContext getEngine(String applicationName, String version);
	
	void setServiceName(String name);
	String getServiceName();
	
	/**
	 * The average response time for the service to reason over
	 * some data with the ruleset
	 * @param milliseconds
	 */
	long getAverageResponseTime();

	/**
	 * The average number of rules fired per request
	 * @param average
	 */
	long getAverageRulesFired();

	/**
	 * The total number of requests processed across all
	 * rule engine instances in the pool
	 * @param requests
	 */
	long getRequests();

	/**
	 * the total number of rules fired across all rule
	 * engine instances in the pool
	 * @param count
	 */
	long getTotalRulesFired();
	
	/**
	 * Returns a list of RuleApplication instances, which contains the information
	 * about a specific application. A rule service can have multiple rule applications
	 * deployed.
	 * @return
	 */
	List getRuleApplications();
}
