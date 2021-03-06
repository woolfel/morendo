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

/**
 * @author Peter Lin
 *
 * BoundParam is a parameter that is a binding. The test node will need to
 * call setFact(Fact[] facts) so the parameter can access the value.
 */
public class BoundParam extends AbstractParam {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * The fact
     */
    protected Fact fact = null;
    
    protected Object resolvedVal = null;
    
    /**
     * Column refers to the column of the fact. the value of the column
     * should be a non-negative integer.
     */
    protected int column = -1;
    
    /**
     * the int value defining the valueType
     */
    protected int valueType = -1;

    /**
     * the row id of the fact as defined by the rule
     */
    protected int rowId = -1;
    
    /**
     * By default the action is assert
     */
    protected int actionType = Constants.ACTION_ASSERT;
    
    /**
     * the name of the variable
     */
    protected String variableName = null;
    /**
     * if the binding is for a multislot, it should be
     * set to true. by default, it is false.
     */
    protected boolean isMultislot = false;
    
    public BoundParam() {
        super();
    }
    
	/**
	 * 
	 */
	public BoundParam(int col, int vType) {
		super();
        this.column = col;
        this.valueType = vType;
        this.objBinding = true;
	}

    public BoundParam(int col, int vType, boolean objBinding) {
        super();
        this.column = col;
        this.valueType = vType;
        this.objBinding = objBinding;
    }
    
    public BoundParam(int row, int col, int vType, boolean obj) {
    	super();
    	this.rowId = row;
    	this.column = col;
    	this.valueType = vType;
    	this.objBinding = obj;
    }
    
    public BoundParam(Fact fact) {
        this.fact = fact;
        this.objBinding = true;
        this.valueType = Constants.FACT_TYPE;
    }
    
    public String getVariableName() {
        return this.variableName;
    }
    
    public void setVariableName(String value) {
    	if (value.substring(0,1).equals("?")) {
            this.variableName = value.substring(1);
    	} else {
            this.variableName = value;
    	}
    }
    
	/**
     * get the value type
	 */
	public int getValueType() {
		return this.valueType;
	}

	public void resolveBinding(Rete engine) {
		if (fact == null && this.variableName != null) {
			this.resolvedVal = engine.getBinding(this.variableName);
		}
	}
	
	/**
     * Get the value of the given slot
	 */
	public Object getValue() {
		if (fact != null) {
			return this.fact.getSlotValue(this.column);
		} else {
			return this.resolvedVal;
		}
	}
    
    /**
     * method will try to resolve the variable and return the value.
     */
    public Object getValue(Rete engine, int valueType) {
    	if (valueType == Constants.OBJECT_TYPE && this.fact != null) {
    		return this.fact.getObjectInstance();
    	} else if (fact != null) {
            return this.fact.getSlotValue(this.column);
        } else {
            return engine.getBinding(this.variableName);
        }
    }
	
	public void setResolvedValue(Object val) {
		this.resolvedVal = val;
	}

    /**
     * Return the fact
     * @return
     */
    public Fact getFact() {
        return this.fact;
    }
    
    /**
     * The TestNode should call this method to set the fact. The fact should
     * never be null, since it has to have matched preceding patterns. We
     * may be able to remove the check for null. If the row id is less than
     * zero, it means the binding is an object binding.
     * @param facts
     */
    public void setFact(Fact[] facts){
        if (rowId > -1 && facts[rowId] != null) {
            this.fact = facts[rowId];
        }
    }
    
    /**
     * The method will return the Object instance for the given shadow fact
     * @return
     */
    public Object getObjectRef() {
        return this.fact.getObjectInstance();
    }
    
    /**
     * if the binding is bound to an object, the method will return true.
     * By default, the method will return false.
     * @return
     */
    public boolean isObjectBinding() {
        return this.objBinding;
    }
    
    /**
     * If the binding is bound to an object, call the method with true.
     * @param obj
     */
    public void setObjectBinding(boolean obj) {
        this.objBinding = obj;
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
     * In some cases, we need to know what the action for the parameter.
     * @return
     */
    public int getActionType() {
        return this.actionType;
    }
    
    /**
     * Set the action for this bound parameter
     * @param action
     */
    public void setActionType(int action) {
        this.actionType = action;
    }
    
    public void setRow(int row) {
    	this.rowId = row;
    }
    
    public void setColumn(int col) {
    	this.column = col;
    	if (this.column == -1) {
    		this.objBinding = true;
    	}
    }
    
    /**
     * reset sets the Fact handle to null
     */
    public void reset(){
        this.fact = null;
    }
    
    public String toPPString() {
    	if (this.isMultislot) {
        	return "$?" + this.variableName;
    	} else {
        	return "?" + this.variableName;
    	}
    }
    
    public BoundParam clone() {
    	BoundParam clone = new BoundParam(this.rowId, this.column, this.valueType, this.objBinding);
    	clone.actionType = this.actionType;
    	clone.fact = this.fact;
    	clone.isMultislot = this.isMultislot;
    	clone.resolvedVal = this.resolvedVal;
    	clone.variableName = this.variableName;
    	return clone;
    }
}
