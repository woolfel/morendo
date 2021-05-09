/*
 * Copyright 2002-2009 Peter Lin
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

import java.util.HashMap;
import java.util.Map;

public class CubeFact implements Fact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Template template = null;
    protected Object objInstance;
    protected BaseSlot[] slots = null;
    protected long id;
	private long timeStamp = 0;
	protected boolean hasBinding = false;
	private EqualityIndex Eindex = null;
	private Map slotMap = null;
	
	public CubeFact(Template template, Object instance, BaseSlot[] values, long id) {
		this.template = template;
		this.objInstance = instance;
		this.slots = values;
		this.id = id;
		this.timeStamp = System.currentTimeMillis();
		slotMap = new HashMap(values.length);
		mapSlots();
	}

	protected void mapSlots() {
		for (int idx=0; idx < slots.length; idx++) {
			slotMap.put(this.slots[idx].getName(), slots[idx]);
		}
	}
	
	public void clear() {
		this.template = null;
		this.objInstance = null;
		this.slots = null;
		this.id = 0;
		this.timeStamp = 0;
		slotMap.clear();
	}

	public EqualityIndex equalityIndex() {
		if (this.Eindex == null) {
			this.Eindex = new EqualityIndex(this);
		}
		return this.Eindex;
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

	public Template getDeftemplate() {
		return this.template;
	}

	public long getFactId() {
		return this.id;
	}

	public Object getObjectInstance() {
		return this.objInstance;
	}

	public int getSlotId(String name) {
		BaseSlot s = (BaseSlot)slotMap.get(name);
		return s.getId();
	}

	public Object getSlotValue(int id) {
		BaseSlot s = this.slots[id];
		if (s instanceof DimensionSlot) {
			return ((DimensionSlot)s).getDimension();
		} else {
			return ((MeasureSlot)s).getDefmeasure();
		}
	}

	public void resetID(Fact fact) {
		this.id = fact.getFactId();
	}

	public void setFactId(Rete engine) {
		if (this.id == -1) {
			this.id = engine.nextFactId();
		}
	}

	public long timeStamp() {
		return this.timeStamp;
	}

	/**
	 * the method generates the definition of the Cube and doesn't
	 * populate it with actual values. Since a cube could have a lot
	 * of data, populating it with all the data would be huge. That
	 * isn't practical.
	 */
	public String toFactString() {
		StringBuffer buf = new StringBuffer();
		buf.append("f-" + id + " (" + this.template.getName());
		if (this.slots.length > 0) {
			buf.append(" ");
		}
		for (int idx = 0; idx < this.slots.length; idx++) {
			buf.append("(" + this.slots[idx].getName() + " ");
			if (slots[idx] instanceof DimensionSlot) {
				buf.append("Dimension");
			} else if (slots[idx] instanceof MeasureSlot) {
				buf.append("Measure");
			}
			buf.append(") ");
		}
		buf.append(")");
		return buf.toString();
	}

	/**
	 * method is not implemented. It doesn't apply to CubeFact
	 */
	public void updateSlots(Rete engine, BaseSlot[] slots) {
	}

	/**
	 * Since a CubeFact wraps a Cube instance, they are considered
	 * equal if the underlying Cube is the same. Not it doesn't
	 * compare the slots like deffact.
	 */
	public boolean slotEquals(Fact fact) {
		if (fact instanceof CubeFact) {
			return ((CubeFact)fact).getObjectInstance() == this.objInstance;
		} else {
			return false;
		}
	}
	
	public ResultsetFact createResultsetFact(Rete engine) {
		BaseSlot[] newSlots = new BaseSlot[this.slots.length];
		for (int idx=0; idx < slots.length; idx++) {
			Slot s = new Slot();
			newSlots[idx] = s;
			s.setName( slots[idx].getName() );
			s.setId( slots[idx].getId() );
		}
		ResultsetFact fact = new ResultsetFact(this.template, this.objInstance, newSlots, engine.nextFactId());
		return fact;
	}
}
