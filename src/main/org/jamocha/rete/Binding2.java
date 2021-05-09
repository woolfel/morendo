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

/**
 * @author Peter Lin
 * 
 * Binding2 is used for bindings that are are numeric comparison like
 * >, <, >=, <=.
 */
public class Binding2 extends Binding {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int operator = Constants.EQUAL;
    protected Function function = null;
    protected Parameter[] params = null;
    protected String rightVariable = null;
    protected Object queryValue = null;
	
	/**
	 * 
	 */
	public Binding2(int operator) {
		super();
		this.operator = operator;
	}

	public int getOperator() {
		return this.operator;
	}

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public boolean isPredicate() {
        return isPredJoin;
    }

    public void setPredicate(boolean isPredicate) {
        this.isPredJoin = isPredicate;
    }
    
    public Parameter[] getParams() {
        return params;
    }

    public void setParams(Parameter[] params) {
        this.params = params;
    }
    
    public String getRightVariable() {
        return rightVariable;
    }

    public void setRightVariable(String rightVariable) {
        this.rightVariable = rightVariable;
    }
    
    public Object getQueryValue() {
    	return this.queryValue;
    }
    
    public void setQueryValue(Object value) {
    	this.queryValue = value;
    }
    
    public boolean evaluate(Fact[] left, Fact right, Rete engine) {
        if (this.function != null) {
        	Fact[] facts = new Fact[left.length + 1];
        	System.arraycopy(left, 0, facts, 0, left.length);
        	facts[left.length] = right;
    		Parameter[] funcParams = this.getParameters(facts, engine);
            ReturnVector rv = this.function.executeFunction(engine, funcParams);
            return rv.firstReturnValue().getBooleanValue();
        } else if (left.length > leftrow) {
            if (left[leftrow] == right) {
                return false;
            } 
            return Evaluate.evaluate(operator, left[leftrow].getSlotValue(leftIndex),right.getSlotValue(rightIndex));
        } else {
            return false;
        }
    }
    
    protected Parameter[] getParameters(Fact[] facts, Rete engine) {
    	if (params != null) {
        	Parameter[] newparams = new Parameter[this.params.length];
        	for (int i=0; i < this.params.length; i++) {
        		if (params[i] instanceof BoundParam) {
        			ValueParam vp = new ValueParam();
        			BoundParam bp = (BoundParam)params[i];
        			newparams[i] = vp;
        			vp.objBinding = bp.objBinding;
        			vp.valueType = bp.valueType;
        			vp.value = facts[bp.rowId].getSlotValue(bp.column);
        		} else if (params[i] instanceof FunctionParam2) {
        			FunctionParam2 fp = new FunctionParam2();
        			newparams[i] = fp;
        			fp.setEngine(engine);
        			fp.facts = facts;
        			fp.func = ((FunctionParam2)params[i]).func;
        			fp.funcName = ((FunctionParam2)params[i]).funcName;
        			fp.objBinding = ((FunctionParam2)params[i]).objBinding;
        		} else if (params[i] instanceof ValueParam) {
        			ValueParam vp = (ValueParam)params[i];
        			newparams[i] = vp.cloneParameter();
        		}
        	}
        	return newparams;
    	}
    	return this.params;
    }
    
    public String toBindString() {
        StringBuffer buf = new StringBuffer();
        buf.append("(" + this.leftrow + ")(");
        buf.append(this.leftIndex);
        if (function != null) {
            buf.append(") " + function.getName() + " (0)(");
        } else {
            buf.append(") " + ConversionUtils.getPPOperator(operator) + " (0)(");
        }
        buf.append(this.rightIndex);
        buf.append(") ?" + this.rightVariable);
        return buf.toString();
    }

    public String toPPString() {
        StringBuffer buf = new StringBuffer();
        if (!isPredJoin) {
            buf.append("?" + varName + " (" + this.leftrow + ")(");
            buf.append(this.leftIndex);
            if (function != null) {
                buf.append(") " + function.toPPString(params, 1) + " ");
            } else {
                buf.append(") " + ConversionUtils.getPPOperator(operator) + " (0)(");
            }
            buf.append(this.rightIndex);
            if (this.rightVariable != null) {
                buf.append(") ?" + this.rightVariable);
            } else {
            	buf.append(")");
            }
        } else {
        	buf.append(function.toPPString(params,1));
        }
        return buf.toString();
    }
}
