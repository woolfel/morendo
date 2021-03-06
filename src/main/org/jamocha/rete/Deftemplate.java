/*
 * Copyright 2002-2008 Peter Lin
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Peter Lin Deftemplate is equivalent to CLIPS deftemplate<br/>
 * 
 * Deftemplate contains an array of slots that represent un-ordered facts.
 * Currently, deftemplate does not have a reference to the corresponding Defclass,
 * since many objects in java.beans and java.lang.reflect are not serializable.
 * This means when ever we need to lookup the defclass from the deftemplate, we
 * have to use the String form and do the lookup.
 * 
 * Some general design notes about the current implementation. In the case where
 * a class is declared to create the deftemplate, the order of the slots are
 * based on java Introspection. In the case where an user declares the
 * deftemplate from console or directly, the order is the same as the string
 * equivalent. The current implementation does not address redeclaring a
 * deftemplate for a couple of reasons. The primary one is how does it affect
 * the existing RETE nodes. One possible approach is to always add new slots to
 * the end of the deftemplate and ignore the explicit order. Another is to
 * recompute the deftemplate, binds and all nodes. The second approach is very
 * costly and would make redeclaring a deftemplate undesirable.
 */
public class Deftemplate implements Template, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Slot[] slots;

	private boolean watch = false;

	private String templateName = null;

	private Template parent = null;

	/**
	 * Defclass and Deftemplate are decoupled, so it uses a string
	 * to look up the Defclass rather than have a link to it. This
	 * is because the reflection classes are not serializable.
	 */
	private String defclass = null;
	
	/**
	 * the temporal distance is in milliseconds
	 */
	private int temporalDistance = 0;
	
	private boolean nonTemporalRule = false;

	public Deftemplate(String name, String defclass, Slot[] slots) {
		this.templateName = name;
		this.defclass = defclass;
		this.slots = slots;
	}

	public Deftemplate(String name, String defclass, Slot[] slots,
			Template parent) {
		this(name, defclass, slots);
		this.parent = parent;
	}

	public Deftemplate(String name) {
		this.templateName = name;
	}

	public Deftemplate(String name, Template parent) {
		this.templateName = name;
		this.parent = parent;
	}

	public Deftemplate() {
	}

	/**
	 * checkName will see if the user defined the module to declare the
	 * template. if it is, it will create the module and return it.
	 * 
	 * @param engine
	 * @return
	 */
	public Module checkName(Rete engine) {
		if (this.templateName.indexOf("::") > 0) {
			String[] sp = this.templateName.split("::");
			this.templateName = sp[1];
			return engine.addModule(sp[0], false);
		} else {
			return null;
		}
	}

	public Template getParent() {
		return this.parent;
	}

	public void setParent(Template parent) {
		this.parent = parent;
	}

	/**
	 * return whether the deftemplate should be watched
	 * 
	 * @return
	 */
	public boolean getWatch() {
		return this.watch;
	}

	/**
	 * set whether the deftemplate should be watched
	 * 
	 * @param watch
	 */
	public void setWatch(boolean watch) {
		this.watch = watch;
	}

	/**
	 * the template name is an alias for an object
	 * 
	 * @param name
	 */
	public String getName() {
		return this.templateName;
	}

	/**
	 * Get the class the deftemplate represents
	 * 
	 * @return
	 */
	public String getClassName() {
		return this.defclass;
	}

	/**
	 * Return the number of slots in the deftemplate
	 * 
	 * @return
	 */
	public int getNumberOfSlots() {
		return this.slots.length;
	}

	/**
	 * Return all the slots
	 * 
	 * @return
	 */
	public Slot[] getAllSlots() {
		return this.slots;
	}

	public int getSlotsUsed() {
		int used = 0;
		for (int idx=0; idx < slots.length; idx++) {
			if (slots[idx].getNodeCount() > 0) {
				used++;
			}
		}
		return used;
	}
	
	/**
	 * A convienance method for finding the slot matching the String name.
	 * 
	 * @param name
	 * @return
	 */
	public Slot getSlot(String name) {
		for (int idx = 0; idx < this.slots.length; idx++) {
			if (this.slots[idx].getName().equals(name)) {
				return this.slots[idx];
			}
		}
		return null;
	}

	/**
	 * get the Slot at the given column id
	 * 
	 * @param id
	 * @return
	 */
	public Slot getSlot(int id) {
		return this.slots[id];
	}

	/**
	 * Look up the column index of the slot
	 * 
	 * @param name
	 * @return
	 */
	public int getColumnIndex(String name) {
		for (int idx = 0; idx < this.slots.length; idx++) {
			if (this.slots[idx].getName().equals(name)) {
				return idx;
			}
		}
		return -1;
	}

	/**
	 * convienance method for incrementing the column's use count.
	 * @param name
	 */
	public void incrementColumnUseCount(String name) {
		for (int idx = 0; idx < this.slots.length; idx++) {
			if (this.slots[idx].getName().equals(name)) {
				this.slots[idx].incrementNodeCount();
			}
		}
	}

	public int getTemporalDistance() {
		return temporalDistance;
	}

	public void setTemporalDistance(int temporalDistance) {
		this.temporalDistance = temporalDistance;
	}
	
	public boolean getNonTemporalRules() {
		return this.nonTemporalRule;
	}
	
	public void setNonTemporalRules(boolean value) {
		this.nonTemporalRule = value;
	}
	
	/**
	 * Method will create a Fact from the given object instance
	 * 
	 * @param data
	 * @return
	 */
	public Fact createFact(Object data, Defclass clazz, long id) {
		// first we clone the slots
		BaseSlot[] values = cloneAllSlots();
		// now we set the values
		for (int idx = 0; idx < values.length; idx++) {
			Object val = clazz.getSlotValue(idx, data);
			if (val == null) {
				values[idx].value = Constants.NIL_SYMBOL;
			} else {
				values[idx].value = val;
			}
		}
		Deffact newfact = new Deffact(this, data, values, id);
		return newfact;
	}

	public Fact createTemporalFact(Object data, Defclass clazz, long id) {
		// first we clone the slots
		BaseSlot[] values = cloneAllSlots();
		// now we set the values
		for (int idx = 0; idx < values.length; idx++) {
			Object val = clazz.getSlotValue(idx, data);
			if (val == null) {
				values[idx].value = Constants.NIL_SYMBOL;
			} else {
				values[idx].value = val;
			}
		}
		TemporalDeffact newfact = new TemporalDeffact(this, data, values, id);
		return newfact;
	}
	
	/**
	 * Method takes a list of Slots and creates a deffact from it.
	 * 
	 * @param data
	 * @param id
	 * @return
	 */
	public Fact createFact(List<?> data, long id) {
		BaseSlot[] values = cloneAllSlots();
		Iterator<?> itr = data.iterator();
		while (itr.hasNext()) {
			Slot s = (Slot) itr.next();
			for (int idx = 0; idx < values.length; idx++) {
				if (values[idx].getName().equals(s.getName())) {
					if (s.value == null) {
						values[idx].value = Constants.NIL_SYMBOL;
					} else
					if (values[idx].getValueType() == Constants.STRING_TYPE
							&& !(s.value instanceof BoundParam)) {
						values[idx].value = s.value.toString();
					} else {
						values[idx].value = s.value;
					}
				}
			}
		}
		Deffact newfact = new Deffact(this, null, values, id);
		// we call this to create the string used to map the fact.
		newfact.equalityIndex();
		return newfact;
	}
	
	public Fact createFact(Object[] data, long id) {
		BaseSlot[] values = cloneAllSlots();
        ArrayList<Slot> bslots = new ArrayList<Slot>();
        boolean hasbinding = false;
		for (int idz=0; idz < data.length; idz++) {
			Slot s = (Slot) data[idz];
			for (int idx = 0; idx < values.length; idx++) {
                if (values[idx].getName().equals(s.getName())) {
                    if (s instanceof MultiSlot && !(s.value instanceof BoundParam)) {
                        // since the value is multislot, we have to
                        // check for boundparams
                        MultiSlot ms = (MultiSlot)s;
                        Object[] mvals = ms.getValue();
                        for (int mdx=0; mdx < mvals.length; mdx++) {
                            if (mvals[mdx] instanceof BoundParam) {
                                bslots.add((Slot) ms.clone());
                                hasbinding = true;
                                break;
                            }
                        }
                        values[idx].value = s.value;
                    } else {
                        if (s.value == null) {
                            values[idx].value = Constants.NIL_SYMBOL;
                        } else if (values[idx].getValueType() == Constants.STRING_TYPE
                                && !(s.value instanceof BoundParam)) {
                            values[idx].value = s.value.toString();
                        } else if (s.value instanceof BoundParam) {
                            values[idx].value = s.value;
                            bslots.add((Slot) s.clone());
                            hasbinding = true;
                        } else {
                            values[idx].value = s.value;
                        }
                    }
                    break;
                }
            }
		}
		Deffact newfact = new Deffact(this, null, values, id);
        if (hasbinding) {
            Slot[] slts2 = new Slot[bslots.size()];
            newfact.boundSlots = bslots.toArray(slts2);
            newfact.hasBinding = true;
        }
		// we call this to create the string used to map the fact.
		newfact.equalityIndex();
		return newfact;
	}

    public Fact createTemporalFact(Object[] data, long id) {
    	BaseSlot[] values = cloneAllSlots();
        long effective = 0;
        long expire = 0;
        String source = "";
        String service = "";
        int valid = 0;
        for (int idz=0; idz < data.length; idz++) {
            Slot s = (Slot) data[idz];
            // check to see if the slot is a temporal fact attribute
            if (isTemporalAttribute(s)) {
            	if (s.getName().equals(TemporalFact.EFFECTIVE)) {
            		effective = ((BigDecimal)s.getValue()).longValue();
            	} else if (s.getName().equals(TemporalFact.EXPIRATION)) {
                    expire = ((BigDecimal)s.getValue()).longValue();
                } else if (s.getName().equals(TemporalFact.SERVICE_TYPE)) {
                    service = (String)s.getValue();
                } else if (s.getName().equals(TemporalFact.SOURCE)) {
                    source = (String)s.getValue();
                } else if (s.getName().equals(TemporalFact.VALIDITY)) {
                    valid = ((BigDecimal)s.getValue()).intValue();
                }
            } else {
                for (int idx = 0; idx < values.length; idx++) {
                    if (values[idx].getName().equals(s.getName())) {
                        if (s.value == null) {
                            values[idx].value = Constants.NIL_SYMBOL;
                        } else
                        if (values[idx].getValueType() == Constants.STRING_TYPE
                                && !(s.value instanceof BoundParam)) {
                            values[idx].value = s.value.toString();
                        } else if (s.value instanceof BoundParam) {
                            values[idx].value = s.value;
                        } else {
                            values[idx].value = s.value;
                        }
                    }
                }
            }
        }
        TemporalDeffact newfact = new TemporalDeffact(this, null, values, id);
        // we call this to create the string used to map the fact.
        newfact.setEffectiveTime(effective);
        newfact.setExpirationTime(expire);
        newfact.setServiceType(service);
        newfact.setSource(source);
        newfact.setValidity(valid);
        newfact.equalityIndex();
        return newfact;
    }
    
    public static boolean isTemporalAttribute(Slot s) {
        if (s.getName().equals(TemporalFact.EFFECTIVE)
        		|| s.getName().equals(TemporalFact.EXPIRATION)
                || s.getName().equals(TemporalFact.SERVICE_TYPE)
                || s.getName().equals(TemporalFact.SOURCE)
                || s.getName().equals(TemporalFact.VALIDITY)) {
            return true;
        } else {
            return false;
        }
    }
    
	/**
	 * clone the slots
	 * 
	 * @return
	 */
	public BaseSlot[] cloneAllSlots() {
		Slot[] cloned = new Slot[this.slots.length];
		for (int idx = 0; idx < cloned.length; idx++) {
			cloned[idx] = (Slot) this.slots[idx].clone();
		}
		return cloned;
	}

	/**
	 * If any slot has a usecount greater than 0, we return true.
	 */
	public boolean inUse() {
		for (int idx=0; idx < this.slots.length; idx++) {
			if (this.slots[idx].getNodeCount() > 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Method will return a string format with the int type code for the slot
	 * type
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(" + this.templateName + " ");
		for (int idx = 0; idx < this.slots.length; idx++) {
			buf.append("("
					+ this.slots[idx].getName()
					+ " (type "
					+ ConversionUtils.getTypeName(this.slots[idx]
							.getValueType()) + ") ) ");
		}
		if (this.defclass != null) {
			buf.append("[" + this.defclass + "] ");
		}
		buf.append(")");
		return buf.toString();
	}

	/**
	 * Method will generate a pretty printer format of the Deftemplate
	 * 
	 * @return
	 */
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(" + this.templateName + Constants.LINEBREAK);
		for (int idx = 0; idx < this.slots.length; idx++) {
			buf.append("  ("
					+ this.slots[idx].getName()
					+ " (type "
					+ ConversionUtils.getTypeName(this.slots[idx]
							.getValueType()) + ") " 
					+ "(distinct-count " + this.slots[idx].getDistinctCount() + ") )" + Constants.LINEBREAK);
		}
		if (this.defclass != null) {
			buf.append("[" + this.defclass + "] ");
		}
		buf.append(")");
		if (this.temporalDistance > 0) {
			buf.append(" temporal distance: " + this.temporalDistance + " ms");
		}
		return buf.toString();
	}

	/**
	 * TODO - need to finish implementing this
	 */
	public Deftemplate cloneDeftemplate() {
		Deftemplate dt = new Deftemplate(this.templateName, this.defclass,
				this.slots);

		return dt;
	}
}
