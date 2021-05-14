package org.jamocha.service;

/**
 * The basic idea is a rule application defines the models, functions,
 * rules and data needed for a fully functional engine instance. The
 * load methods return boolean so that implementing classes can make
 * sure the step completed before proceeding to the next. For example,
 * if the engine fails to load the model, there's no point continuing
 * with the rest of the initialization process.
 * 
 * An important note about RuleApplications. Unlike JSR94, a rule
 * application can be used by itself with the RuleService. In some
 * cases, we don't need to create a pool of engines and collect a
 * bunch of statistics.
 * 
 * For use cases that need a pool of engines and manage them from a
 * central entry point, use the RuleService instead. This approach
 * makes using and managing rule engines more fine grain and consistent.
 * 
 * From a runtime management perspective, long running applications
 * need to be updated without bringing down the server. Some
 * environments it's acceptable to restart the server, but for
 * many it isn't. All components of a rule application depend on
 * the model, so there isn't a method for reloading just the model.
 * Functions, rules and data can be reloaded independently of each
 * other with minimal risk.
 * 
 * RuleApplication extends Configuration, which defines the get/set
 * methods. The method define by the interface cover load,
 * initialization and reload. Each rule application instance
 * should create a ClassLoader. This is necessary for reloading
 * the application.
 * 
 * @author Peter Lin
 */
public interface RuleApplication extends Configuration {
	
	void setVersion(String version);
	String getVersion();
	
	/**
	 * model includes jar files, deftemplates and template
	 * associations
	 * @param engine
	 * @return
	 */
	boolean loadModels(org.jamocha.rete.Rete engine);

	/**
	 * functiongroups include functiongroups and individual
	 * functions
	 * @param engine
	 * @return
	 */
	boolean loadFunctionGroups(org.jamocha.rete.Rete engine);
	/**
	 * 
	 * @param engine
	 * @return
	 */
	boolean reloadFunctionGroups(org.jamocha.rete.Rete engine);
	
	boolean loadRulesets(org.jamocha.rete.Rete engine);
	boolean reloadRulesets(org.jamocha.rete.Rete engine);
	
	// boolean loadInitialData(org.jamocha.rete.Rete engine);
	
	/**
	 * 
	 * @param engine
	 * @return
	 */
	boolean reloadInitialData(org.jamocha.rete.Rete engine);
	
	/**
	 * The engine should be initialized in the following order.
	 * 1. models
	 * 2. functions
	 * 3. rules
	 * 4. initial data
	 * @param engine
	 * @return
	 */
	boolean initializeEngine(org.jamocha.rete.Rete engine);
	
	/**
	 * reinitialize will reload the URL's and refresh the
	 * application with the latest data. To refresh an
	 * engine instance with the data that was loaded at
	 * start time, call the reload method directly.
	 * @param engine
	 * @return
	 */
	boolean reinitializeEngine(org.jamocha.rete.Rete engine);
	
	ClassLoader getClassLoaders();
	Class findClass(String className);
	
	void close();
	
	void setCurrentPoolCount(int count);
	int getCurrentPoolCount();
}
