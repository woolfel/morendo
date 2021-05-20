package org.jamocha.service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.rete.Function;
import org.jamocha.rete.Rete;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The default implementation of RuleApplication interface. It does not
 * manage or maintain engine pools. Instead, it just defines the configuration
 * properties a rule application may have.
 * 
 * @author Peter Lin
 */
public class RuleApplicationImpl implements RuleApplication {

	@JsonIgnore
	private transient Logger log = LogFactory.createLogger(RuleApplicationImpl.class);
	private String applicationName = null;
	private String version = null;
	private List<ObjectModel> models = null;
	private List<ObjectData> objectData = null;
	@SuppressWarnings("rawtypes")
	private List<JSONData> jsonData = null;
	private List<ClipsInitialData> clipsData = null;
	private List<FunctionPackage> functionGroups = null;
	private List<ClipsRuleset> rulesets = new ArrayList<ClipsRuleset>();
	/**
	 * FunctionGroup just lists the names, we keep the
	 * instances in a list to make it easier to reload.
	 */
	@SuppressWarnings("rawtypes")
	@JsonIgnore
	private List functionInstances = new ArrayList();
	
	private int minPool;
	private int maxPool;
	private int initialPool;
	@JsonIgnore
	private int currentPoolCount;
	@JsonIgnore
	private URLClassLoader classloader = null;
	@JsonIgnore
	private static ObjectMapper mapper = new ObjectMapper();
	private ServletContext servletCtx = null;
	
	public RuleApplicationImpl() {
		super();
	}

	public void readBean(RuleApplicationBean bean) {
		this.maxPool = bean.getMaxPool();
		this.minPool = bean.getMinPool();
		this.applicationName = bean.getName();
		this.functionGroups = bean.getFunctionGroups();
		this.initialPool = bean.getInitialPool();
		this.clipsData = bean.getClipsData();
		this.objectData = bean.getObjectData();
		this.jsonData = bean.getJsonData();
		this.models = bean.getModels();
		this.rulesets = bean.getRulesets();
	}
	
	@JsonIgnore
	public void close() {
		this.models.clear();
		this.functionGroups.clear();
		this.functionInstances.clear();
		this.rulesets.clear();
		this.objectData.clear();
		this.clipsData.clear();
		this.classloader = null;
	}
	
	@JsonIgnore
	public void setServletContext(ServletContext ctx) {
		this.servletCtx = ctx;
	}
	
	/**
	 * Creates a new URLClassLoader using the URL's from the models, and functions.
	 * The initial data depends on the model definitions and should not use any
	 * data that isn't declared in the models.
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@JsonIgnore
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
		if (urls.size() > 0) {
			URL[] urllist = new URL[urls.size()];
			urllist = (URL[])urls.toArray(urllist);
			return URLClassLoader.newInstance(urllist, ClassLoader.getSystemClassLoader());
		} else {
			// if there aren't any url's it means the jar files are in the WAR package
			// in that case, create a new URLClassloader with the current classloader
			// as the parent. The server should have loaded all of the jars in the WAR in
			// a new URLClassloader and this instance should belong to that instance
			return URLClassLoader.newInstance(new URL[0], this.getClass().getClassLoader());
		}
	}
	
	@JsonIgnore
	public ClassLoader getClassLoaders() {
		return this.classloader;
	}
	
	@SuppressWarnings("rawtypes")
	@JsonIgnore
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
	@JsonIgnore
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
	@JsonIgnore
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
	@JsonIgnore
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
					log.info("load function group: " + clzz.getName());
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

	@JsonIgnore
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

	@SuppressWarnings("rawtypes")
	@JsonIgnore
	public boolean loadInitialData(Rete engine) {
		boolean success = true;
		for (int idx=0; idx < this.objectData.size(); idx++) {
			InitialData initialData = (InitialData)this.objectData.get(idx);
			initialData.loadData(engine);
		}
		for (int idx=0; idx < this.clipsData.size(); idx++) {
			ClipsInitialData initialData = this.clipsData.get(idx);
			initialData.loadData(engine);
		}
		for (int idx=0; idx < this.jsonData.size(); idx++) {
			JSONData jdata = this.jsonData.get(idx);
			jdata.setServletContext(servletCtx);
			jdata.loadData(engine);
		}
		this.log.info(" asserted data: clips(" + this.clipsData.size() + ") - object(" + this.objectData.size() +
				") - json(" + this.jsonData.size() + ")");
		return success;
	}

	/**
	 * Reinitialize calls close first to clear the application. Next it
	 * reloads the jar files in the classloader and continues.
	 */
	@JsonIgnore
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
	@JsonIgnore
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

	@JsonIgnore
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

	@JsonIgnore
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

	public List<FunctionPackage> getFunctionGroups() {
		return this.functionGroups;
	}

	public List<ObjectData> getObjectData() {
		return this.objectData;
	}
	
	public List<ClipsInitialData> getClipsData() {
		return this.clipsData;
	}
	
	@SuppressWarnings("rawtypes")
	public List<JSONData> getJsonData() {
		return this.jsonData;
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

	public List<ObjectModel> getModels() {
		return this.models;
	}

	public String getName() {
		return this.applicationName;
	}

	public List<ClipsRuleset> getRulesets() {
		return this.rulesets;
	}

	public String getVersion() {
		return this.version;
	}
	
	public void setFunctionGroups(List<FunctionPackage> functionGroups) {
		this.functionGroups = functionGroups;
	}

	public void setObjectData(List<ObjectData> data) {
		this.objectData = data;
	}

	public void setClipsData(List<ClipsInitialData> data) {
		this.clipsData = data;
	}
	
	@SuppressWarnings("rawtypes")
	public void setJsonData(List<JSONData> data) {
		this.jsonData = data;
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

	public void setModels(List<ObjectModel> models) {
		this.models = models;
	}

	public void setName(String name) {
		this.applicationName = name;
	}

	public void setRulesets(List<ClipsRuleset> rulesets) {
		this.rulesets = rulesets;
	}

	public void setVersion(String value) {
		this.version = value;
	}
	
	@JsonIgnore
	public static void saveConfiguration(String filename, RuleApplication app) {
		FileWriter writer;
		try {
			writer = new FileWriter(filename);
			mapper.writeValue(writer, app);
			writer.close();
		} catch (IOException e) {
			Logger log = LogFactory.createLogger(RuleApplicationImpl.class);
			log.fatal(e);
		}
	}
	
	@JsonIgnore
	public static RuleApplication loadConfiguration(String url) {
		FileReader reader;
		try {
			reader = new FileReader(url);
			RuleApplication app = mapper.readValue(reader, RuleApplication.class);
			return app;
		} catch (Exception e) {
			Logger log = LogFactory.createLogger(RuleApplicationImpl.class);
			log.fatal(e);
		}
		return null;
	}

	@JsonIgnore
	public int getCurrentPoolCount() {
		return currentPoolCount;
	}

	@JsonIgnore
	public void setCurrentPoolCount(int currentPoolCount) {
		this.currentPoolCount = currentPoolCount;
	}
}
