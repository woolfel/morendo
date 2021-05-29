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

package org.jamocha.service;

import java.util.List;

/**
 * The purpose of the ServiceAdministration interface is to provide
 * a way to administer the RuleService, and RuleApplications. The
 * RuleService interface provides coarse grain method to reload
 * all engines for a given RuleApplication, but it doesn't give
 * fine grain control.
 * 
 * The RuleService reload is there for convienance when you the
 * user just wants to reload all the engine for a given rule
 * application.
 * 
 * @author Peter Lin
 */
public interface ServiceAdministration {
	@SuppressWarnings("rawtypes")
	List getRuleApplications();
	@SuppressWarnings("rawtypes")
	List getEngines(String applicationName, String version);
	RuleApplication getApplication(String applicationName, String version);
	/**
	 * Reload a specific rule application in the service. Concrete
	 * implementations should not reset the statistics when a
	 * single rule application is reinitialized.
	 * @param ruleApplication
	 */
	void reinitialize(String ruleApplication, String version);
	/**
	 * Reload the FunctionPackage for a given application in the service.
	 * @param ruleApplication
	 * @param version
	 * @return
	 */
	boolean reloadFunctionPackage(String ruleApplication, String version);
	/**
	 * Reload the ruleset for a given application in the service.
	 * All engines in the pool will be reloaded.
	 * @param ruleApplication
	 * @param version
	 * @return
	 */
	boolean reloadRuleset(String ruleApplication, String version);
	/**
	 * Reload the initial data for a given application in the service.
	 * @param ruleApplication
	 * @param version
	 * @return
	 */
	boolean reloadInitialData(String ruleApplication, String version);
	/**
	 * Return the number of engines in a given pool
	 * @param ruleApplication
	 * @param version
	 * @return
	 */
	int getEnginePoolCount(String ruleApplication, String version);
	/**
	 * Return the service configuration for the rule service
	 * @return
	 */
	ServiceConfiguration getServiceConfiguration();
}
