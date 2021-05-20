/*
 * Copyright 2002-2006 Peter Lin
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.Constants;

/**
 * @author Peter Lin
 *
 * BoundConstraint is a basic implementation of Constraint interface
 * for bound constraints. When a rule declares a slot as a binding,
 * a BoundConstraint is used.
 */
public class BoundConstraint implements Constraint {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * the name is the slot name
     */
    protected String name;
    /**
     * In the case of BoundConstraints, the value is the name of
     * the variable given my the user
     */
    protected Object value;
    
    protected boolean isObjectBinding = false;
    protected boolean negated = false;
    protected boolean firstDeclaration = false;
    /**
     * if the binding is for a multislot, it should be
     * set to true. by default, it is false.
     */
    protected boolean isMultislot = false;
    protected boolean intraFactJoin = false;
    @SuppressWarnings("rawtypes")
	protected List ifjoins = new ArrayList();
    /**
     * Be default a variable binding is accessible from the action
     * of the rule unless it's declared by a conditional element
     * that does not propagate changes down the RETE network.
     */
    protected boolean bindableConstraint = true;
    
	/**
	 * 
	 */
	public BoundConstraint() {
		super();
	}

    public BoundConstraint(String name, boolean isObjBind) {
        super();
        setName(name);
        this.isObjectBinding = isObjBind;
    }
    
	/**
     * The name of the slot or object field.
	 */
	public String getName() {
		return name;
	}

	/**
     * the name is the name of the slot or object field.
	 */
	public void setName(String name) {
        if (name.startsWith("?")) {
            this.name = name.substring(1);
        } else {
            this.name = name;
        }
	}

	/**
     * The value is the name of the variable. In the case of CLIPS,
     * if the rule as "?name", the value returned is "name" without
     * the question mark prefix.
	 */
	public Object getValue() {
		return value;
	}

	/** 
     * The input parameter should be a string and it should be
     * the name of the variable. Make sure to parse out the
     * prefix. For example, CLIPS uses "?" to denote a variable.
	 */
	public void setValue(Object val) {
		this.value = val;
	}
    
    public String getVariableName() {
        return (String)this.value;
    }
    
    /**
     * Set the constraint to true if the binding is for an object or
     * a deffact.
     * @param obj
     */
    public void setIsObjectBinding(boolean obj) {
        this.isObjectBinding = obj;
    }
    
    /**
     * if the binding is to an object or deffact, the method will
     * return true.
     * @return
     */
    public boolean getIsObjectBinding() {
        return this.isObjectBinding;
    }

    /**
     * if the binding is for a multislot, it will return true.
     * by default is is false.
     * @return
     */
    public boolean isMultislot() {
    	return this.isMultislot;
    }
    
    /**
     * only set the multislot to true if the slot is defined
     * as a multislot
     * @param multi
     */
    public void setIsMultislot(boolean multi) {
    	this.isMultislot = multi;
    }
    
    /**
	 * if the literal constraint is negated with a "~" tilda, call
	 * the method pass true.
	 * @param negate
	 */
	public void setNegated(boolean negate) {
		this.negated = negate;
	}
	
	/**
	 * if the literal constraint is negated, the method returns true
	 * @return
	 */
	public boolean getNegated() {
		return this.negated;
	}
	
	/**
	 * NOTCE, Exists CE and Multiple CE should set binding to false,
	 * since the action of the rule cannot bind to the declared
	 * variables.
	 * @param exists
	 */
	public void setBindableConstraint(boolean exists) {
		this.bindableConstraint = exists;
	}
	
	/**
	 * If the constraint is accessible from the action of the rule,
	 * the variable should be set to bindable.
	 * @return
	 */
	public boolean getBindableConstraint() {
		return this.bindableConstraint;
	}
	
	public void setFirstDeclaration(boolean first) {
		this.firstDeclaration = first;
	}
	
	/**
	 * by default the method returns false, unless it is set to true
	 * @return
	 */
	public boolean firstDeclaration() {
		return this.firstDeclaration;
	}
	
    public boolean hasIntraFactJoin() {
        return intraFactJoin;
    }

    public void setIntraFactJoin(boolean intraFactJoin) {
        this.intraFactJoin = intraFactJoin;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void addIntrFactJoin(List list) {
        if (list.size() > 0) {
            Iterator itr = list.iterator();
            while (itr.hasNext()) {
                BoundConstraint bc = (BoundConstraint)itr.next();
                bc.setName(this.name);
                ifjoins.add(bc);
            }
        }
    }
    
    public BoundConstraint getFirstIFJ() {
        return (BoundConstraint)this.ifjoins.get(0);
    }
    
	/**
	 * returns the constriant in a pretty printer format
	 */
	public String toPPString() {
		if (this.isMultislot) {
			return "    (" + this.name + " $?" + this.value.toString() +
			")" + Constants.LINEBREAK;
        } else if (this.negated) {
            return "    (" + this.name + " ~?" + this.value.toString() +
            ")" + Constants.LINEBREAK;
		} else {
			return "    (" + this.name + " ?" + this.value.toString() +
			")" + Constants.LINEBREAK;
		}
	}
	
	public String toFactBindingPPString() {
		return "  ?" + this.value.toString() + " <-";
	}
}
