/*
 * Copyright 2002-2008 Peter Lin
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

import java.io.Serializable;

/**
 * @author Peter Lin
 *
 * NSFact stands for Non-Shadow Fact. NSFact is different than
 * Deffact which is a shadow fact for an object instance. NSFact
 * should only be used for cases where fact modification isn't
 * needed. In all cases where the application expects to modify
 * facts in the reasoning cycle, Deffacts should be used. Using
 * NSFact for situations where facts are modified or asserted
 * during the reasoning cycle will produce unreliable results.
 * It will violate the principle of truth maintenance, which
 * means the final result is true and accurate.
 * 
 * Cases where NSFact is useful are routing scenarios where the
 * facts are filtered to determien where they should go. In
 * cases like that, the consequence produces results which are
 * used by the application, but aren't used by the rule engine
 * for reasoning.
 */
public class NSFact implements Fact, Serializable {

	private static final long serialVersionUID = 1L;
	private Deftemplate deftemplate = null;
    private Defclass dclazz = null;
    private Object objInstance;
    private Slot[] slots = null;
    /**
     * the Fact id must be unique, since we use it for the indexes
     */
    private long id;
    private long timeStamp = 0;

    /**
	 * 
	 */
	public NSFact(Deftemplate template, Defclass clazz,
			Object instance, Slot[] values, long id) {
        this.deftemplate = template;
        this.dclazz = clazz;
        this.objInstance = instance;
        this.slots = values;
        this.id = id;
        this.timeStamp = System.currentTimeMillis();
	}

	/**
     * The implementation gets the Defclass and passes the 
     * objectInstance to invoke the read method.
	 * @see org.jamocha.rete.Fact#getSlotValue(int)
	 */
	public Object getSlotValue(int id) {
        return dclazz.getSlotValue(id,objInstance);
	}

	/**
     * 
	 * @see org.jamocha.rete.Fact#getSlotId(java.lang.String)
	 */
	public int getSlotId(String name) {
        int col = -1;
        for (int idx=0; idx < slots.length; idx++){
            if (slots[idx].getName().equals(name)){
                col = idx;
                break;
            }
        }
        return col;
	}

	/**
	 * The object instance for the fact
	 */
	public Object getObjectInstance() {
		return this.objInstance;
	}

	/**
     * The method will return the Fact as a string
	 */
	public String toFactString() {
        StringBuffer buf = new StringBuffer();
        buf.append("(" + this.deftemplate.getName() + " ");
        for (int idx=0; idx < this.slots.length; idx++){
            buf.append("(" + this.slots[idx].getName() + " ");
            Object value = dclazz.getSlotValue(idx,this.objInstance);
            if (value != null) {
            	buf.append(value.toString());
            } else {
            	buf.append("NIL");
            }
            buf.append(") ");
        }
        buf.append(")");
        return buf.toString();
	}

	/**
	 * Return the unique fact id
	 */
	public long getFactId() {
		return this.id;
	}

    /**
     * Non-Shadow Fact does not implement this, since this method
     * doesn't apply to facts derived from objects.
     */
    public void updateSlots(Rete engine, BaseSlot[] updates) {}
    
	/**
	 * Return the deftemplate for the fact
	 */
	public Deftemplate getDeftemplate() {
		return this.deftemplate;
	}

    /**
     * the implementation returns nano time
     */
    public long timeStamp() {
        return this.timeStamp;
    }
    
	/**
     * clear will set all the references to null. this makes sure
     * objects are GC.
	 */
	public void clear() {
        this.slots = null;
        this.objInstance = null;
        this.deftemplate = null;
        this.id = 0;
	}

    /**
     * non shadow fact does not implement the method, since it
     * doesn't apply.
     */
    public EqualityIndex equalityIndex() {
        return null;
    }

	public int slotHash() {
		return this.objInstance.hashCode();
	}
	
	public void resetID(Fact fact) {
		this.id = fact.getFactId();
	}

	public void setFactId(Rete engine) {
		if (this.id == -1) {
			this.id = engine.nextFactId();
		}
	}
	
	public boolean slotEquals(Fact fact) {
		if (fact instanceof NSFact) {
			return ((NSFact)fact).objInstance == this.objInstance;
		} else {
			return false;
		}
	}
}
