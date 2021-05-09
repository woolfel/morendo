/*
 * Copyright 2002-2009 Peter Lin
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

import org.jamocha.rule.Query;
import org.jamocha.rule.Rule;

/**
 * @author Peter Lin
 *
 * Describe difference between the Function parameters
 */
public class FunctionParam2 extends AbstractParam {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected Function func = null;
    protected String funcName = null;
    private Parameter[] params = null;
    private Rete engine = null;
    protected Fact[] facts;

	public FunctionParam2() {
		super();
	}
	
	public void setFunctionName(String name) {
		this.funcName = name;
	}
	
	public String getFunctionName() {
		return this.funcName;
	}
	
	public void setEngine(Rete engine) {
		this.engine = engine;
	}
	
	public void configure(Rete engine, Rule util) {
		if (this.engine == null) {
			this.engine = engine;
		}
		for (int idx=0; idx < this.params.length; idx++) {
			if (this.params[idx] instanceof BoundParam) {
				// we need to set the row value if the binding is a slot or fact
				BoundParam bp = (BoundParam)this.params[idx];
				Binding b1 = util.getBinding(bp.getVariableName());
				if (b1 != null) {
					bp.setRow(b1.getLeftRow());
					bp.setColumn(b1.getLeftIndex());
					if (b1.getLeftIndex() == -1) {
						bp.setObjectBinding(true);
					}
				}
			}
		}
	}

	public void configure(Rete engine, Query util) {
		if (this.engine == null) {
			this.engine = engine;
		}
		for (int idx=0; idx < this.params.length; idx++) {
			if (this.params[idx] instanceof BoundParam) {
				// we need to set the row value if the binding is a slot or fact
				BoundParam bp = (BoundParam)this.params[idx];
				Binding b1 = util.getBinding(bp.getVariableName());
				if (b1 != null) {
					bp.setRow(b1.getLeftRow());
					bp.setColumn(b1.getLeftIndex());
					if (b1.getLeftIndex() == -1) {
						bp.setObjectBinding(true);
					}
				}
			}
		}
	}
	
	public void setParameters(Parameter[] params) {
		this.params = params;
	}
	
	public Parameter[] getParameters() {
		return this.params;
	}
	
	public boolean hasBoundParameter() {
		for (int idx=0; idx < params.length; idx++) {
			if (params[idx] instanceof BoundParam) {
				return true;
			}
		}
		return false;
	}
	
    public void lookUpFunction() {
        this.func = engine.findFunction(this.funcName);
    }

    public int getValueType() {
		return this.func.getReturnType();
	}

	public Object getValue() {
        if (this.params != null) {
        	if (this.facts != null) {
        		this.setFact();
        	}
            return this.func.executeFunction(engine,this.params);
        } else {
            return null;
        }
	}

	protected void setFact() {
		for (int idx=0; idx < this.params.length; idx++) {
			if (this.params[idx] instanceof BoundParam) {
				((BoundParam) this.params[idx]).setFact(this.facts);
			} else if (this.params[idx] instanceof FunctionParam) {
				((FunctionParam) this.params[idx]).setFacts(this.facts);
			}
		}
	}
	
    /**
     * TODO we may want to check the value type and throw and exception
     * for now just getting it to work.
     */
    public Object getValue(Rete engine, int valueType) {
        if (this.params != null) {
            this.engine = engine;
            lookUpFunction();
            checkParameters();
            ReturnVector rval = this.func.executeFunction(engine,this.params);
            if (valueType == Constants.BIG_DECIMAL) {
                return rval.firstReturnValue().getBigDecimalValue();
            } else if (valueType == Constants.OBJECT_TYPE || valueType == Constants.ARRAY_TYPE) {
            	return rval.firstReturnValue().getValue();
            } else if (valueType == Constants.INTEGER_OBJECT || valueType == Constants.INT_PRIM_TYPE) {
            	return rval.firstReturnValue().getIntValue();
            } else if (valueType == Constants.LONG_OBJECT || valueType == Constants.LONG_PRIM_TYPE) {
            	return rval.firstReturnValue().getLongValue();
            } else if (valueType == Constants.FLOAT_OBJECT || valueType == Constants.FLOAT_PRIM_TYPE) {
            	return rval.firstReturnValue().getFloatValue();
            } else if (valueType == Constants.DOUBLE_OBJECT || valueType == Constants.DOUBLE_PRIM_TYPE) {
            	return rval.firstReturnValue().getDoubleValue();
            } else {
            	return rval.firstReturnValue().getValue();
            }
        } else {
            return null;
        }
    }
    
    protected void checkParameters() {
    	for (int idx=0; idx < this.params.length; idx++) {
    		if (params[idx] instanceof BoundParam) {
    			((BoundParam)params[idx]).setFact(this.facts);
    		}
    	}
    }
    
	public void reset() {
		this.engine = null;
		this.params = null;
	}

	public String toPPString() {
		this.lookUpFunction();
		return this.func.toPPString(this.params,1);
	}

	public Fact[] getFacts() {
		return facts;
	}

	public void setFacts(Fact[] facts) {
		this.facts = facts;
	}
	
	public FunctionParam2 clone() {
		Parameter[] cloneParams = new Parameter[this.params.length];
		for (int i=0; i < params.length; i++) {
			if (params[i] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[i];
				cloneParams[i] = bp.clone();
			} else if (params[i] instanceof ValueParam) {
				ValueParam vp = (ValueParam)params[i];
				cloneParams[i] = vp.cloneParameter();
			} else if (params[i] instanceof FunctionParam) {
				FunctionParam fp = (FunctionParam)params[i];
				cloneParams[i] = fp.clone();
			} else if (params[i] instanceof FunctionParam2) {
				FunctionParam2 fp = (FunctionParam2)params[i];
				cloneParams[i] = fp.clone();
			}
		}
		FunctionParam2 clone = new FunctionParam2();
		clone.engine = this.engine;
		clone.facts = this.facts;
		clone.func = this.func;
		clone.funcName = this.funcName;
		clone.objBinding = this.objBinding;
		clone.params = cloneParams;
		return clone;
	}
}
