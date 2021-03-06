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
package org.jamocha.rete;

/**
 * @author Peter Lin
 * 
 * MultiSlot always returns Constants.ARRAY_TYPE. It is the class for array
 * types.
 */
public class MultiSlot extends Slot {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected int type = Constants.ARRAY_TYPE;

	/**
	 * 
	 */
	public MultiSlot() {
		super();
	}

	public MultiSlot(String name) {
		super.setName(name);
	}

	public MultiSlot(String name, Object[] value) {
		super.setName(name);
		this.value = value;
	}

	public void setValue(Object[] val) {
		this.value = val;
	}

	public Object[] getValue() {
        if (this.value != Constants.NIL_SYMBOL) {
            return (Object[])this.value;
        } else {
            return new Object[]{this.value};
        }
	}

	/**
	 * In some cases, a deftemplate can be define with a default value.
	 * 
	 * @param value
	 */
	public void setDefaultValue(Object[] value) {
		this.value = value;
	}

	/**
	 * We override the base implementation and do nothing, since a multislot is
	 * an object array. That means it is an array type
	 */
	public void setValueType(int type) {
	}
	
	/**
	 *  Always return ARRAY_TYPE if asked 
	 */
	public int getValueType() {
		return Constants.ARRAY_TYPE;
	}

	public String valueToString() {
		return this.value.toString();
	}

    /**
     * we have to calculate the value hash, for the equalityIndex
     * @return
     */
    public int valueHash() {
        int hash = 0;
        if (!this.value.equals(Constants.NIL_SYMBOL)) {
        	if (this.value instanceof Object[]) {
                Object[] val = (Object[])this.value;
                for (int idx=0; idx < val.length; idx++) {
                    hash += val[idx].hashCode();
                }
        	}
        }
        return hash;
    }
    
	/**
	 * method returns a clone and set id, name and value.
	 */
	public Object clone() {
		MultiSlot newms = new MultiSlot();
		newms.setId(this.getId());
		newms.setName(this.getName());
		newms.setValue((Object[]) this.getValue());
		return newms;
	}

}
