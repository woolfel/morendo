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
package org.jamocha.rete;

public class CubeTemplate implements Template {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cube cube = null;
	private String templateName;
	private String defclass = null;
	protected BaseSlot[] slots;
	private boolean watch = false;
	private int temporalDistance = 0;

	public CubeTemplate(String name, String defclass, BaseSlot[] slots) {
		this.templateName = name;
		this.defclass = defclass;
		this.slots = slots;
	}

	public Fact createFact(Object data, Defclass clazz, long id) {
		BaseSlot[] values = cloneAllSlots();
		CubeFact newfact = new CubeFact(this, data, values, id);
		return newfact;
	}

	/**
	 * NOT implemented. It doesn't apply
	 */
	public Fact createTemporalFact(Object data, Defclass clazz, long id) {
		return null;
	}

	public Cube getCube() {
		return this.cube;
	}
	
	public BaseSlot[] getAllSlots() {
		return this.slots;
	}

	public String getClassName() {
		return this.defclass;
	}

	public int getColumnIndex(String name) {
		for (int idx = 0; idx < this.slots.length; idx++) {
			if (this.slots[idx].getName().equals(name)) {
				return idx;
			}
		}
		return -1;
	}

	public String getName() {
		return templateName;
	}

	public int getNumberOfSlots() {
		return this.slots.length;
	}

	public Template getParent() {
		return null;
	}

	public BaseSlot getSlot(String name) {
		for (int idx = 0; idx < this.slots.length; idx++) {
			if (this.slots[idx].getName().equals(name)) {
				return this.slots[idx];
			}
		}
		return null;
	}

	public BaseSlot getSlot(int column) {
		return this.slots[column];
	}

	public int getTemporalDistance() {
		return temporalDistance;
	}

	public boolean getWatch() {
		return watch;
	}

	public boolean inUse() {
		for (int idx=0; idx < this.slots.length; idx++) {
			if (this.slots[idx].getNodeCount() > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method does not apply to CubeTemplates
	 */
	public void setParent(Template parent) {
	}

	public void setTemporalDistance(int distance) {
		this.temporalDistance = distance;
	}

	/**
	 * not implemented. doesn't apply to cubes
	 */
	public void setNonTemporalRules(boolean value) {
	}
	
	public boolean getNonTemporalRules() {
		return false;
	}
	
	public void setWatch(boolean watch) {
		this.watch = watch;
	}

	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(" + this.templateName + Constants.LINEBREAK);
		for (int idx = 0; idx < this.slots.length; idx++) {
			buf.append("  ("
					+ this.slots[idx].getName()
					+ " (type ");
			if (slots[idx] instanceof DimensionSlot) {
				buf.append( ((DimensionSlot)slots[idx]).toPPString() + "))" + Constants.LINEBREAK);
			} else if (slots[idx] instanceof MeasureSlot) {
				buf.append( ((MeasureSlot)slots[idx]).toPPString() + "))" + Constants.LINEBREAK);
			} else if (slots[idx] instanceof Slot) {
				buf.append("Object[]))" + Constants.LINEBREAK);
			}
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

	public BaseSlot[] cloneAllSlots() {
		BaseSlot[] cloned = new BaseSlot[this.slots.length];
		for (int idx = 0; idx < cloned.length; idx++) {
			cloned[idx] = (BaseSlot) this.slots[idx].clone();
		}
		return cloned;
	}

	public void incrementColumnUseCount(String name) {
		this.getSlot(name).incrementNodeCount();
	}

	public int getSlotsUsed() {
		return this.slots.length;
	}

}
