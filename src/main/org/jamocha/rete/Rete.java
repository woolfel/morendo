/*
 * Copyright 2002-2020 Peter Lin
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
package org.jamocha.rete;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.MessageRouter;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.exception.FunctionException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.exception.TemplateAssociationException;
import org.jamocha.rete.functions.BooleanFunctions;
import org.jamocha.rete.functions.DeffunctionGroup;
import org.jamocha.rete.functions.IfFunction;
import org.jamocha.rete.functions.InterpretedFunction;
import org.jamocha.rete.functions.LoadFunctionsFunction;
import org.jamocha.rete.functions.RuleEngineFunctions;
import org.jamocha.rete.functions.UserDefinedFunctions;
import org.jamocha.rete.functions.agent.AgentFunctions;
import org.jamocha.rete.functions.analysis.AnalysisFunctions;
import org.jamocha.rete.functions.bit.BitFunctions;
import org.jamocha.rete.functions.cube.CubeFunctions;
import org.jamocha.rete.functions.io.BatchFunction;
import org.jamocha.rete.functions.io.BuildFunction;
import org.jamocha.rete.functions.io.IOFunctions;
import org.jamocha.rete.functions.java.JavaFunctions;
import org.jamocha.rete.functions.list.ListFunctions;
import org.jamocha.rete.functions.macro.MacroFunctions;
import org.jamocha.rete.functions.math.MathFunctions;
import org.jamocha.rete.functions.memory.MemoryFunctions;
import org.jamocha.rete.functions.messaging.MessagingFunctions;
import org.jamocha.rete.functions.query.QueryFunctions;
import org.jamocha.rete.functions.string.StringFunctions;
import org.jamocha.rete.functions.temporal.TemporalFunctions;
import org.jamocha.rete.functions.text.TextFunctions;
import org.jamocha.rete.functions.time.TimeFunctions;
import org.jamocha.rete.measures.AggregateGroup;
import org.jamocha.rete.measures.Measure;
import org.jamocha.rete.measures.MeasureGroup;
import org.jamocha.rete.util.FactUtils;
import org.jamocha.rete.util.ProfileStats;
import org.jamocha.rule.Defquery;
import org.jamocha.rule.GraphQuery;
import org.jamocha.rule.Query;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * This is the main Rete engine class. For now it's called Rete, but I may
 * change it to Engine to be more generic.
 */
@SuppressWarnings("rawtypes")
public class Rete implements PropertyChangeListener, CompilerListener,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WATCH_ACTIVATIONS = 001;
	public static final int WATCH_ALL = 002;
	public static final int WATCH_FACTS = 003;
	public static final int WATCH_RULES = 004;
	public static final int PROFILE_ADD_ACTIVATION = 101;
	public static final int PROFILE_ASSERT = 102;
	public static final int PROFILE_ALL = 103;
	public static final int PROFILE_FIRE = 104;
	public static final int PROFILE_RETRACT = 105;
	public static final int PROFILE_RM_ACTIVATION = 106;
	protected boolean halt = true;
	protected int firingcount = 0;
	protected boolean prettyPrint = false;
	protected WorkingMemory workingMem = null;
    
	/**
	 * the key is the Class object. The value is the defclass. the defclass is
	 * then used to lookup the deftemplate in the current Module.
	 */
	protected Map defclass = new HashMap();
	protected Map defclassByName = new HashMap();
	protected Map templateToDefclass = new HashMap();
	protected Map classToTemplate = new HashMap();

	/**
	 * this is the HashMap for all functions. This means all function names are
	 * unique.
	 */
	protected Map functions = new HashMap();

	/**
	 * The HashMap for all measures
	 */
	protected Map measures = new HashMap();
	
	protected Map outputStreams = new HashMap();

	/**
	 * an ArrayList for the listeners
	 */
	protected ArrayList listeners = new ArrayList();

	private ArrayList functionGroups = new ArrayList();
	private ArrayList measureGroups = new ArrayList();

	private long lastFactId = 1;

	private int lastNodeId = 0;

    private InterpretedFunction intrFunction = null;
	private Logger log = null;
	private MessageRouter router = new MessageRouter(this);
	protected Deftemplate initFact = new InitialFact();
    private DeffunctionGroup deffunctions = new DeffunctionGroup();
    private RootNode root = new RootNode(this);
    private RuleCompiler compiler = null;
    private Map rulesFired = new HashMap();
    private QueryCompiler queryCompiler = null;
    private GraphQueryCompiler graphQueryCompiler = null;
    private Map queries = new HashMap();
    private Map graphQueries = new HashMap();

	/**
	 * 
	 */
	public Rete() {
		super();
		log = LogFactory.createLogger(Rete.class);
        this.compiler = new DefaultRuleCompiler(this, this.root.getObjectTypeNodes());
        this.queryCompiler = new DefaultQueryCompiler(this, this.root.getObjectTypeNodes());
        this.graphQueryCompiler = new GraphQueryCompiler(this);
		this.workingMem = new DefaultWM(this, root, compiler);
		init();
		startLog();
	}
	
	public Rete(Logger logger) {
		super();
		this.log = logger;
        this.compiler = new DefaultRuleCompiler(this, this.root.getObjectTypeNodes());
		this.workingMem = new DefaultWM(this, root, compiler);
		init();
		startLog();
	}

	/**
	 * initialization logic should go here
	 */
	protected void init() {
		loadBuiltInFunctions();
		loadBuiltInMeasures();
		declareInitialFact();
		this.declareGraph();
        this.compiler.addListener(this);
	}

	@SuppressWarnings("unchecked")
	protected void loadBuiltInFunctions() {
		AgentFunctions agentfuncs = new AgentFunctions();
		declareFunctionGroup(agentfuncs);
		
        AnalysisFunctions analysis = new AnalysisFunctions();
        declareFunctionGroup(analysis);
        
        BitFunctions bitfs = new BitFunctions();
        declareFunctionGroup(bitfs);
        
		BooleanFunctions boolfs = new BooleanFunctions();
		declareFunctionGroup(boolfs);

		CubeFunctions cubefunctions = new CubeFunctions();
		declareFunctionGroup(cubefunctions);

		IOFunctions iof = new IOFunctions();
		declareFunctionGroup(iof);

		// load list functions
		ListFunctions listf = new ListFunctions();
		declareFunctionGroup(listf);

		// load the text functions
		TextFunctions textf = new TextFunctions();
		declareFunctionGroup(textf);
		
		// load the java functions
		JavaFunctions jfuncs = new JavaFunctions();
		declareFunctionGroup(jfuncs);

		// load the math functions
		MathFunctions mathf = new MathFunctions();
		declareFunctionGroup(mathf);

		MemoryFunctions memfuncs = new MemoryFunctions();
		declareFunctionGroup(memfuncs);

		MessagingFunctions msgfuncs = new MessagingFunctions();
		declareFunctionGroup(msgfuncs);
		
		// load the engine relate functions like declaring rules, templates, etc
		RuleEngineFunctions rulefs = new RuleEngineFunctions();
		declareFunctionGroup(rulefs);

		QueryFunctions queryfs = new QueryFunctions();
		declareFunctionGroup(queryfs);
		
		// load string functions
		StringFunctions strfs = new StringFunctions();
		declareFunctionGroup(strfs);

		// load temporal functions
		TemporalFunctions tempfuncs = new TemporalFunctions();
		declareFunctionGroup(tempfuncs);

		// load boolean functions
        TimeFunctions timefs = new TimeFunctions();
        declareFunctionGroup(timefs);
        
        MacroFunctions macros = new MacroFunctions();
        declareFunctionGroup(macros);
		
		declareFunction(new IfFunction());
        functionGroups.add(deffunctions);
        // load function group for user defined functions
        UserDefinedFunctions udfs = new UserDefinedFunctions();
        functionGroups.add(udfs);
        LoadFunctionsFunction lff = (LoadFunctionsFunction)this.functions.get(LoadFunctionsFunction.LOAD_FUNCTION);
        lff.setUserDefinedFunctions(udfs);
	}
	
	@SuppressWarnings("unchecked")
	protected void loadBuiltInMeasures() {
		AggregateGroup aggrGrp = new AggregateGroup();
		aggrGrp.loadMeasures(this);
		measureGroups.add(aggrGrp);
	}

	protected void clearBuiltInFunctions() {
		this.functions.clear();
	}
	
	protected void clearBuiltInMeasures() {
		this.measureGroups.clear();
		this.measures.clear();
	}
	
	protected void startLog() {
		log.info("Morendo started");
	}

	protected void declareInitialFact() {
		declareTemplate(initFact);
		Deffact ifact = (Deffact) initFact.createFact(null, null, this
				.nextFactId());
		try {
			this.assertFact(ifact);
		} catch (AssertException e) {
			// an error should not occur
			log.info(e);
		}
	}
	
	protected void declareGraph() {
		this.declareObject(org.jamocha.model.Graph.class, org.jamocha.model.Graph.class.getSimpleName());
		this.declareObject(org.jamocha.model.Node.class, org.jamocha.model.Node.class.getSimpleName());
		this.declareObject(org.jamocha.model.Edge.class, org.jamocha.model.Edge.class.getSimpleName());
	}

	// ----- methods for clearing rules and facts ----- //

	/**
	 * Clear the objects from the working memory
	 */
	public void clearObjects() {
        this.workingMem.clearObjects();
	}

	/**
	 * clear the deffacts from the working memory. This does not include facts
	 * asserted using assertObject.
	 */
	public void clearFacts() {
        this.workingMem.clearFacts();
	}

	/**
	 * Iterates over all modules and removes all rules.
	 */
	public synchronized void clearRules() {
		Collection modules = this.workingMem.getModules();
		Iterator iterator = modules.iterator();
		while (iterator.hasNext()) {
			Defmodule mod = (Defmodule)iterator.next();
			mod.removeAllRules(this, this.workingMem);
		}
	}
	
	/**
	 * clear all objects and deffacts
	 */
	public void clearAll() {
        this.workingMem.getDynamicFacts().clear();
        this.workingMem.getStaticFacts().clear();
		this.workingMem.getDeffactMap().clear();
		this.workingMem.clear();
		// now we clear all the rules and templates
		this.clearDefclass();
		ProfileStats.reset();
		this.lastFactId = 1;
		this.lastNodeId = 1;
		this.clearBuiltInFunctions();
		this.loadBuiltInFunctions();
		this.clearBuiltInMeasures();
		this.loadBuiltInMeasures();
        FactUtils.reset();
		declareInitialFact();
		declareGraph();
	}

	public void clearDefclass() {
		Iterator iterator = this.defclass.values().iterator();
		while (iterator.hasNext()) {
			Defclass dclass = (Defclass)iterator.next();
			dclass.clear();
		}
		this.defclass.clear();
		this.defclassByName.clear();
	}
	
	/**
	 * Method will clear the engine of all rules, facts and objects.
	 */
	public void close() {
		this.workingMem.clear();
		this.defclass.clear();
		this.workingMem.getDeffactMap().clear();
        this.workingMem.getDynamicFacts().clear();
		this.functions.clear();
		this.workingMem.getInitialFacts().clear();
		this.listeners.clear();
        this.workingMem.getStaticFacts().clear();
	}

    @SuppressWarnings("unchecked")
	protected void addRuleFired(Rule r) {
        this.rulesFired.put(r, null);
    }
    
	/**
	 * this is useful for debugging purposes. clips allows the user to fire 1
	 * rule at a time.
	 * 
	 * @param count
	 * @return
	 */
	public int fire(int count) throws ExecuteException {
		int counter = 0;
		if (this.workingMem.getCurrentFocus().getActivationCount() > 0) {
			Activation actv = null;
			if (this.workingMem.profileFire()) {
				ProfileStats.startFire();
			}
			while ((actv = this.workingMem.getCurrentFocus().nextActivation(this)) != null
					&& counter < count) {
				try {
					if (this.workingMem.watchRules()) {
						this.writeMessage("==> fire: " + actv.toPPString()
								+ "\r\n", "t");
					}
					this.pushScope(actv.getRule());
					actv.executeActivation(this);
					actv.clear();
                    this.popScope();
					counter++;
                    this.addRuleFired(actv.getRule());
				} catch (ExecuteException e) {
					// we need to report the exception
					log.debug(e);
					// we break out of the for loop
					break;
				}
			}
			if (this.workingMem.profileFire()) {
				ProfileStats.endFire();
			}
		}
		return counter;
	}

	/**
	 * this is the normal fire. it will fire all the rules that have matched
	 * completely.
	 * 
	 * @return
	 */
	public int fire() {
		if (this.workingMem.getCurrentFocus().getActivationCount() > 0) {
			// we reset the rules fire count
			this.firingcount = 0;
			Activation actv = null;
			if (this.workingMem.profileFire()) {
				ProfileStats.startFire();
			}
			while ((actv = this.workingMem.getCurrentFocus().nextActivation(this)) != null) {
				try {
					if (this.workingMem.watchRules()) {
						this.writeMessage("==> fire: " + actv.toPPString()
								+ "\r\n", "t");
					}
					// we push the rule into the scope
					this.pushScope(actv.getRule());
					actv.executeActivation(this);
					actv.clear();
                    this.popScope();
                    this.firingcount++;
                    this.addRuleFired(actv.getRule());
				} catch (ExecuteException e) {
					log.debug(e);
				}
			}
			if (this.workingMem.profileFire()) {
				ProfileStats.endFire();
			}
			return this.firingcount;
		} else {
			return 0;
		}
	}

	/**
	 * method is used to fire an activation immediately
	 * 
	 * @param act
	 */
	protected void fireActivation(Activation act) {
		if (act != null) {
			if (this.workingMem.watchRules()) {
				this.writeMessage("==> fire: " + act.toPPString()
						+ "\r\n", "t");
			}
			try {
				this.pushScope(act.getRule());
				act.executeActivation(this);
				act.clear();
                this.popScope();
                this.firingcount++;
                this.addRuleFired(act.getRule());
			} catch (ExecuteException e) {
				log.debug(e);
			}
		}
	}

    /**
     * Method returns a list of the rules that fired
     * @return
     */
    @SuppressWarnings("unchecked")
	public List getRulesFired() {
        ArrayList list = new ArrayList();
        list.addAll(this.rulesFired.keySet());
        return list;
    }
    
    public int getRulesFiredCount() {
    	return this.firingcount;
    }
    
	// ----- defmodule related methods ----- //

	/**
	 * Method returns the current focus. Only the rules in the current focus
	 * will be fired. Activations in other modules will not be fired until the
	 * focus is changed to it.
	 * 
	 * @return
	 */
	public Module getCurrentFocus() {
		return this.workingMem.getCurrentFocus();
	}

	public boolean addModule(String name) {
		if (this.workingMem.addModule(name) == null) {
            return false;
		} else {
            return true;
        }
	}

	public Module addModule(String name, boolean setfocus) {
        Module mod = this.workingMem.addModule(name);
        if (setfocus) {
            this.workingMem.setCurrentModule(mod);
        }
        return mod;
	}

	public Module removeModule(String name) {
        return this.workingMem.removeModule(name);
	}

	public Module findModule(String name) {
		return this.workingMem.findModule(name);
	}

	/**
	 * Add a new Cube definition. Unlike deftemplates, cubes are global
	 * and aren't specific to modules. A cube is available to all modules
	 * currently defined in the engine.
	 * @param cube
	 */
    public void addCube(Cube cube) {
    	this.workingMem.addCube(cube);
    }
    
    /**
     * Get the cube by the name
     * @param name
     * @return
     */
    public Cube getCube(String name) {
    	return this.workingMem.getCube(name);
    }
    
    /**
     * Remove the cube from the engine by the name
     * @param name
     * @return
     */
    public Cube removeCube(String name) {
    	return this.workingMem.removeCube(name);
    }
	
    public List getCubes() {
    	return this.workingMem.getCubes();
    }
    
	/**
	 * find a function by the name. The name is not the class name. It
	 * is the name the function returns in Function.getName().
	 * @param name
	 * @return
	 */
	public Function findFunction(String name) {
		return (Function) this.functions.get(name);
	}

	/**
	 * find the template starting with other modules and ending with the main
	 * module.
	 * 
	 * @param name
	 * @return
	 */
	public Template findTemplate(String name) {
		Template tmpl = null;
		Iterator itr = this.workingMem.getAgenda().modules.values().iterator();
		while (itr.hasNext()) {
			Object val = itr.next();
			if (val != this.workingMem.getMain()) {
				tmpl = ((Defmodule) val).getTemplate(name);
			}
			if (tmpl != null) {
				break;
			}
		}
		// if it wasn't found in any other module, check main
		if (tmpl == null) {
			tmpl = this.workingMem.getMain().getTemplate(name);
		}
		return tmpl;
	}

    // -------- method for declaring an object ------------------ //

	/**
	 * declare an object using the qualified class name, template and
	 * parent. The method will lookup the class. If it fails to find
	 * the class, it will throw a ClassNotFoundException.
	 */
	public void declareObject(String className, String templateName,
			String parent) throws ClassNotFoundException {
		try {
			Class clzz = Class.forName(className);
			declareObject(clzz, templateName, parent);
		} catch (ClassNotFoundException e) {
			// for now do nothing, but we should report the error for real
			log.debug(e);
            throw e;
		}
	}

	/**
	 * Declare the object using the fully qualified class name for the
	 * template name.
	 * @param obj
	 */
	public void declareObject(Class obj) {
		declareObject(obj, null, null);
	}

	/**
	 * Declare a class with a specific template name
	 * 
	 * @param obj
	 * @param templateName
	 */
	public void declareObject(Class obj, String templateName) {
		declareObject(obj, templateName, null);
	}

	/**
	 * @param obj
	 * @param templateName
	 * @param parent -
	 *            the parent template
	 */
	@SuppressWarnings("unchecked")
	public void declareObject(Class obj, String templateName, String parent) {
		// if the class hasn't already been declared, we create a defclass
		// and deftemplate for the class.
		if (!this.defclass.containsKey(obj)) {
			Defclass dclass = new Defclass(obj);
			this.defclassByName.put(obj.getName(), dclass);
			// the Class is the key and the Defclass is the value
			this.defclass.put(obj, dclass);
			if (templateName == null) {
				templateName = obj.getName();
			}
			// we map the template name to Defclass for convienant lookup
			this.templateToDefclass.put(templateName, dclass);
			if (!getCurrentFocus().containsTemplate(dclass)) {
				Template dtemp = null;
				// if the parent is found, we set it
				if (parent != null) {
					Template ptemp = this.workingMem.getCurrentFocus()
							.findParentTemplate(parent);
					if (ptemp != null) {
						dtemp = dclass.createDeftemplate(templateName, ptemp);
						dtemp.setParent(ptemp);
					} else {
						// we need to throw an exception to let users know the
						// parent template wasn't found
					}
				} else {
					dtemp = dclass.createDeftemplate(templateName);
				}
				// we map the Class object to Deftemplate for convienant lookup
				this.classToTemplate.put(obj, dtemp);
				// the key for the deftemplate is the declass, this means
				// that when we assert an object instance to the engine,
				// we need to use the Class to lookup defclass and then
				// use the defclass to lookup the deftemplate. Once we
				// have the deftemplate, we can use it to create the shadow
				// fact for the object instance.
				getCurrentFocus().addTemplate(dtemp, this, this.workingMem);
				writeMessage(dtemp.getName() + Constants.LINEBREAK, "t");
			}
		}
	}
    
	/**
	 * Method removes class declaration from the engine. First it checks to see
	 * if the template is used by a rule. If it is used, the class won't be
	 * removed until the rules have been removed also.
	 * @param clzz
	 */
	public boolean removeObjectType(Class clzz) {
		Template template = (Template)this.classToTemplate.get(clzz);
		if (template.getSlotsUsed() == 0) {
			this.defclass.remove(clzz);
			this.defclassByName.remove(clzz.getName());
			this.templateToDefclass.remove(clzz.getName());
			this.classToTemplate.remove(clzz);
			return true;
		}
		return false;
	}
	
	/**
	 * Declare a cube, so the rule engine can assert cubes and pattern
	 * match against it in rules.
	 * @param cube
	 */
	@SuppressWarnings("unchecked")
	public void declareCube(Cube cube) {
		if (!this.defclass.containsKey(cube)) {
			Defclass dclass = new Defclass(cube.getClass());
			// the Class is the key and the Defclass is the value
			// note that all Cubes are associated to a different instance of Defclass
			this.defclass.put(cube, dclass);
			// we map the template name to Defclass for convienant lookup
			this.templateToDefclass.put(cube.getName(), dclass);
			Template dtemp = dclass.createCubeTemplate(cube);
			// we map the Cube instance to Deftemplate for convienant lookup
			this.classToTemplate.put(cube, dtemp);
			// the key for the deftemplate is the declass, this means
			// that when we assert an object instance to the engine,
			// we need to use the Class to lookup defclass and then
			// use the defclass to lookup the deftemplate. Once we
			// have the deftemplate, we can use it to create the shadow
			// fact for the object instance.
			getCurrentFocus().addTemplate(dtemp, this, this.workingMem);
			writeMessage(dtemp.getName() + Constants.LINEBREAK, "t");
		}
	}
	
	/**
	 * Convienance method for looking up the Deftemplate for a given
	 * Class type. the method is used internally when assertObject(List)
	 * is called.
	 * @param clazz
	 * @return
	 */
	public Deftemplate findDeftemplate(Class clazz) {
		Defclass dclass = (Defclass)this.defclass.get(clazz);
		if (dclass != null) {
			return (Deftemplate)this.classToTemplate.get(clazz);
		}
		return null;
	}
	
	public Defclass findDeclassByTemplate(String templateName) {
		return (Defclass)this.templateToDefclass.get(templateName);
	}
	
	/**
	 * In situations where the domain model uses JAXB style objects,
	 * we may want to Associate the concrete class with a given
	 * deftemplate. if the Clazz object has already been declared,
	 * the method will throw an exception.
	 * @param clazz
	 * @param template
	 */
	@SuppressWarnings("unchecked")
	public void addAssociation(Class clazz, Deftemplate template) throws TemplateAssociationException {
		if (this.defclass.containsKey(clazz)) {
			throw new TemplateAssociationException(clazz.getName() + 
					" has already been declared. Cannot add association");
		} else {
			Defclass dclass = (Defclass)this.templateToDefclass.get(template.getName());
			this.defclass.put(clazz, dclass);
			this.classToTemplate.put(clazz, template);
		}
	}

	/**
	 * method will try to find the defclass using the Template name.
	 * If no defclass is found, method will throw an exception. If
	 * it was found, method looks up the Deftemplate using the Class
	 * and adds the association.
	 * @param clazz
	 * @param templateName
	 * @throws TemplateAssociationException
	 */
	public void addAssociation(Class clazz, String templateName) throws TemplateAssociationException {
		Defclass dclass = (Defclass)this.templateToDefclass.get(templateName);
		if (dclass != null) {
			addAssociation(clazz, findDeftemplate(dclass.getClassObject()) ); 
		} else {
			throw new TemplateAssociationException(templateName + " not found. Cound not add association");
		}
	}
	
	/**
	 * Lookup the Defclass in the defclass HashMap.
	 * @param clazz
	 * @return
	 */
    public Defclass findDefclass(Class clazz) {
        return (Defclass)this.defclass.get(clazz);
    }

    /**
     * Convienance method for looking up the Defclass by the
     * template name. Some times we use this from functions
     * to quickly lookup the defclass.
     * @param templateName
     * @return
     */
    public Defclass findDefclassByTemplate(String templateName) {
        return (Defclass)this.templateToDefclass.get(templateName);
    }

	/**
	 * Return a Set of the declass instances
	 * 
	 * @return
	 */
	public Set getDefclasses() {
		return this.defclass.entrySet();
	}

	/**
	 * Implementation will lookup the defclass for a given object by using the
	 * Class as the key.
	 * 
	 * @param key
	 * @return
	 */
	public Defclass findDefclass(Object key) {
		return (Defclass) this.defclass.get(key.getClass());
	}

	public Defclass findDefclassByName(String key) {
		return (Defclass)this.defclassByName.get(key);
	}
	
	/**
	 * method is specifically for templates that are declared in the shell and
	 * do not have a corresponding java class.
	 * 
	 * @param temp
	 */
	public void declareTemplate(Template temp) {
		if (!getCurrentFocus().containsTemplate(temp.getName())) {
			// the module doesn't contain it, so we add it
			getCurrentFocus().addTemplate(temp, this, this.workingMem);
		}
	}

	/**
	 * To explicitly deploy a custom function, call the method with an instance
	 * of the function
	 * 
	 * @param func
	 */
	@SuppressWarnings("unchecked")
	public void declareFunction(Function func) {
		this.functions.put(func.getName(), func);
        if (func instanceof InterpretedFunction) {
            this.deffunctions.addFunction(func);
        }
	}

	/**
	 * In some cases, we may want to declare a function under an alias. For
	 * example, Add can be alias as "+".
	 * 
	 * @param alias
	 * @param func
	 */
	@SuppressWarnings("unchecked")
	public void declareFunction(String alias, Function func) throws FunctionException {
		if (this.functions.containsKey(alias)) {
			throw new FunctionException(alias + " is already in use. Please use a different alias for the function.");
		} else {
			this.functions.put(alias, func);
		}
	}

	/**
	 * Method will create an instance of the function and declare it. Once a
	 * function is declared, it can be used. All custom functions must be
	 * declared before they can be used.
	 * 
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public Function declareFunction(String name) throws ClassNotFoundException {
		try {
			Class fclaz = Class.forName(name);
			Function func = (Function) fclaz.getDeclaredConstructor().newInstance();
			declareFunction(func);
			return func;
		} catch (ClassNotFoundException e) {
			log.debug(e);
            throw e;
		} catch (IllegalAccessException e) {
			log.debug(e);
		} catch (InstantiationException e) {
			log.debug(e);
		} catch (IllegalArgumentException e) {
			log.debug(e);
		} catch (InvocationTargetException e) {
			log.debug(e);// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			log.debug(e);
		} catch (SecurityException e) {
			log.debug(e);
		}
		return null;
	}
	
	/**
	 * Remove a function using the Function class
	 * @param function
	 */
	public void removeFunction(Function function) {
		this.functions.remove(function.getName());
	}

	/**
	 * Method will create in instance of the FunctionGroup class and load the
	 * functions.
	 * 
	 * @param name
	 */
	@SuppressWarnings("unchecked")
	public void declareFunctionGroup(String name) throws ClassNotFoundException {
		try {
			Class fclaz = Class.forName(name);
			FunctionGroup group = (FunctionGroup) fclaz.getDeclaredConstructor().newInstance();
			declareFunctionGroup(group);
		} catch (ClassNotFoundException e) {
			log.debug(e);
			throw e;
		} catch (IllegalAccessException e) {
			log.debug(e);
		} catch (InstantiationException e) {
			log.debug(e);
		} catch (IllegalArgumentException e) {
			log.debug(e);
		} catch (InvocationTargetException e) {
			log.debug(e);
		} catch (NoSuchMethodException e) {
			log.debug(e);
		} catch (SecurityException e) {
			log.debug(e);
		}
	}

	/**
	 * Method will register the function of the FunctionGroup .
	 * 
	 * @param functionGroup FunctionGroup with the functions to register.
	 */
	@SuppressWarnings("unchecked")
	public void declareFunctionGroup(FunctionGroup functionGroup) {
		functionGroup.loadFunctions(this);
		this.functionGroups.add(functionGroup);
	}

	public void removeFunctionGroup(FunctionGroup functionGroup) {
		Iterator itr = functionGroup.listFunctions().iterator();
		while (itr.hasNext()) {
			Function func = (Function)itr.next();
			this.functions.remove(func.getName());
		}
		this.functionGroups.remove(functionGroup);
	}
	
	/**
	 * Returns a list of the function groups. If a function is not in a group,
	 * get the complete list of functions using getAllFunctions instead.
	 * 
	 * @return
	 */
	public List getFunctionGroups() {
		return this.functionGroups;
	}

	/**
	 * Returns a collection of the function instances
	 * 
	 * @return
	 */
	public Collection getAllFunctions() {
		return this.functions.values();
	}
	
	@SuppressWarnings("unchecked")
	public void declareDefquery(Query query) {
		if (!this.queries.containsKey(query.getName())) {
			this.queries.put(query.getName(), query);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void declareGraphQuery(GraphQuery query) {
		if (!this.graphQueries.containsKey(query.getName())) {
			this.graphQueries.put(query.getName(), query);
		}
	}
	
	public Query getDefquery(String name) {
		return ((Defquery)this.queries.get(name)).clone(this);
	}
	
	public Query removeDefquery(String name) {
		return (Query)this.queries.remove(name);
	}
	
	public GraphQuery getGraphQuery(String name) {
		return ((GraphQuery)this.graphQueries.get(name));
	}
	
	public GraphQuery removeGraphQuery(String name) {
		return (GraphQuery)this.graphQueries.remove(name);
	}
	
	@SuppressWarnings("unchecked")
	public List getAllMeasures() {
		return new ArrayList(this.measures.values());
	}
	
	public Measure findMeasure(String name) {
		return (Measure)measures.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public void declareMeasure(Measure measure) {
		if (!this.measures.containsKey(measure.getMeasureName())) {
			this.measures.put(measure.getMeasureName(), measure);
		}
	}

	@SuppressWarnings("unchecked")
	public void declareMeasureGroup(MeasureGroup measureGroup) {
		if (!this.measureGroups.contains(measureGroup)) {
			this.measureGroups.add(measureGroup);
			measureGroup.loadMeasures(this);
		}
	}
	// ------------- Methods for loading the ruleset ------------------ //
	
	/**
	 * pass a filename to load the rules. The implementation uses BatchFunction
	 * to load the file.
	 * 
	 * @param filename
	 */
	public void loadRuleset(String filename) {
		BatchFunction bf = (BatchFunction) this.functions
				.get(BatchFunction.BATCH);
		Parameter[] params = new Parameter[] { new ValueParam(
				Constants.STRING_TYPE, filename) };
		bf.executeFunction(this, params);
	}

	/**
	 * load the rules from an inputstream. The implementation uses the Batch
	 * function to load the input.
	 * 
	 * @param ins
	 */
	public void loadRuleset(InputStream ins) {
		if (ins != null) {
			BatchFunction bf = (BatchFunction) this.functions
			.get(BatchFunction.BATCH);
			bf.parse(this, ins, null);
		}
	}
	
    public RootNode getRootNode() {
        return this.root;
    }
    
	public void declareDefglobal(String name, Object value) {
		this.workingMem.getDefglobals().declareDefglobal(name, value);
	}

	public Object getDefglobalValue(String name) {
		return this.workingMem.getDefglobals().getValue(name);
	}
    
	public DefglobalMap getDefglobalMap() {
		return this.workingMem.getDefglobals();
	}
	
	public void removeDefglobal(String name) {
		this.workingMem.getDefglobals().removeDefglobal(name);
	}
	
    /**
     * build method will take text and pass it to the parser
     * @param text
     */
    public void build(String text) {
        Function f = this.findFunction(BuildFunction.BUILD);
        ValueParam p = new ValueParam(Constants.STRING_TYPE,text);
        Parameter[] params = new Parameter[]{p};
        f.executeFunction(this, params);
    }

	// -------------- Get / Set methods --------------------- //

	/**
	 * The current implementation will check to see if the variable is a
	 * defglobal. If it is, it will return the value. If not, it will see if
	 * there is an active rule and try to get the local bound value.
	 * 
	 * @param name
	 * @return
	 */
	public Object getBinding(String name) {
        return this.workingMem.getBinding(name);
	}

	/**
	 * This is the main method for setting the bindings. The current
	 * implementation will check to see if the name of the variable begins and
	 * ends with "*". If it does, it will declare it as a defglobal. Otherwise,
	 * it will try to add it to the rule being fired. Note: might need to have
	 * add one for shell variables later.
	 * 
	 * @param key
	 * @param value
	 */
	public void setBindingValue(String key, Object value) {
        this.workingMem.setBindingValue(key, value);
	}

    /**
     * when a rule is active, it should push itself into the scopes. when
     * the rule is done, it has to pop itself out of scope. The same applies
     * to interpretedFunctions.
     * @param s
     */
    public void pushScope(Scope s) {
        this.workingMem.pushScope(s);
    }
    
    /**
     * pop a scope out of the stack
     */
    public void popScope() {
        this.workingMem.popScope();
    }
    
    public void setInterpretedFunction(InterpretedFunction f) {
        this.intrFunction = f;
    }
    
	/**
	 * set the focus to a different module
	 * 
	 * @param moduleName
	 */
	public void setFocus(String moduleName) {
        Module mod = this.workingMem.findModule(moduleName);
		if (mod != null) {
			this.workingMem.setCurrentModule(mod);
		}
	}

	/**
	 * Rete class contains a list of items that can be watched. Call the method
	 * with one of the four types:<br/> activations<br/> all<br/> facts<br/>
	 * rules<br/>
	 * 
	 * @param type
	 */
	public void setWatch(int type) {
		if (type == WATCH_ACTIVATIONS) {
			this.workingMem.getAgenda().setWatch(true);
		} else if (type == WATCH_ALL) {
			this.workingMem.getAgenda().setWatch(true);
            this.workingMem.setWatchFact(true);
            this.workingMem.setWatchRules(true);
		} else if (type == WATCH_FACTS) {
            this.workingMem.setWatchFact(true);
		} else if (type == WATCH_RULES) {
            this.workingMem.setWatchRules(true);
		}
	}

	/**
	 * Call the method with the type to unwatch activations<br/> facts<br/>
	 * rules<br/>
	 * 
	 * @param type
	 */
	public void setUnWatch(int type) {
		if (type == WATCH_ACTIVATIONS) {
			this.workingMem.getAgenda().setWatch(false);
		} else if (type == WATCH_ALL) {
			this.workingMem.getAgenda().setWatch(false);
            this.workingMem.setWatchFact(false);
            this.workingMem.setWatchRules(false);
		} else if (type == WATCH_FACTS) {
            this.workingMem.setWatchFact(false);
		} else if (type == WATCH_RULES) {
            this.workingMem.setWatchRules(false);
		}
	}

	public void setWatchQuery(String name) {
		Query q = (Query)this.queries.get(name);
		if (q != null) {
			q.setWatch(true);
		} else {
			GraphQuery gq = this.getGraphQuery(name);
			if (gq != null) {
				gq.setWatch(true);
			}
		}
	}
	
	public void setUnWatchQuery(String name) {
		Query q = (Query)this.queries.get(name);
		if (q != null) {
			q.setWatch(false);
		}
	}
	
	public void setQueryTime(String name, long time) {
		Query q = (Query)this.queries.get(name);
		if (q != null) {
			((Defquery)q).setElapsedTime(time);
		}
	}
	
	public long getQueryTime(String name) {
		Query q = (Query)this.queries.get(name);
		if (q != null) {
			return ((Defquery)q).getElapsedTime();
		}
		return 0;
	}
	
	/**
	 * To turn on profiling, call the method with the appropriate parameter. The
	 * parameters are defined in Rete class as static int values.
	 * 
	 * @param type
	 */
	public void setProfile(int type) {
		if (type == PROFILE_ADD_ACTIVATION) {
			this.workingMem.getAgenda().setProfileAdd(true);
		} else if (type == PROFILE_ASSERT) {
            this.workingMem.setProfileAssert(true);
		} else if (type == PROFILE_ALL) {
			this.workingMem.getAgenda().setProfileAdd(true);
            this.workingMem.setProfileAssert(true);
            this.workingMem.setProfileFire(true);
            this.workingMem.setProfileRetract(true);
			this.workingMem.getAgenda().setProfileRemove(true);
		} else if (type == PROFILE_FIRE) {
            this.workingMem.setProfileFire(true);
		} else if (type == PROFILE_RETRACT) {
            this.workingMem.setProfileRetract(true);
		} else if (type == PROFILE_RM_ACTIVATION) {
			this.workingMem.getAgenda().setProfileRemove(true);
		}
	}

	/**
	 * To turn off profiling, call the method with the appropriate parameter.
	 * The parameters are defined in Rete class as static int values.
	 * 
	 * @param type
	 */
	public void setProfileOff(int type) {
		if (type == PROFILE_ADD_ACTIVATION) {
			this.workingMem.getAgenda().setProfileAdd(false);
		} else if (type == PROFILE_ASSERT) {
            this.workingMem.setProfileAssert(false);
		} else if (type == PROFILE_ALL) {
			this.workingMem.getAgenda().setProfileAdd(false);
            this.workingMem.setProfileAssert(false);
            this.workingMem.setProfileFire(false);
            this.workingMem.setProfileRetract(false);
			this.workingMem.getAgenda().setProfileRemove(false);
		} else if (type == PROFILE_FIRE) {
            this.workingMem.setProfileFire(false);
		} else if (type == PROFILE_RETRACT) {
            this.workingMem.setProfileRetract(false);
		} else if (type == PROFILE_RM_ACTIVATION) {
			this.workingMem.getAgenda().setProfileRemove(false);
		}
	}

	// --------------- methods for getting facts and counts ----------------- //
	
	/**
	 * return a list of all the facts including deffacts and shadow of objects
	 * 
	 * @return
	 */
	public List getAllFacts() {
		return this.workingMem.getAllFacts();
	}

	/**
	 * Return a list of the objects asserted in the working memory
	 * 
	 * @return
	 */
	public List getObjects() {
        return this.workingMem.getObjects();
	}

	/**
	 * Return a list of all facts which are not shadows of Objects.
	 * 
	 * @return
	 */
	public List getDeffacts() {
		return this.workingMem.getDeffacts();
	}

    /**
     * return just the number of deffacts
     * @return
     */
	public int getDeffactCount() {
		return this.workingMem.getDeffactMap().size();
	}

    /**
     * get the shadow for the object
     * @param key
     * @return
     */
	public Fact getShadowFact(Object key) {
		Fact f = (Fact) this.workingMem.getDynamicFacts().get(key);
		if (f == null) {
			f = (Fact) this.workingMem.getStaticFacts().get(key);
		}
		return f;
	}

    /**
     * changed the implementation so it searches for the fact by id.
     * Starting with the HashMap for deffact, dynamic facts and finally
     * static facts.
     * @param id
     * @return
     */
    public Fact getFactById(long id) {
        return this.workingMem.getFactById(id);
    }
    
	// ----- method for adding output streams for spools ----- //
	/**
	 * this method is for adding printwriters for spools. the purpose of
	 * the spool function is to dump everything out to a file.
	 */
	@SuppressWarnings("unchecked")
	public void addPrintWriter(String name, Writer writer) {
		this.outputStreams.put(name,writer);
	}
	
	/**
	 * It is up to spool function to make sure it removes the printer
	 * writer and closes it properly.
	 * @param name
	 * @return
	 */
	public PrintWriter removePrintWriter(String name) {
		return (PrintWriter)this.outputStreams.remove(name);
	}
	
	// ----- method for writing messages out ----- //
	/**
	 * The method is called by classes to write watch, profiling and other
	 * messages to the output stream. There maybe 1 or more outputstreams.
	 * 
	 * @param msg
	 */
	public void writeMessage(String msg) {
		writeMessage(msg, "t");
	}

	/**
	 * writeMessage will create a MessageEvent and pass it along to any
	 * channels. It will also write out all messages to all registered
	 * PrintWriters. For example, if there's a spool setup, it will write
	 * the messages to the printwriter.
	 * @param msg
	 * @param output
	 */
	public void writeMessage(String msg, String output) {
		MessageRouter router = getMessageRouter();
		router.postMessageEvent(new MessageEvent(MessageEvent.ENGINE, msg, "t"
				.equals(output) ? router.getCurrentChannelId() : output));
		if (this.outputStreams.size() > 0) {
			Iterator itr = this.outputStreams.values().iterator();
			while (itr.hasNext()) {
				PrintWriter wr = (PrintWriter)itr.next();
				wr.write(msg);
				wr.flush();
			}
		}
	}

	/**
	 * The method will print out the node. It is up to the method to check if
	 * pretty printer is true and call the appropriate node method to get the
	 * string.
	 * TODO - need to implement this
	 * @param node
	 */
	public void writeMessage(BaseNode node) {

	}

	// ------------------ methods for assert, retract and modify facts ----------------- //
	
	/**
	 * the method calls WorkingMemory.assertObject
	 * @param data
	 * @param template
	 * @param statc
	 * @param shadow
	 * @throws AssertException
	 */
	public void assertObject(Object data, String template, boolean statc,
			boolean shadow) throws AssertException {
        this.workingMem.assertObject(data, template, statc, shadow);
	}

	/**
	 * the method is used to assert temporal objects. Call the method with the
	 * an optional effective time and required expiration time.
	 * @param data
	 * @param template
	 * @param effective
	 * @param expiration
	 * @param statc
	 * @throws AssertException
	 */
	public void assertTemporalObject(Object data, String template, Date effective, 
    		Date expiration, boolean statc) throws AssertException {
		this.workingMem.assertTemporalObject(data, template, effective, expiration, statc);
	}
	
	/**
	 * By default assertObjects will assert with shadow and dynamic. It also
	 * assumes the classes aren't using an user defined template name.
	 * 
	 * @param objs
	 * @throws AssertException
	 */
	public void assertObjects(List objs) throws AssertException {
        this.workingMem.assertObjects(objs);
	}

	/**
	 * 
	 * @param data
	 */
	public void retractObject(Object data) throws RetractException {
        this.workingMem.retractObject(data);
	}

	/**
	 * Modify will call retract with the old fact, followed by updating the fact
	 * instance and asserting the fact.
	 * 
	 * @param data
	 */
	public void modifyObject(Object data) throws AssertException,
			RetractException {
        this.workingMem.modifyObject(data);
	}

	/**
	 * This method is explicitly used to assert facts.
	 * 
	 * @param fact
	 * @param statc -
	 *            if the fact should be static, assert with true
	 */
	public void assertFact(Fact fact) throws AssertException {
        this.workingMem.assertFact(fact);
	}
	
	/**
	 * Method is for asserting a temporal fact
	 * @param fact
	 * @param expirationTime
	 * @throws AssertException
	 */
	public void assertFact(TemporalFact fact, Date effectiveTime, Date expirationTime) throws AssertException {
		this.workingMem.assertFact(fact, effectiveTime, expirationTime);
	}

	/**
	 * retract by fact id is slower than retracting by the deffact instance. the
	 * method will find the fact and then call retractFact(Deffact)
	 * 
	 * @param id
	 */
	public void retractById(long id) throws RetractException {
		Iterator itr = this.workingMem.getDeffactMap().values().iterator();
		Fact ft = null;
		while (itr.hasNext()) {
			Fact f = (Fact) itr.next();
			if (f.getFactId() == id) {
				ft = f;
				break;
			}
		}
		if (ft != null) {
			retractFact(ft);
		}
	}

	/**
	 * Retract a fact directly
	 * 
	 * @param fact
	 * @throws RetractException
	 */
	public void retractFact(Fact fact) throws RetractException {
        this.workingMem.retractFact(fact);
	}

	/**
	 * Modify retracts the old fact and asserts the new fact. Unlike assertFact,
	 * modifyFact will not check to see if the fact already exists. This is
	 * because the old fact would already be unique.
	 * 
	 * @param old
	 * @param newfact
	 */
	public void modifyFact(Fact old, Fact newfact)
			throws RetractException, AssertException {
		retractFact(old);
		assertFact(newfact);
	}

	// -------------- method for reseting the rule engine ----------------- //
	
	/**
	 * Method will call resetObjects first, followed by resetFacts.
	 */
	public void resetAll() {
        ProfileStats.reset();
		resetObjects();
		resetFacts();
	}

	/**
	 * Method will retract the objects and re-assert them. It does not reset the
	 * deffacts.
	 */
	public void resetObjects() {
		try {
			this.workingMem.getAgenda().startReset();
			
			Iterator itr = this.workingMem.getStaticFacts().values().iterator();
			while (itr.hasNext()) {
				Fact ft = (Fact) itr.next();
				this.workingMem.retractFact(ft);
			}
			itr = this.workingMem.getDynamicFacts().values().iterator();
			while (itr.hasNext()) {
				Fact ft = (Fact) itr.next();
				this.workingMem.retractFact(ft);
			}
			// now assert
			this.workingMem.getAgenda().endReset();
			
			itr = this.workingMem.getStaticFacts().values().iterator();
			while (itr.hasNext()) {
				Fact ft = (Fact) itr.next();
				this.workingMem.assertFact(ft);
			}
			itr = this.workingMem.getDynamicFacts().values().iterator();
			while (itr.hasNext()) {
				Fact ft = (Fact) itr.next();
				this.workingMem.assertFact(ft);
			}
		} catch (RetractException e) {
			log.debug(e);
		} catch (AssertException e) {
			log.debug(e);
		}
	}

	/**
	 * Method will retract all the deffacts and then re-assert them. Reset does
	 * not reset the objects. To reset both the facts and objects, call
	 * resetAll. resetFacts handles deffacts which are not derived from objects.
	 */
	@SuppressWarnings("unchecked")
	public void resetFacts() {
		try {
			this.workingMem.getAgenda().startReset();
			
            List facts = new ArrayList(this.workingMem.getDeffactMap().values());
			Iterator itr = facts.iterator();
			while (itr.hasNext()) {
				Fact ft = (Fact) itr.next();
				this.workingMem.retractFact(ft);
			}

			// now assert
			this.workingMem.getAgenda().endReset();
			
			itr = facts.iterator();
			while (itr.hasNext()) {
				Fact ft = (Fact) itr.next();
				this.workingMem.assertFact(ft);
			}
		} catch (RetractException e) {
			log.debug(e);
		} catch (AssertException e) {
			log.debug(e);
		}
	}

	/**
	 * This is temporary, it should be replaced with something like the current
	 * factHandleFactory().newFactHandle()
	 * 
	 * @return
	 */
	public long nextFactId() {
		return this.lastFactId++;
	}

	public Agenda getAgenda() {
		return this.workingMem.getAgenda();
	}

	/**
	 * return the next rete node id for a new node
	 * 
	 * @return
	 */
	public int nextNodeId() {
		return ++this.lastNodeId;
	}

	/**
	 * peak at the next node id. Do not use this method to get an id for the
	 * next node. only nextNodeId() should be used to create new rete nodes.
	 * 
	 * @return
	 */
	public int peakNextNodeId() {
		return this.lastNodeId + 1;
	}

	public RuleCompiler getRuleCompiler() {
		return this.workingMem.getRuleCompiler();
	}
	
	public QueryCompiler getQueryCompiler() {
		return this.queryCompiler;
	}
	
	public GraphQueryCompiler getGraphQueryCompiler() {
		return this.graphQueryCompiler;
	}

	public WorkingMemory getWorkingMemory() {
		return this.workingMem;
	}

	public Strategy getStrategy() {
		return this.workingMem.getStrategy();
	}

	public ActivationList getActivationList() {
		return this.workingMem.getCurrentFocus().getAllActivations();
	}

	public int getObjectCount() {
		return this.workingMem.getDynamicFacts().size() + this.workingMem.getStaticFacts().size();
	}

	public void setValidateRules(boolean val) {
		this.workingMem.getRuleCompiler().setValidateRule(val);
	}
	
	public boolean getValidateRules() {
		return this.workingMem.getRuleCompiler().getValidateRule();
	}
	
	/// Map methods
	public Map newMap() {
		return new HashMap();
	}
	
	public Map newLocalMap() {
		return new HashMap();
	}
	
    public Map newAlphaMemoryMap(String name) {
        return new HashMap();
    }
    
    public Map newLinkedHashmap(String name) {
        return new LinkedHashMap();
    }
    
    public Map newBetaMemoryMap(String name) {
        return new HashMap();
    }
    
    public Map newTerminalMap() {
    	return new HashMap();
    }
    
    public Map newClusterableMap(String name) {
        return new HashMap();
    }
    
	/**
	 * Method calls modifyObject to notify the engine the
	 * object has changed. No optimization at this point
	 * in time. Later on we can check to make sure the
	 * object actually changed, but that shouldn't be
	 * necessary if the class checks the field before
	 * calling propertyChange.
	 * 
	 * @param event
	 */
	public void propertyChange(PropertyChangeEvent event) {
		Object source = event.getSource();
		try {
			this.modifyObject(source);
		} catch (RetractException e) {
			log.debug(e);
		} catch (AssertException e) {
			log.debug(e);
		}
	}

	/**
	 * Add a listener if it isn't already a listener
	 * 
	 * @param listen
	 */
	@SuppressWarnings("unchecked")
	public void addEngineEventListener(EngineEventListener listen) {
		if (!this.listeners.contains(listen)) {
			this.listeners.add(listen);
		}
	}

	/**
	 * remove a listener
	 * 
	 * @param listen
	 */
	public void removeEngineEventListener(EngineEventListener listen) {
		this.listeners.remove(listen);
	}

	/**
	 * For now, this is not implemented
	 * 
	 * @param event
	 */
	public void ruleAdded(CompileEvent event) {
		this.log.info("added: " + event.getMessage());
	}

	/**
	 * For now, this is not implemented
	 * 
	 * @param event
	 */
	public void ruleRemoved(CompileEvent event) {
		this.log.info("removed: " + event.getMessage());
	}

	/**
	 * For now, this is not implemented
	 * 
	 * @param event
	 */
	public void compileError(CompileEvent event) {
		this.log.warn(event.getMessage());
	}

	public MessageRouter getMessageRouter() {
		return router;
	}

	public Deftemplate getInitFact() {
		return initFact;
	}
}
