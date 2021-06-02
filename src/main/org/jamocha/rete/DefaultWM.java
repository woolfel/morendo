/*
 * Copyright 2002-2010 Jamocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;
import org.jamocha.rete.query.QueryCubeQueryJoin;
import org.jamocha.rete.query.QueryExistJoin;
import org.jamocha.rete.query.QueryExistNeqJoin;
import org.jamocha.rete.query.QueryHashedEqJoin;
import org.jamocha.rete.query.QueryHashedEqNot;
import org.jamocha.rete.query.QueryHashedNeqJoin;
import org.jamocha.rete.query.QueryHashedNeqNot;
import org.jamocha.rete.query.QueryMultipleJoin;
import org.jamocha.rete.query.QueryMultipleNeqJoin;
import org.jamocha.rete.query.QueryOnlyJoin;
import org.jamocha.rete.query.QueryOnlyNeqJoin;
import org.jamocha.rete.strategies.Strategies;
import org.jamocha.rete.util.ProfileStats;

/**
 * @author Peter Lin
 *
 * This a new implementation of the working memory that is a clean rewrite to make
 * it organized. The old one was getting a bit messy and refactoring it was becoming
 * a pain.
 */
public class DefaultWM implements WorkingMemory, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Rete engine = null;

    protected RootNode root = null;

	protected Map<Object, Object> alphaMemories = null;
    protected Map<Object, Object> betaLeftMemories = null;
	protected Map<Object, Object> betaRightMemories = null;
	protected Map<Object, Object> terminalMemories = null;
	protected Map<Object, Object> queryLeftMemories = null;
	protected Map<Object, Object> queryRightMemories = null;
    protected RuleCompiler compiler = null;

    /**
     * We keep a map between the object instance and the corresponding shadown
     * fact. If an object is added as static, it is added to this map. When the
     * rule engine is notified of changes, it will check this list. If the
     * object instance is in this list, we ignore it.
     */
    protected Map<Object, Object> staticFacts = null;
    /**
     * We keep a map of the dynamic object instances. When the rule engine is
     * notified
     */
    protected Map<Object, Object> dynamicFacts = null;
    /**
     * We use a HashMap to make it easy to determine if an existing deffact
     * already exists in the working memory. this is only used for deffacts and
     * not for objects
     */
    protected Map<Object, Object> deffactMap = null;
    /**
     * Container for Defglobals
     */
    protected DefglobalMap defglobals = null;
    /**
     * The initial facts the rule engine needs at startup
     */
    protected ArrayList<?> initialFacts = new ArrayList<Object>();

    private Agenda agenda = null;
    /**
     * The ArrayList for the modules.
     */
    protected Map<Object, Module> modules = null;
    protected Map<Object, Object> cubes = null;
    protected Hashtable<Object, Object> contexts = new Hashtable<Object, Object>();
    protected ArrayList<?> focusStack = new ArrayList<Object>();
    private Module main = null;
    private Module currentModule = null;
    private Strategy theStrat = null;
    private Stack<Scope> scopes = new Stack<Scope>();

    private boolean watchFact = false;
    private boolean watchRules = false;
    private boolean profileFire = false;
    private boolean profileAssert = false;
    private boolean profileRetract = false;
    
    @SuppressWarnings("unchecked")
	public DefaultWM(Rete engine, RootNode node, RuleCompiler compiler) {
        this.engine = engine;
        alphaMemories = (Map<Object, Object>) engine.newMap();
        betaLeftMemories = (Map<Object, Object>) engine.newMap();
        betaRightMemories = (Map<Object, Object>) engine.newMap();
        terminalMemories = (Map<Object, Object>) engine.newMap();
        staticFacts = (Map<Object, Object>) engine.newLocalMap();
        dynamicFacts = (Map<Object, Object>) engine.newLocalMap();
        deffactMap = (Map<Object, Object>) engine.newLocalMap();
        modules = (Map<Object, Module>) engine.newLocalMap();
        cubes = (Map<Object, Object>) engine.newLocalMap();
        queryRightMemories = (Map<Object, Object>) engine.newLocalMap();
        queryLeftMemories = (Map<Object, Object>) engine.newLocalMap();
        this.defglobals = new DefglobalMap(engine);
        this.root = node;
        this.compiler = compiler;
        this.compiler.setWorkingMemory(this);
        this.agenda = new Agenda(engine);
        init();
    }
    
    protected void init() {
        this.theStrat = Strategies.DEPTH;
        this.main = new Defmodule(Constants.MAIN_MODULE, engine);
        this.main.setStrategy(this.theStrat);
        this.addModule(this.main);
        this.currentModule = main;
    }

    public Module addModule(String name) {
        Module mod = findModule(name);
        if (mod == null) {
            mod = new Defmodule(name, engine);
            this.modules.put(mod.getModuleName(), mod);
            this.setCurrentModule(mod);
        }
        return mod;
    }
    
    public void addModule(Module mod) {
        if (mod != null) {
            this.modules.put(mod.getModuleName(), mod);
            this.setCurrentModule(mod);
        }
    }
    
	public Collection<Module> getModules() {
        return this.modules.values();
    }
    
    public void addCube(Cube cube) {
    	if (!cubes.containsKey(cube.getName())) {
    		cubes.put(cube.getName(), cube);
    	}
    }
    
    public Cube getCube(String name) {
    	return (Cube)cubes.get(name);
    }
    
    public Cube removeCube(String name) {
    	return (Cube)cubes.remove(name);
    }
    
	public List<?> getCubes() {
    	return new ArrayList<Object>(this.cubes.keySet());
    }
    
	public void assertFact(Fact fact) throws AssertException {
        Fact f = (Fact)fact;
        if (!this.containsFact(f)) {
            this.deffactMap.put(fact.equalityIndex(), f);
            f.setFactId(engine);
            if (this.profileAssert) {
                this.assertFactWProfile(f);
            } else {
                if (this.watchFact) {
                    engine.writeMessage("==> " + fact.toFactString()
                            + Constants.LINEBREAK, "t");
                }
                this.root.assertObject(f, engine, this);
            }
        } else {
            f.resetID((Fact) this.deffactMap.get(fact.equalityIndex()));
        }
    }
    
    public void assertFact(TemporalFact fact, Date effectiveTime, Date expirationTime) throws AssertException {
    	if (expirationTime != null) {
    		fact.setEffectiveTime(effectiveTime.getTime());
    		fact.setExpirationTime(expirationTime.getTime());
    	}
    	assertFact(fact);
    }

    /**
     * The current implementation of assertObject is simple, but flexible. This
     * version is not multi-threaded and doesn't use an event queue. Later on a
     * multi-threaded version will be written which overrides the base
     * implementation. If the user passes a specific template name, the engine
     * will attempt to only propogate the fact down that template. if no
     * template name is given, the engine will propogate the fact down all input
     * nodes, including parent templates.
     * 
     * @param data
     * @param template
     * @param statc
     * @param shadow
     * @throws AssertException
     */
	public void assertObject(Object data, String template, boolean statc,
            boolean shadow) throws AssertException {
        Defclass dc = null;
        if (template == null) {
            dc = this.engine.findDefclass(data);
            // Note: cubes aren't mapped by defclass, so we always lookup by template using cube name
            if (data instanceof Cube) {
            	dc = this.engine.findDefclassByTemplate(((Cube)data).getName());
            }
        } else {
            dc = this.engine.findDefclassByTemplate(template);
        }
        if (dc != null) {
            if (statc && !this.getStaticFacts().containsKey(data)) {
                Fact shadowfact = createFact(data, dc, template, engine.nextFactId(), false);
                // add it to the static fact map
                this.getStaticFacts().put(data, shadowfact);
                this.assertFact(shadowfact);
            } else if (!this.getDynamicFacts().containsKey(data)) {
                if (shadow) {
                    // first add the rule engine as a listener
                    if (dc.isJavaBean()) {
                        try {
                            dc.getAddListenerMethod().invoke(data,
                                    new Object[] { this.engine });
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    // second, lookup the deftemplate and create the
                    // shadow fact
                    Fact shadowfact = createFact(data, dc, template,
                            engine.nextFactId(), false);
                    // add it to the dynamic fact map
                    this.getDynamicFacts().put(data, shadowfact);
                    this.assertFact(shadowfact);
                } else {
                    Fact nsfact = createNSFact(data, dc, engine.nextFactId());
                    this.getDynamicFacts().put(data, nsfact);
                    this.assertFact(nsfact);
                }
            }
        }
    }
    
    /**
     * Method is used to assert temporal facts, which have effective and expiration time
     */
	public void assertTemporalObject(Object data, String template, Date effective, 
    		Date expiration, boolean statc) throws AssertException {
        Defclass dc = null;
        if (template == null) {
            dc = this.engine.findDefclass(data);
        } else {
            dc = this.engine.findDefclassByTemplate(template);
        }
        if (dc != null) {
            if (statc && !this.getStaticFacts().containsKey(data)) {
                Fact shadowfact = createFact(data, dc, template, engine.nextFactId(),true);
                // add it to the static fact map
                this.getStaticFacts().put(data, shadowfact);
                this.assertFact(shadowfact);
            } else if (!this.getDynamicFacts().containsKey(data)) {
                // first add the rule engine as a listener
                if (dc.isJavaBean()) {
                    try {
                        dc.getAddListenerMethod().invoke(data,
                                new Object[] { this.engine });
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                // second, lookup the deftemplate and create the
                // shadow fact
                TemporalFact shadowfact = (TemporalFact)createFact(data, dc, template,
                        engine.nextFactId(), true);
                // add it to the dynamic fact map
                shadowfact.setEffectiveTime(effective.getTime());
                shadowfact.setExpirationTime(expiration.getTime());
                this.getDynamicFacts().put(data, shadowfact);
                this.assertFact(shadowfact);
            }
        }
    }

    /**
     * By default assertObjects will assert with shadow and dynamic. It also
     * assumes the classes aren't using an user defined template name.
     * 
     * @param objs
     * @throws AssertException
     */
	public void assertObjects(List<?> objs) throws AssertException {
        Iterator<?> itr = objs.iterator();
        while (itr.hasNext()) {
        	Object fact = itr.next();
            assertObject(fact, null, false, true);
        }
    }

	public void clear() {
        Iterator<?> amitr = this.alphaMemories.values().iterator();
        while (amitr.hasNext()) {
            AlphaMemory am = (AlphaMemory) amitr.next();
            am.clear();
        }
        this.alphaMemories.clear();
        // aggressivley clear the memories
        Iterator<?> blitr = this.betaLeftMemories.values().iterator();
        while (blitr.hasNext()) {
            Object bval = blitr.next();
            if (bval instanceof Map) {
                Map<?, ?> lmem = (Map<?, ?>) bval;
                // now iterate over the betamemories
                Iterator<?> bmitr = lmem.keySet().iterator();
                while (bmitr.hasNext()) {
                	Object value = bmitr.next();
                	if (value instanceof Index) {
                        Index indx = (Index) value;
                        indx.clear();
                	} else if (value instanceof Map) {
                		((Map<?, ?>)value).clear();
                	}
                }
                lmem.clear();
            }
        }
        this.betaLeftMemories.clear();
        Iterator<?> britr = this.betaRightMemories.values().iterator();
        while (britr.hasNext()) {
            Object val = britr.next();
            if (val instanceof HashedAlphaMemoryImpl) {
                ((HashedAlphaMemoryImpl) val).clear();
            } else if (val instanceof TemporalHashedAlphaMem) {
                ((TemporalHashedAlphaMem)val).clear();
            } else if (val instanceof CubeHashMemoryImpl) {
            	((CubeHashMemoryImpl)val).clear();
            } else {
                Map<?, ?> mem = (Map<?, ?>) val;
                mem.clear();
            }
        }
        this.betaRightMemories.clear();
        this.terminalMemories.clear();
        this.root.clear();
        this.focusStack.clear();
        this.contexts.clear();
        this.agenda.clear();
        this.main.clear();
        Collection<?> mods = this.modules.values();
        Iterator<?> mitr = mods.iterator();
        while (mitr.hasNext()) {
        	((Module)mitr.next()).clear();
        }
        this.cubes.clear();
        this.addModule(this.main);
    }

    /**
     * clear the deffacts from the working memory. This does not include facts
     * asserted using assertObject.
     */
	public void clearFacts() {
		if (this.deffactMap.size() > 0) {
			try {
				List<?> facts = new ArrayList<Object>(this.deffactMap.keySet());
				Iterator<?> itr = facts.iterator();
				while (itr.hasNext()) {
					Object obj = itr.next();
					if (obj instanceof EqualityIndex) {
						EqualityIndex i = (EqualityIndex)obj;
						Deffact f = (Deffact)this.deffactMap.get(i);
						if (!(f.getDeftemplate() instanceof InitialFact)) {
							this.retractFact(f);
							this.deffactMap.remove(i);
						}
					}
				}
			} catch (RetractException e) {
				
			}
		}
    }

    /**
     * Clear the objects from the working memory
     */
	public synchronized void clearObjects() {
        if (this.getDynamicFacts().size() > 0) {
            try {
            	ArrayList<?> objects = new ArrayList<Object>(this.dynamicFacts.keySet());
                Iterator<?> itr = objects.iterator();
                while (itr.hasNext()) {
                    Object obj = itr.next();
                    retractObject(obj);
                }
            } catch (RetractException e) {
                // log.debug(e);
            }
        }
        if (this.getStaticFacts().size() > 0) {
            try {
            	ArrayList<?> objects = new ArrayList<Object>(this.getStaticFacts().keySet());
                Iterator<?> itr = objects.iterator();
                while (itr.hasNext()) {
                    Object obj = itr.next();
                    retractObject(obj);
                }
            } catch (RetractException e) {
                // log.debug(e);
            }
        }
    }

    /**
     * The implementation will look in the current module in focus. If it isn't
     * found, it will search the other modules. The last module it checks should
     * be the main module.
     * 
     * @param data
     * @param id
     * @return
     */
    protected Fact createFact(Object data, Defclass dclass, String template,
            long id, boolean temporal) throws AssertException {
        Fact ft = null;
        Template dft = null;
        if (template == null) {
        	if (data instanceof Cube) {
        		dft = getCurrentFocus().getTemplate( ((Cube)data).getName() );
        	} else {
                dft = getCurrentFocus().getTemplate(
                        dclass.getClassObject().getName());
        	}
        } else {
            dft = getCurrentFocus().getTemplate(template);
        }
        // if the deftemplate is null, check the other modules
        if (dft == null) {
            // get the entry set from the agenda and iterate
            Iterator<?> itr = this.modules.values().iterator();
            while (itr.hasNext()) {
                Module mod = (Module) itr.next();
                if (mod.containsTemplate(dclass)) {
                    dft = mod.getTemplate(dclass);
                    break;
                }
            }
            // we've searched every module, so now check main
            if (dft == null && this.main.containsTemplate(dclass)) {
                dft = this.main.getTemplate(dclass);
            } 
            if (dft == null) {
                // throw an exception
                throw new AssertException("Could not find the template");
            }
        }
        if (temporal) {
        	ft = dft.createTemporalFact(data, dclass, id);
        } else {
            ft = dft.createFact(data, dclass, id);
        }
        return ft;
    }

    /**
     * convienance method for creating a Non-Shadow fact.
     * 
     * @param data
     * @param id
     * @return
     */
    protected Fact createNSFact(Object data, Defclass dclass, long id) {
        Deftemplate dft = (Deftemplate) getCurrentFocus().getTemplate(dclass);
        NSFact fact = new NSFact(dft, dclass, data, dft.getAllSlots(), id);
        return fact;
    }

    public Module findModule(String name) {
        return (Module)this.modules.get(name);
    }
    
    /**
     * Returns all alpha memories in a Map. The key is the alpha node
     * and the value is the memory for it
     * @return
     */
    public Map<?, ?> getAllAlphaMemories() {
        return this.alphaMemories;
    }
    
    /**
     * The current implementation will try to find the memory for the node.
     * If it doesn't find it, it will create a new one.
     */
    public Object getAlphaMemory(Object key) {
        Object m = this.alphaMemories.get(key);
        if (m == null) {
            String mname = "alphamem" + ((BaseNode) key).nodeID;
            m = new AlphaMemoryImpl(mname, engine);
            this.alphaMemories.put(key, m);
        }
        return m;
    }

    /**
     * Returns all left beta memories in a Map. The key is the beta node
     * and the value is the memory for it
     * @return
     */
    public Map<?, ?> getAllBetaLeftMemories() {
        return this.betaLeftMemories;
    }
    
    /**
     * the current implementation will try to find the memory for the node.
     * If it doesn't find it, it will create a new Left memory, which is
     * HashMap.
     */
    public Object getBetaLeftMemory(Object key) {
        Object m = this.betaLeftMemories.get(key);
        if (m == null) {
            String mname = "blmem" + ((BaseNode) key).nodeID;
            m = engine.newBetaMemoryMap(mname);
            this.betaLeftMemories.put(key, m);
        }
        return m;
    }
    
    public Object getQueryBetaMemory(Object key) {
    	Object m = this.queryLeftMemories.get(key);
    	if (m == null) {
    		String mname = "query" + ((BaseNode) key).nodeID;
    		m = engine.newBetaMemoryMap(mname);
    		this.queryLeftMemories.put(key, m);
    	}
    	return m;
    }

    public Map<?, ?> getAllBetaRightMemories() {
        return this.betaRightMemories;
    }
    
    /**
     * the current implementation will try to find the memory for the node.
     * If it doesn't find it, it checks the node type and creates the
     * appropriate AlphaMemory for the node. Since right memories are
     * hashed, it creates the appropriate type of Hashed memory.
     */
	public Object getBetaRightMemory(Object key) {
        Object val = this.betaRightMemories.get(key);
        if (val != null) {
            return val;
        } else {
            if (key instanceof HashedEqBNode || key instanceof HashedEqNJoin ||
                    key instanceof ExistJoin || key instanceof OnlyJoin ||
                    key instanceof MultipleJoin) {
                String mname = "hnode" + ((BaseNode) key).nodeID;
                HashedAlphaMemoryImpl alpha = new HashedAlphaMemoryImpl(mname, engine);
                this.betaRightMemories.put(key, alpha);
                return alpha;
            } else if (key instanceof HashedNotEqBNode || key instanceof HashedNotEqNJoin ||
                    key instanceof ExistNeqJoin || key instanceof OnlyNeqJoin || 
                    key instanceof MultipleNeqJoin) {
                String mname = "hneq" + ((BaseNode) key).nodeID;
                HashedNeqAlphaMemory alpha = new HashedNeqAlphaMemory(mname, engine);
                this.betaRightMemories.put(key, alpha);
                return alpha;
            } else if (key instanceof TemporalEqNode || key instanceof TemporalIntervalNode) {
                String mname = "hnode" + ((BaseNode) key).nodeID;
                TemporalHashedAlphaMem alpha = new TemporalHashedAlphaMem(mname, engine);
                this.betaRightMemories.put(key, alpha);
                return alpha;
            } else if (key instanceof CubeQueryBNode) {
            	String mname = "cqbnode" + ((BaseNode)key).nodeID;
            	CubeHashMemoryImpl alpha = new CubeHashMemoryImpl(mname, engine);
            	this.betaRightMemories.put(key, alpha);
            	return alpha;
            } else {
                String mname = "brmem" + ((BaseNode) key).nodeID;
                Map<?, ?> right = engine.newAlphaMemoryMap(mname);
                this.betaRightMemories.put(key, right);
                return right;
            }
        }
    }

	public Object getQueryRightMemory(Object key) {
    	Object val = this.queryRightMemories.get(key);
    	if (val != null) {
    		return val;
    	} else {
        	if (key instanceof QueryHashedEqJoin || key instanceof QueryHashedEqNot ||
        			key instanceof QueryExistJoin || key instanceof QueryOnlyJoin ||
        			key instanceof QueryMultipleJoin) {
                String mname = "hnode" + ((BaseNode) key).nodeID;
                HashedAlphaMemoryImpl alpha = new HashedAlphaMemoryImpl(mname, engine);
                this.queryRightMemories.put(key, alpha);
                return alpha;
        	} else if (key instanceof QueryHashedNeqJoin || key instanceof QueryHashedNeqNot ||
        			key instanceof QueryExistNeqJoin || key instanceof QueryOnlyNeqJoin ||
        			key instanceof QueryMultipleNeqJoin) {
                String mname = "hneq" + ((BaseNode) key).nodeID;
                HashedNeqAlphaMemory alpha = new HashedNeqAlphaMemory(mname, engine);
                this.queryRightMemories.put(key, alpha);
                return alpha;
            } else if (key instanceof QueryCubeQueryJoin) {
            	String mname = "cqbnode" + ((BaseNode)key).nodeID;
            	CubeHashMemoryImpl alpha = new CubeHashMemoryImpl(mname, engine);
            	this.queryRightMemories.put(key, alpha);
            	return alpha;
            } else {
                String mname = "brmem" + ((BaseNode) key).nodeID;
                Map<?, ?> right = engine.newAlphaMemoryMap(mname);
                this.queryRightMemories.put(key, right);
                return right;
        	}
    	}
    }
    
    public Agenda getAgenda() {
        return this.agenda;
    }
    
    public Object getBinding(String name) {
        if (!this.scopes.isEmpty() && !name.startsWith("*")) {
            Object val =  this.scopes.peek().getBindingValue(name);
            return val;
        } else {
            return this.getDefglobals().getValue(name);
        }
    }

    	public Map<?, Object> getDeffactMap() {
        return this.deffactMap;
    }

    public DefglobalMap getDefglobals() {
        return this.defglobals;
    }

	public List<Fact> getAllFacts() {
        ArrayList<Fact> facts = new ArrayList<Fact>();
        facts.addAll(this.getDeffacts());
        return facts;
    }
    
	public List<Fact> getDeffacts() {
        ArrayList<Fact> objects = new ArrayList<Fact>();
        Iterator<Object> itr = this.getDeffactMap().values().iterator();
        while (itr.hasNext()) {
            Object fact = itr.next();
            objects.add((Fact) fact);
        }
        return objects;
    }
    
	public Fact getFactById(long id) {
        Fact df = null;
        Iterator<?> itr = this.getDeffactMap().values().iterator();
        while (itr.hasNext()) {
            df = (Deffact)itr.next();
            if (df.getFactId() == id) {
                return df;
            }
        }
        // now search the object facts
        if (df == null) {
            // check dynamic facts
            Iterator<?> itr2 = this.getDynamicFacts().values().iterator();
            while (itr2.hasNext()) {
                df = (Fact)itr2.next();
                if (df.getFactId() == id) {
                    return df;
                }
            }
            if (df == null) {
                itr2 = this.getStaticFacts().values().iterator();
                while (itr2.hasNext()) {
                    df = (Fact)itr2.next();
                    if (df.getFactId() == id) {
                        return df;
                    }
                }
            }
        }
        return null;
    }
    
   	public List<Object> getObjects() {
        ArrayList<Object> objects = new ArrayList<Object>();
        Iterator<?> itr = this.getDynamicFacts().keySet().iterator();
        while (itr.hasNext()) {
            Object key = itr.next();
            if (!(key instanceof Fact)) {
                objects.add(key);
            }
        }
        itr = this.getStaticFacts().keySet().iterator();
        while (itr.hasNext()) {
            Object key = itr.next();
            if (!(key instanceof Fact)) {
                objects.add(key);
            }
        }
        return objects;
    }
    
	public List<?> getInitialFacts() {
        return this.initialFacts;
    }
    
    public Module getCurrentFocus() {
        return this.currentModule;
    }
    
    public Module getMain() {
        return this.main;
    }
    
	public Map<Object, Object> getDynamicFacts() {
        return this.dynamicFacts;
    }
    
    public RuleCompiler getRuleCompiler() {
        return this.compiler;
    }

	public Map<Object, Object> getStaticFacts() {
        return this.staticFacts;
    }
    
    public Strategy getStrategy() {
        return this.theStrat;
    }

    public Object getTerminalMemory(Object key) {
        Object m = this.terminalMemories.get(key);
        if (m == null) {
            m = engine.newTerminalMap();
            this.terminalMemories.put(key, m);
        }
        return m;
    }

    /**
     * Modify will call retract with the old fact, followed by updating the fact
     * instance and asserting the fact.
     * 
     * @param data
     */
	public void modifyObject(Object data) throws AssertException,
            RetractException {
        if (this.getDynamicFacts().containsKey(data)) {
            Defclass dc = (Defclass) this.engine.findDefclass(data);
            // first we retract the fact
            Fact ft = (Fact) this.getDynamicFacts().remove(data);
            // check to see if the fact is a temporal fact
            boolean temporal = false;
            if (ft instanceof TemporalFact) {
            	temporal = true;
            }
            String tname = ft.getDeftemplate().getName();
            long fid = ft.getFactId();
            this.retractFact(ft);
            // create a new fact with the same ID
            ft = createFact(data, dc, tname, fid, temporal);
            this.getDynamicFacts().put(data, ft);
            this.assertFact(ft);
        }
    }
    
    public boolean profileAssert() {
        return this.profileAssert;
    }

    public boolean profileFire() {
        return this.profileFire;
    }

    public boolean profileRetract() {
        return this.profileRetract;
    }

    public boolean watchFact() {
        return this.watchFact;
    }

    public boolean watchRules() {
        return this.watchRules;
    }

    public void popScope() {
        this.scopes.pop();
    }

	public void pushScope(Scope s) {
        this.scopes.push(s);
    }

    public void removeAlphaMemory(Object key) {
        this.alphaMemories.remove(key);
    }

    public Module removeModule(String name) {
        return (Module)this.modules.remove(name);
    }

    public void retractFact(Fact fact) throws RetractException {
        this.deffactMap.remove(fact.equalityIndex());
        if (this.profileRetract) {
            this.retractFactWProfile(fact);
        } else {
            if (watchFact) {
                engine.writeMessage("<== " + fact.toFactString()
                        + Constants.LINEBREAK, "t");
            }
            this.root.retractObject(fact, engine, this);
        }
    }

    /**
     * 
     * @param data
     */
    public synchronized void retractObject(Object data) throws RetractException {
        if (this.getStaticFacts().containsKey(data)) {
            Fact ft = (Fact) this.getStaticFacts().remove(data);
            this.retractFact(ft);
            // Note: not sure if this could potentially lead to a slow memory
            // leak under load if we don't clear the shadow fact for an object
            if (ft.getObjectInstance() == null) {
                ft.clear();
            }
        } else if (this.getDynamicFacts().containsKey(data)) {
            Fact ft = (Fact) this.getDynamicFacts().remove(data);
            this.retractFact(ft);
            // Note: not sure if this could potentially lead to a slow memory
            // leak under load if we don't clear the shadow fact for an object
            if (ft.getObjectInstance() == null) {
                ft.clear();
            }
        }
    }

    public void setBindingValue(String key, Object value) {
        if (!this.scopes.isEmpty() && !key.startsWith("*")) {
            this.scopes.peek().setBindingValue(key, value);
        } else {
            getDefglobals().declareDefglobal(key, value);
        }
    }

    public void setCurrentModule(Module mod) {
        this.currentModule = mod;
    }
    
    public void setProfileAssert(boolean profileAssert) {
        this.profileAssert = profileAssert;
    }

    public void setProfileFire(boolean profileFire) {
        this.profileFire = profileFire;
    }

    public void setProfileRetract(boolean profileRetract) {
        this.profileRetract = profileRetract;
    }

    /**
     * the implementation sets the strategy for the current module
     * in focus. If there are multiple modules, it does not set
     * the strategy for the other modules.
     */
    public void setStrategy(Strategy strategy) {
        this.theStrat = strategy;
        this.getCurrentFocus().setStrategy(strategy);
    }
    
    public void setWatchFact(boolean watchFact) {
        this.watchFact = watchFact;
    }

    public void setWatchRules(boolean watchRules) {
        this.watchRules = watchRules;
    }

    /// ----- helper methods that are not defined in WorkingMemory interface ----- ///
    
    protected void assertFactWProfile(Fact fact) throws AssertException {
        ProfileStats.startAssert();
        this.root.assertObject(fact, engine, this);
        ProfileStats.endAssert();
    }

    public boolean containsFact(Fact fact) {
        return this.deffactMap.containsKey(fact.equalityIndex());
    }
    
    /**
     * 
     * @param fact
     * @throws RetractException
     */
    protected void retractFactWProfile(Fact fact) throws RetractException {
        ProfileStats.startRetract();
        this.root.retractObject(fact, engine, this);
        ProfileStats.endRetract();
    }
}
