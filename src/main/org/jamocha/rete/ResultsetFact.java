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

public class ResultsetFact implements Fact {

	private static final long serialVersionUID = 1L;

	protected Template cubeTemplate = null;
    protected Object objInstance;
    protected BaseSlot[] slots = null;
    protected long id;
	private long timeStamp = 0;
	private EqualityIndex Eindex = null;
	
	public ResultsetFact(Template template, Object instance, BaseSlot[] values, long id) {
		super();
		this.cubeTemplate = template;
		this.objInstance = instance;
		this.slots = values;
		this.id = id;
	}

	public void clear() {
		this.cubeTemplate = null;
		this.objInstance = null;
		this.slots = null;
		this.id = 0;
		this.timeStamp = 0;
	}

	public EqualityIndex equalityIndex() {
		if (this.Eindex == null) {
			this.Eindex = new EqualityIndex(this);
		}
		return this.Eindex;
	}

	public Template getDeftemplate() {
		return this.cubeTemplate;
	}

	public long getFactId() {
		return this.id;
	}

	public Object getObjectInstance() {
		return this.objInstance;
	}

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

	public Object getSlotValue(int id) {
		return slots[id].value;
	}

	public void setSlotValue(int id, Object value) {
		this.slots[id].value = value;
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
		return hash;
	}

	public long timeStamp() {
		return this.timeStamp;
	}

	public String toFactString() {
		StringBuffer buf = new StringBuffer();
		buf.append("f-" + id + " (" + this.cubeTemplate.getName());
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

	/**
	 * Method is not implemented. It doesn't apply
	 */
	public void updateSlots(Rete engine, BaseSlot[] slots) {
	}

	public void setResultsetData(Object[] data) {
		slots[slots.length - 1].value = data;
	}
	
	public Object[] getResultsetData() {
		return (Object[])slots[slots.length - 1].value;
	}
}
