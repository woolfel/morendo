/*
 * Copyright 2002-2009 Jamocha
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

import java.util.ArrayList;

import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 * 
 * Deffact is a concrete implementation of Fact interface. It is
 * equivalent to deffact in CLIPS.
 */
public class Deffact implements Fact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Template deftemplate = null;

    protected Object objInstance;

    protected BaseSlot[] slots = null;

    protected BaseSlot[] boundSlots = null;

	/**
	 * the Fact id must be unique, since we use it for the indexes
	 */
    protected long id;

	private long timeStamp = 0;

	protected boolean hasBinding = false;

	private EqualityIndex Eindex = null;

	/**
	 * this is the default constructor
	 * @param instance
	 * @param values
	 */
	public Deffact(Template template, Object instance, BaseSlot[] values, long id) {
		this.deftemplate = template;
		this.objInstance = instance;
		this.slots = values;
		this.id = id;
		this.timeStamp = System.currentTimeMillis();
	}

	/**
	 * 
	 * @param util
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void compileBinding(Rule util) {
		ArrayList list = new ArrayList();
		for (int idx = 0; idx < this.slots.length; idx++) {
			if (this.slots[idx].value instanceof BoundParam) {
				this.hasBinding = true;
				list.add(this.slots[idx]);
				BoundParam bp = (BoundParam) this.slots[idx].value;
				Binding bd = util.getBinding(bp.getVariableName());
				if (bd != null) {
					bp.rowId = bd.getLeftRow();
					bp.column = bd.getLeftIndex();
				}
			}
		}
		if (list.size() > 0) {
			this.boundSlots = (Slot[]) list.toArray(new Slot[list.size()]);
		}
	}

	/**
	 * In some cases, a deffact may have bindings. This is a design choice. When
	 * rules are parsed and compiled, actions that assert facts are converted to
	 * Deffact instances with BoundParam for the slot value.
	 * @return
	 */
	public boolean hasBinding() {
		return this.hasBinding;
	}
	
	public void resolveValues(Rete engine, Fact[] triggerFacts) {
		for (int idx = 0; idx < this.boundSlots.length; idx++) {
            if ((this.boundSlots[idx] instanceof MultiSlot) && !(this.boundSlots[idx].value instanceof BoundParam)) {
                // for multislot we have to resolve each slot
                Object[] mvals = ((MultiSlot)this.boundSlots[idx]).getValue();
                for (int mdx=0; mdx < mvals.length; mdx++) {
                    if (mvals[mdx] instanceof BoundParam) {
                        BoundParam bp = (BoundParam)mvals[mdx];
                        bp.setResolvedValue(engine.getBinding(bp.getVariableName()));
                    }
                }
            } else if (this.boundSlots[idx].value instanceof BoundParam) {
				BoundParam bp = (BoundParam) this.boundSlots[idx].value;
				if (bp.column > -1) {
					bp.setFact(triggerFacts);
				} else {
					bp.setResolvedValue(engine.getBinding(bp
						.getVariableName()));
				}
			}
		}
	}

	/**
	 * Method returns the value of the given slot at the
	 * id.
	 * @param id
	 * @return
	 */
	public Object getSlotValue(int id) {
		return this.slots[id].value;
	}

	/*
	 * *
	 * Method will iterate over the slots until finds the match.
	 * If no match is found, it return -1.
	 */
	public int getSlotId(String name) {
		int col = -1;
		for (int idx = 0; idx < slots.length; idx++) {
			if (slots[idx].getName().equals(name)) {
				col = idx;
				break;
			}
		}
		return col;
	}

	/**
	 * If the fact is a shadow fact, it will return the
	 * object instance. If the fact is just a deffact
	 * and isn't a shadow fact, it return null.
	 * @return
	 */
	public Object getObjectInstance() {
		return this.objInstance;
	}

	/**
	 * Method will return the fact in a string format.
	 * @return
	 */
	public String toFactString() {
		StringBuffer buf = new StringBuffer();
		buf.append("f-" + id + " (" + this.deftemplate.getName());
		if (this.slots.length > 0) {
			buf.append(" ");
		}
		for (int idx = 0; idx < this.slots.length; idx++) {
			buf.append("(" + this.slots[idx].getName() + " "
					+ ConversionUtils.formatSlot(this.slots[idx].value)
					+ ") ");
		}
		buf.append(")");
		return buf.toString();
	}

	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(" + this.deftemplate.getName());
		if (this.slots.length > 0) {
			buf.append(" ");
		}
		for (int idx = 0; idx < this.slots.length; idx++) {
			if (this.slots[idx].value instanceof BoundParam) {
				BoundParam bp = (BoundParam) this.slots[idx].value;
				buf.append("(" + this.slots[idx].getName() + " ?"
						+ bp.getVariableName() + ") ");
			} else {
				buf.append("("
						+ this.slots[idx].getName()
						+ " "
						+ ConversionUtils
								.formatSlot(this.slots[idx].value) + ") ");
			}
		}
		buf.append(")");
		return buf.toString();
	}

	/**
	 * Returns the string format for the fact without the fact-id. this is used
	 * to make sure that if an user asserts an equivalent fact, we can easily
	 * check it.
	 * @return
	 */
	public EqualityIndex equalityIndex() {
		if (this.Eindex == null) {
			this.Eindex = new EqualityIndex(this);
		}
		return this.Eindex;
	}

	/**
	 * this is used by the EqualityIndex class
	 * @return
	 */
	public int slotHash() {
		int hash = 0;
		for (int idx = 0; idx < this.slots.length; idx++) {
            if (this.slots[idx] instanceof MultiSlot) {
                hash += this.slots[idx].getName().hashCode()
                + ((MultiSlot)this.slots[idx]).valueHash();
            } else {
                hash += this.slots[idx].getName().hashCode()
                + this.slots[idx].value.hashCode();
            }
		}
		if (this.objInstance != null) {
			hash += this.objInstance.hashCode();
		}
		return hash;
	}

	/**
	 * Return the long factId
	 */
	public long getFactId() {
		return this.id;
	}

	/**
	 * if the factId is -1, the fact will get will the next fact id
	 * from Rete and set it. Otherwise, the fact will use the same one.
	 * @param engine
	 */
	public void setFactId(Rete engine) {
		if (this.id == -1) {
			this.id = engine.nextFactId();
		}
	}

	/**
	 * this is used to reset the id, in the event an user tries to
	 * assert the same fact again, we reset the id to the existing one.
	 * @param fact
	 */
	public void resetID(Fact fact) {
		this.id = fact.getFactId();
	}

	/**
	 * update the slots
	 */
	public void updateSlots(Rete engine, BaseSlot[] updates) {
		for (int idx = 0; idx < updates.length; idx++) {
			BaseSlot uslot = updates[idx];
			if (uslot.value instanceof BoundParam) {
				BoundParam bp = (BoundParam) uslot.value;
				Object val = engine.getBinding(bp.getVariableName());
				this.slots[uslot.getId()].value = val;
			} else {
				this.slots[uslot.getId()].value = uslot.value;
			}
		}
        this.timeStamp = System.currentTimeMillis();
        this.Eindex = null;
	}

	/**
	 * Return the deftemplate for the fact
	 */
	public Template getDeftemplate() {
		return this.deftemplate;
	}

	/**
	 * the implementation returns nano time
	 */
	public long timeStamp() {
		return this.timeStamp;
	}

	/**
	 * the current implementation only compares the values, since the slot
	 * names are equal. It would be a waste of time to compare the slot
	 * names. The exception to the case is when a deftemplate is changed.
	 * Since that feature isn't supported yet, it's currently not an issue.
	 * Even if updating deftemplates is added in the future, the deffacts
	 * need to be updated. If the deffacts weren't updated, it could lead
	 * to NullPointerExceptions.
	 * @param fact
	 * @return
	 */
	public boolean slotEquals(Fact fact) {
		boolean eq = true;
		BaseSlot[] cslots = ((Deffact)fact).slots;
		for (int idx = 0; idx < this.slots.length; idx++) {
			if (!this.slots[idx].value.equals(cslots[idx].value)) {
				eq = false;
				break;
			}
		}
		return eq;
	}

	/**
	 * Convenience method for cloning a fact. If a slot's value is a BoundParam,
	 * the cloned fact uses the value of the BoundParam.
	 * @return
	 */
	public Deffact cloneFact() {
		Deffact newfact = new Deffact(deftemplate, this.objInstance,
				(BaseSlot[])deftemplate.cloneAllSlots(), -1);
		BaseSlot[] slts = newfact.slots;
		for (int idx = 0; idx < slts.length; idx++) {
			// probably need to revisit this and make sure
			if (this.slots[idx].value instanceof BoundParam) {
				if (slts[idx].getValueType() == Constants.STRING_TYPE) {
					slts[idx].value = ((BoundParam) this.slots[idx].value)
							.getValue().toString();
				} else {
					
					/* The below does not work, at least in OpenJDK 11. 
					   
					   slts[idx].value = ((BoundParam) this.slots[idx].value)
							.getValue(); 
					
					   However, the below does - although the two should be equivalent, 
					   for some reason the intermediate object seems to be rewuired. 
					   */
					
					Object v = this.slots[idx].value;
					slts[idx].value = ((BoundParam)v).getValue();
				}
			} else if (this.slots[idx] instanceof MultiSlot) {
                // it's multislot so we have to replace the bound values
                // correctly
                MultiSlot ms = (MultiSlot)this.slots[idx];
                Object[] sval = ms.getValue();
                Object[] mval = new Object[ms.getValue().length];
                for (int mdx=0; mdx < mval.length; mdx++) {
                    Object v = sval[mdx];
                    if (v instanceof BoundParam) {
                        mval[mdx] = ((BoundParam)v).getValue();
                    } else {
                        mval[mdx] = v;
                    }
                }
                slts[idx].value = mval;
            } else  {
				slts[idx].value = this.slots[idx].value;
			}
		}
		return newfact;
	}


	/**
	 * this will make sure the fact is GC immediately. Note for shadown facts of
	 * objects, retract method does not automatically clear the references. this is
	 * so that functions can still use the shadow in the action of the rule after
	 * it is retracted from the working memory.
	 * Clear will set the id, slots, template, and object instance to null. Once
	 * clear is called, the deffact is cleared of all data.
	 */
	public void clear() {
		this.deftemplate = null;
		this.objInstance = null;
		this.slots = null;
		this.id = 0;
		this.timeStamp = 0;
	}
}
