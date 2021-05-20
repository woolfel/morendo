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

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.rete.Function;
import org.jamocha.rete.Rete;
import org.jamocha.service.ClipsInitialData;
import org.jamocha.service.InitialData;
import org.jamocha.service.Model;
import org.jamocha.service.ObjectData;
import org.jamocha.service.ObjectModel;
import org.jamocha.service.RuleApplication;
import org.jamocha.service.Ruleset;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The default implementation of RuleApplication interface. It does not
 * manage or maintain engine pools. Instead, it just defines the configuration
 * properties a rule application may have.
 * 
 * @author Peter Lin
 */
public class RuleServiceApplication implements RuleApplication {

	private transient Logger log = LogFactory.createLogger(RuleServiceApplication.class);
	private String applicationName = null;
	private String version = null;
	@SuppressWarnings("rawtypes")
	private List models = null;
	private List<ObjectData> objectData = null;
	private List<ClipsInitialData> clipsData = null;
	@SuppressWarnings("rawtypes")
	private List functionGroups = null;
	@SuppressWarnings("rawtypes")
	private List rulesets = new ArrayList();
	/**
	 * FunctionGroup just lists the names, we keep the
	 * instances in a list to make it easier to reload.
	 */
	@SuppressWarnings("rawtypes")
	private List functionInstances = new ArrayList();
	
	private int minPool;
	private int maxPool;
	private int initialPool;
	private int currentPoolCount;
	private URLClassLoader classloader = null;
	private static ObjectMapper mapper = new ObjectMapper();
	
	public RuleServiceApplication() {
		super();
	}

	public void close() {
		this.models.clear();
		this.functionGroups.clear();
		this.functionInstances.clear();
		this.rulesets.clear();
		this.objectData.clear();
		this.clipsData.clear();
		this.classloader = null;
	}
	
	/**
	 * Creates a new URLClassLoader using the URL's from the models, and functions.
	 * The initial data depends on the model definitions and should not use any
	 * data that isn't declared in the models.
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected URLClassLoader createURLClassLoader() {
		ArrayList urls = new ArrayList();
		if (this.models != null) {
			for (int idx=0; idx < models.size(); idx++) {
				Model m = (Model) models.get(idx);
				if (m instanceof ObjectModel) {
					((ObjectModel)m).setRuleApplication(this);
				}
				if (m.getURLObject() != null) {
					urls.add(m.getURLObject());
				}
			}
		}
		if (this.functionGroups != null) {
			for (int idx=0; idx < functionGroups.size(); idx++) {
				org.jamocha.service.FunctionPackage functionGroup = (org.jamocha.service.FunctionPackage)functionGroups.get(idx);
				if (functionGroup.getURLObject() != null) {
					urls.add(functionGroup.getURLObject());
				}
			}
		}
		URL[] urllist = new URL[urls.size()];
		urllist = (URL[])urls.toArray(urllist);
		return URLClassLoader.newInstance(urllist, RuleServiceApplication.class.getClassLoader());
	}
	
	public ClassLoader getClassLoaders() {
		return this.classloader;
	}
	
	@SuppressWarnings("rawtypes")
	public Class findClass(String className) {
		try {
			return this.classloader.loadClass(className);
		} catch (ClassNotFoundException e) {
			log.debug(e);
			return null;
		}
	}
	
	/**
	 * Method will initialize the engine in the following order.
	 * 1. the models
	 * 2. the function groups
	 * 3. rulesets
	 * 4. initial data
	 */
	public boolean initializeEngine(Rete engine) {
		this.classloader = createURLClassLoader();
		if (this.classloader != null) {
			boolean init = true;
			init = this.loadModels(engine);
			if (init) {
				init = this.loadFunctionGroups(engine);
			}
			if (init) {
				init = this.loadRulesets(engine);
			}
			if (init) {
				init = this.loadInitialData(engine);
			}
			return init;
		}
		return false;
	}

	/**
	 * The current implementation iterates over the list of models and
	 * calls Model.loadModel(Rete).
	 */
	public boolean loadModels(Rete engine) {
		boolean success = true;
		for (int idx=0; idx < this.models.size(); idx++) {
			Model m = (Model)models.get(idx);
			m.loadModel(engine);
		}
		return success;
	}

	/**
	 * The current implementation allows Function and FunctionGroup. This means
	 * users can group a variety of functions or groups into a logical group.
	 * Within the rule engine, functions will be added to the main group.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean loadFunctionGroups(Rete engine) {
		if (this.functionInstances == null) {
			this.functionInstances = new ArrayList();
		}
		boolean success = true;
		for (int idx=0; idx < this.functionGroups.size(); idx++) {
			org.jamocha.service.FunctionPackage functionGroup = (org.jamocha.service.FunctionPackage)this.functionGroups.get(idx);
			String[] classnames = functionGroup.getClassNames();
			for (int fx=0; fx < classnames.length; fx++) {
				String classname = classnames[fx];
				Class clzz;
				try {
					clzz = classloader.loadClass(classname);
					if (clzz != null) {
						Object data = clzz.getDeclaredConstructor().newInstance();
						if (data instanceof org.jamocha.rete.Function) {
							Function func = (Function)data;
							engine.declareFunction(func);
						} else if (data instanceof org.jamocha.rete.FunctionGroup) {
							org.jamocha.rete.FunctionGroup fGroup = (org.jamocha.rete.FunctionGroup)data;
							fGroup.loadFunctions(engine);
						}
						functionInstances.add(data);
					}
				} catch (ClassNotFoundException e) {
					log.fatal(e);
					success = false;
					break;
				} catch (InstantiationException e) {
					log.fatal(e);
					success = false;
					break;
				} catch (IllegalAccessException e) {
					log.fatal(e);
					success = false;
					break;
				} catch (IllegalArgumentException e) {
					log.fatal(e);
					success = false;
					break;
				} catch (InvocationTargetException e) {
					log.fatal(e);
					success = false;
					break;
				} catch (NoSuchMethodException e) {
					log.fatal(e);
					success = false;
					break;
				} catch (SecurityException e) {
					log.fatal(e);
					success = false;
					break;
				}
			}
		}
		return success;
	}

	public boolean loadRulesets(Rete engine) {
		boolean success = true;
		for (int idx=0; idx < this.rulesets.size(); idx++) {
			Ruleset ruleset = (Ruleset)this.rulesets.get(idx);
			success = ruleset.loadRuleset(engine);
			if (!success) {
				break;
			}
		}
		return success;
	}

	public boolean loadInitialData(Rete engine) {
		boolean success = true;
		if (this.objectData != null) {
			for (int idx=0; idx < this.objectData.size(); idx++) {
				InitialData initialData = (InitialData)this.objectData.get(idx);
				initialData.loadData(engine);
			}
		}
		return success;
	}

	/**
	 * Reinitialize calls close first to clear the application. Next it
	 * reloads the jar files in the classloader and continues.
	 */
	public boolean reinitializeEngine(Rete engine) {
		this.close();
		if (this.classloader != null) {
			boolean init = true;
			init = this.loadModels(engine);
			if (init) {
				init = this.loadFunctionGroups(engine);
			}
			if (init) {
				init = this.loadRulesets(engine);
			}
			if (init) {
				init = this.loadInitialData(engine);
			}
			return init;
		}
		return false;
	}

	/**
	 * 
	 */
	public boolean reloadFunctionGroups(Rete engine) {
		for (int idx=0; idx < this.functionInstances.size(); idx++) {
			Object data = this.functionInstances.get(idx);
			if (data instanceof org.jamocha.rete.Function) {
				engine.removeFunction((Function)data);
			} else if (data instanceof org.jamocha.rete.FunctionGroup) {
				engine.removeFunctionGroup((org.jamocha.rete.FunctionGroup)data);
			}
		}
		return this.loadFunctionGroups(engine);
	}

	public boolean reloadRulesets(Rete engine) {
		boolean reload = false;
		try {
			for (int idx=0; idx < this.rulesets.size(); idx++) {
				Ruleset ruleset = (Ruleset)this.rulesets.get(idx);
				reload = ruleset.reloadRuleset(engine);
				if (!reload) {
					break;
				}
			}
			reload = true;
		} catch (Exception e) {
			return false;
		}
		return reload;
	}

	public boolean reloadInitialData(Rete engine) {
		boolean reload = false;
		for (int idx=0; idx < this.objectData.size(); idx++) {
			InitialData initialData = (InitialData)this.objectData.get(idx);
			reload = initialData.reloadData(engine);
			if (!reload) {
				break;
			}
		}
		return reload;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getFunctionGroups() {
		return this.functionGroups;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getObjectData() {
		return this.objectData;
	}

	public int getInitialPool() {
		return initialPool;
	}

	public int getMaxPool() {
		return maxPool;
	}

	public int getMinPool() {
		return minPool;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getModels() {
		return this.models;
	}

	public String getName() {
		return this.applicationName;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getRulesets() {
		return this.rulesets;
	}

	public String getVersion() {
		return this.version;
	}
	
	@SuppressWarnings("rawtypes")
	public void setFunctionGroups(List functionGroups) {
		this.functionGroups = functionGroups;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setObjectData(List data) {
		this.objectData = data;
	}

	public List<ClipsInitialData> getClipsData() {
		return clipsData;
	}

	public void setClipsData(List<ClipsInitialData> clipsData) {
		this.clipsData = clipsData;
	}

	public void setInitialPool(int initial) {
		this.initialPool = initial;
	}

	public void setMaxPool(int max) {
		this.maxPool = max;
	}

	public void setMinPool(int min) {
		this.minPool = min;
	}

	@SuppressWarnings("rawtypes")
	public void setModels(List models) {
		this.models = models;
	}

	public void setName(String name) {
		this.applicationName = name;
	}

	@SuppressWarnings("rawtypes")
	public void setRulesets(List rulesets) {
		this.rulesets = rulesets;
	}

	public void setVersion(String value) {
		this.version = value;
	}
	
	public static void saveConfiguration(String filename, RuleApplication app) {
		FileWriter writer;
		try {
			writer = new FileWriter(filename);
			mapper.writeValue(writer, app);
			writer.close();
		} catch (IOException e) {
			Logger log = LogFactory.createLogger(RuleServiceApplication.class);
			log.fatal(e);
		}
	}
	
	public static RuleApplication loadConfiguration(String url) {
		FileReader reader;
		try {
			reader = new FileReader(url);
			RuleApplication app = mapper.readValue(reader, RuleApplication.class);
			return app;
		} catch (Exception e) {
			Logger log = LogFactory.createLogger(RuleServiceApplication.class);
			log.fatal(e);
		}
		return null;
	}

	public int getCurrentPoolCount() {
		return currentPoolCount;
	}

	public void setCurrentPoolCount(int currentPoolCount) {
		this.currentPoolCount = currentPoolCount;
	}
}
