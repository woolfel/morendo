/*
 * Copyright 2002-2009 Jamocha
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
package org.jamocha.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Binding2;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Module;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Scope;
import org.jamocha.rete.Template;

/**
 * @author Peter Lin
 *
 * A basic implementation of the Rule interface
 */
public class Defrule implements Rule, Scope, Serializable {

    protected String name = null;
    protected ArrayList conditions = null;
    protected ArrayList actions = null;
    protected ArrayList modificationActions = null;
	protected ArrayList joins = null;
    protected int salience = 100;
    protected boolean auto = false;
    protected Complexity complex = null;
    protected boolean rememberMatch = true;
    protected boolean hashedMemory = true;
    /**
     * by default noAgenda is false
     */
    protected boolean noAgenda = false;
    protected String version = "";
    protected Module themodule = null;
    protected Map bindValues = new HashMap();
	private LinkedHashMap bindings = new LinkedHashMap();
	private String comment = "";
	/**
	 * by default a rule is active, unless set to false
	 */
	private boolean active = true;
    /**
     * Be default, the rule is set to forward chaining
     */
    protected int direction = Constants.FORWARD_CHAINING;

    /**
     * by default watch is off
     */
    protected boolean watch = false;
    /**
     * default is set to zero
     */
    protected long effectiveDate = 0;
    /**
     * default is set to zero
     */
    protected long expirationDate = 0;
    /**
     * default for temporal activation is false
     */
    protected boolean temporalActivation = false;
    
    protected int costValue = 0;
    
    protected Fact[] triggerFacts = null;
    
	/**
	 * 
	 */
	public Defrule() {
		super();
        this.complex = ComplexityFactory.newInstance();
        this.complex.setRule(this);
        conditions = new ArrayList();
        actions = new ArrayList();
        joins = new ArrayList();
        modificationActions = new ArrayList();
	}

    public Defrule(String name) {
        this();
        setName(name);
    }
    
	/* (non-Javadoc)
	 * @see woolfel.engine.rule.Rule#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rule.Rule#setName()
	 */
	public void setName(String name) {
        this.name = name;
	}

    public void setActive(boolean active) {
    	this.active = active;
    }
    
    /**
     * by default a rule should return true
     * @return
     */
    public boolean isActive() {
    	return this.active;
    }
    
	/* (non-Javadoc)
	 * @see woolfel.engine.rule.Rule#getWatch()
	 */
	public boolean getWatch() {
		return watch;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rule.Rule#setWatch(boolean)
	 */
	public void setWatch(boolean watch) {
        this.watch = watch;
	}
    
    public boolean getAutoFocus() {
        return this.auto;
    }
    
    public void setAutoFocus(boolean auto) {
        this.auto = auto;
    }

    public int getSalience() {
        return this.salience;
    }
    
    public void setSalience(int sal) {
        this.salience = sal;
    }
    
    public String getComment() {
    	return this.comment;
    }
    
    public void setComment(String text) {
    	this.comment = text.substring(1,text.length() -1);
    }

    public Complexity getComplexity() {
        return this.complex;
    }
    
    public void setComplexity(Complexity complexity) {
        this.complex = complexity;
    }

	public long getEffectiveDate() {
		return this.effectiveDate;
	}

	public long getExpirationDate() {
		return this.expirationDate;
	}

	public void setEffectiveDate(long mstime) {
		this.effectiveDate = mstime;
	}

	public void setExpirationDate(long mstime) {
		this.expirationDate = mstime;
	}
	
    public boolean getNoAgenda() {
    	return this.noAgenda;
    }
    
    public void setNoAgenda(boolean agenda) {
    	this.noAgenda = agenda;
    }
    
    public boolean getRememberMatch() {
        return this.rememberMatch;
    }
    
    public void setRememberMatch(boolean remember) {
        this.rememberMatch = remember;
    }
    
	public boolean getHashedMemory() {
		return hashedMemory;
	}

	public void setHashedMemory(boolean hashedMemory) {
		this.hashedMemory = hashedMemory;
	}

    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(String ver) {
    	if (ver != null) {
            this.version = ver;
    	}
    }
    
	public int getCostValue() {
		return costValue;
	}

	public void setCostValue(int costValue) {
		this.costValue = costValue;
	}

    /* (non-Javadoc)
	 * @see woolfel.engine.rule.Rule#addCondition(woolfel.engine.rule.Condition)
	 */
	public void addCondition(Condition cond) {
        conditions.add(cond);
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rule.Rule#addAction(woolfel.engine.rule.Action)
	 */
	public void addAction(Action act) {
        actions.add(act);
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rule.Rule#getConditions()
	 */
	public Condition[] getConditions() {
        Condition[] cond = new Condition[conditions.size()];
        conditions.toArray(cond);
		return cond;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rule.Rule#getActions()
	 */
	public Action[] getActions() {
        Action[] acts = new Action[actions.size()];
        actions.toArray(acts);
		return acts;
	}

    public Action[] getModificationActions() {
        Action[] acts = new Action[modificationActions.size()];
        modificationActions.toArray(acts);
		return acts;
	}

	public void addModificationAction(Action act) {
		this.modificationActions.add(act);
	}

    public void setModule(Module mod) {
    	this.themodule = mod;
    }
    
    public Module getModule() {
    	return this.themodule;
    }
    
    /**
     * add join nodes to the rule
     */
    public void addJoinNode(BaseJoin node) {
        this.joins.add(node);
    }

    /**
     * get the array of join nodes
     */
    public List getJoins() {
        return this.joins;
    }
    
    public BaseNode getLastNode() {
        if (this.joins.size() > 0) {
            return (BaseNode)this.joins.get(this.joins.size() - 1);
        } else if (conditions.size() > 0) {
            // this means there's only 1 ConditionalElement, so the conditions
            // only has 1 element. in all other cases, there will be atleast
            // 1 join node
            Condition c = (Condition)this.conditions.get(0);
            if (c instanceof ObjectCondition) {
                return ((ObjectCondition)c).getLastNode();
            } else if (c instanceof TestCondition) {
                return ((TestCondition)c).getTestNode();
            }
            return null;
        } else {
        	return null;
        }
    }
    
    /**
     * the current implementation simply replaces the existing
     * value if one already exists.
     */
    public void setBindingValue(Object key, Object value) {
    	this.bindValues.put(key,value);
    }

    /**
     * return the value associated with the binding
     */
    public Object getBindingValue(Object key) {
    	Object val = this.bindValues.get(key);
    	if (val == null) {
    		Binding bd = (Binding)this.bindings.get(key);
    		if (bd != null) {
    			Fact left = this.triggerFacts[bd.getLeftRow()];
    			if (bd.getIsObjectVar()) {
    				val = left;
    			} else {
        			val = left.getSlotValue(bd.getLeftIndex());
    			}
    		}
    	}
    	return val;
    }
 
    public void setBindingValue(String name, Object value) {
        this.bindValues.put(name, value);
    }
    
    /**
     * This should be called when the action is being fired. after the rule
     * actions are executed, the trigger facts should be reset. The primary
     * downside of this design decision is it won't work well with multi-
     * threaded parallel execution. Since Sumatra has no plans for implementing
     * parallel execution using multi-threading, the design is not an issue.
     * Implementing multi-threaded parallel execution isn't desirable and
     * has been proven to be too costly. A better approach is to queue assert
     * retract and process them in sequence.
     */
    public void setTriggerFacts(Fact[] facts) {
        this.triggerFacts = facts;
    }
    
    /**
     * reset the trigger facts after all the actions have executed.
     */
    public void resetTriggerFacts() {
        this.triggerFacts = null;
    }
    
    public Fact[] getTriggerFacts() {
    	return this.triggerFacts;
    }

	/**
	 * Method will only add the binding if it doesn't already
	 * exist.
	 * @param bind
	 */
	public void addBinding(String key, Binding bind) {
		if (!this.bindings.containsKey(key)) {
			this.bindings.put(key,bind);
		}
	}
	
	/**
	 * Return the Binding matching the variable name
	 * @param varName
	 * @return
	 */
	public Binding getBinding(String varName) {
		return (Binding)this.bindings.get(varName);
	}

	/**
	 * Get a copy of the Binding using the variable name
	 * @param varName
	 * @return
	 */
	public Binding copyBinding(String varName) {
		Binding b = getBinding(varName);
		if (b != null) {
			Binding b2 = (Binding)b.clone();
			return b2;
		} else {
			return null;
		}
	}
	
	public Binding copyPredicateBinding(String varName, int operator) {
		Object value = this.bindings.get(varName);
		if (value != null) {
			if (value instanceof Binding2) {
				Binding2 b = (Binding2)value;
				Binding2 b2 = new Binding2(operator);
				b2.setLeftRow(b.getLeftRow());
				b2.setLeftIndex(b.getLeftIndex());
	            b2.setVarName(b.getVarName());
	            b2.setQueryValue(b.getQueryValue());
	            b2.setRightIndex(b.getRightIndex());
				return b2;
			} else {
				Binding b = (Binding)value;
				Binding2 b2 = new Binding2(operator);
				b2.setLeftRow(b.getLeftRow());
				b2.setLeftIndex(b.getLeftIndex());
	            b2.setVarName(b.getVarName());
	            b2.setRightIndex(b.getRightIndex());
				return b2;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * The method will return the Bindings in the order they
	 * were added to the utility.
	 * @return
	 */
	public Iterator getBindingIterator() {
		return this.bindings.values().iterator();
	}
	
	/**
	 * Returns the number of unique bindings. If a binding is
	 * used multiple times to join several facts, it is only
	 * counted once.
	 * @return
	 */
	public int getBindingCount() {
		return this.bindings.size();
	}
	
	public void resolveTemplates(Rete engine) {
        this.resolveConditionTemplates(engine, this.getConditions());
	}
    
    private void resolveConditionTemplates(Rete engine, Condition[] cnds) {
        for (int idx=0; idx < cnds.length; idx++) {
            Condition cnd = cnds[idx];
            if (cnd instanceof ObjectCondition) {
                ObjectCondition oc = (ObjectCondition)cnd;
                Template dft = engine.findTemplate(oc.getTemplateName());
                if (dft != null) {
                    oc.setTemplate(dft);
                }
            } else if (cnd instanceof ExistCondition) {
                ExistCondition exc = (ExistCondition) cnd;
                Template dft = engine.findTemplate(exc.getTemplateName());
                if (dft != null) {
                    exc.setTemplate(dft);
                }
            } else if (cnd instanceof TemporalCondition) {
                TemporalCondition tempc = (TemporalCondition)cnd;
                Template dft = engine.findTemplate(tempc.getTemplateName());
                if (dft != null) {
                    tempc.setTemplate(dft);
                }
            } else if (cnd instanceof AndCondition) {
                resolveConditionTemplates(engine, ((AndCondition)cnd).getConditions() );
            }
        }
    }
	
	public void setRuleProperties(List props) {
		Iterator itr = props.iterator();
		while (itr.hasNext()) {
			RuleProperty declaration = (RuleProperty) itr.next();
			if (declaration.getName().equals(RuleProperty.AUTO_FOCUS)) {
				setAutoFocus(declaration.getBooleanValue());
			} else if (declaration.getName().equals(RuleProperty.SALIENCE)) {
				setSalience(declaration.getIntValue());
			} else if (declaration.getName().equals(RuleProperty.VERSION)) {
				setVersion(declaration.getValue());
			} else if (declaration.getName()
					.equals(RuleProperty.REMEMBER_ALPHA)) {
				setRememberMatch(declaration.getBooleanValue());
			} else if (declaration.getName().equals(RuleProperty.NO_AGENDA)) {
				setNoAgenda(declaration.getBooleanValue());
			} else if (declaration.getName().equals(RuleProperty.EFFECTIVE_DATE)) {
				this.effectiveDate = getDateTime(declaration.getValue());
			} else if (declaration.getName().equals(RuleProperty.EXPIRATION_DATE)) {
				this.expirationDate = getDateTime(declaration.getValue());
			} else if (declaration.getName().equals(RuleProperty.TEMPORAL_ACTIVATION)) {
                this.temporalActivation = declaration.getBooleanValue();
            } else if (declaration.getName().equals(RuleProperty.HASHED_MEMORY)) {
            	this.hashedMemory = declaration.getBooleanValue();
            }
		}
		
	}
	
	public static long getDateTime(String date) {
		if (date != null && date.length() > 0) {
			try {
				java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("mm/dd/yyyy HH:mm");
				return df.parse(date).getTime();
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(defrule " + this.name + Constants.LINEBREAK);
		// now print out the rule properties
		buf.append("  (declare (salience " + this.salience + ") (rule-version " +
				this.version + ") (remember-match " + this.rememberMatch + 
				") (effective-date " + this.effectiveDate + ") (expiration-date " +
				this.expirationDate +") (temporal-activation " + this.temporalActivation
                + ") (no-agenda " + this.noAgenda + ") (chaining-direction " + this.direction + ") )" +
				Constants.LINEBREAK);
		for (int idx=0; idx < this.conditions.size(); idx++) {
			Condition c = (Condition)this.conditions.get(idx);
			buf.append(c.toPPString());
		}
		buf.append("=>" + Constants.LINEBREAK);
		// now append the actions
		for (int idx=0; idx < this.actions.size(); idx++) {
			Action ac = (Action)this.actions.get(idx);
			buf.append(ac.toPPString());
		}
		if (this.modificationActions.size() > 0) {
			buf.append("<<=" + Constants.LINEBREAK);
			for (int idx=0; idx < this.modificationActions.size(); idx++) {
				Action ac = (Action)this.modificationActions.get(idx);
				buf.append(ac.toPPString());
			}
		}
		buf.append(")" + Constants.LINEBREAK);
        buf.append(";; topology-cost: " + this.costValue + Constants.LINEBREAK);
		return buf.toString();
	}

	public void clear() {
		Iterator itr = this.conditions.iterator();
		while (itr.hasNext()) {
			Condition cond = (Condition)itr.next();
			cond.clear();
		}
		this.joins.clear();
	}
	
	/**
	 * TODO need to finish implementing the clone method
	 */
	public Object clone() {
		Defrule cl = new Defrule(this.name);
		return cl;
	}

    public boolean isTemporalActivation() {
        return temporalActivation;
    }

    public void setTemporalActivation(boolean temporalActivation) {
        this.temporalActivation = temporalActivation;
    }
}
