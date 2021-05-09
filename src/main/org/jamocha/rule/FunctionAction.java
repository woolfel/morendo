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
package org.jamocha.rule;

import org.jamocha.rete.Binding;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.RuleFunction;
import org.jamocha.rete.Slot;
import org.jamocha.rete.SlotParam;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.exception.ExecuteException;
import org.jamocha.rete.functions.*;

/**
 * @author Peter Lin
 *
 * A FunctionAction is responsible for executing a function in the action
 * of the rule. It uses built-in or user written functions. When the rule
 * is loaded, the engine looks up the functions. At run time, the rule
 * simply executes it.
 */
public class FunctionAction implements Action {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Function faction = null;
    protected String functionName = null;
    protected Parameter[] parameters = null;
    
	/**
	 * 
	 */
	public FunctionAction() {
		super();
	}
    
    public Function getFunction() {
        return this.faction;
    }
    
    public void setFunction(Function func) {
        if (func instanceof ShellFunction) {
            ShellFunction sf = (ShellFunction)func;
            this.functionName = sf.getName();
            this.parameters = sf.getParameters();
        } else {
            this.faction = func;
            this.functionName = func.getName();
        }
    }

    public String getFunctionName() {
        return this.functionName;
    }
    
    public void setFunctionName(String name) {
        this.functionName = name;
    }
    
    public Parameter[] getParameters() {
        return this.parameters;
    }
    
    public void setParameters(Parameter[] params) {
        this.parameters = params;
    }
    
    /**
     * Configure will lookup the function and set it
     */
    public void configure(Rete engine, Rule util) {
        if (this.functionName != null && 
                engine.findFunction(this.functionName) != null) {
            this.faction = engine.findFunction(this.functionName);
        }
        // now setup the BoundParameters if there are any
        for (int idx=0; idx < this.parameters.length; idx++) {
        	if (this.parameters[idx] instanceof BoundParam) {
        		BoundParam bp = (BoundParam)this.parameters[idx];
        		Binding bd = util.getBinding(bp.getVariableName());
        		if (bd != null) {
        			bp.setRow(bd.getLeftRow());
        			bp.setColumn(bd.getLeftIndex());
        		}
        	} else if (this.parameters[idx] instanceof FunctionParam2) {
        		FunctionParam2 fp2 = (FunctionParam2)this.parameters[idx];
        		fp2.configure(engine,util);
        	} else if (this.parameters[idx] instanceof SlotParam) {
        		// this means we are probably modify an object field that has an object
        		SlotParam sp = (SlotParam)this.parameters[idx];
        		if (sp.getSlotValue().getValue() instanceof BoundParam) {
        			BoundParam nestparam = (BoundParam)sp.getSlotValue().getValue();
        			//this.parameters[idx] = (Parameter)nestparam;
            		Binding bd = util.getBinding(nestparam.getVariableName());
            		if (bd != null) {
            			nestparam.setRow(bd.getLeftRow());
            			nestparam.setColumn(bd.getLeftIndex());
            		}
        		}
        	} else if (this.parameters[idx] instanceof ValueParam) {
        		ValueParam vp = (ValueParam)this.parameters[idx];
        		// if the value is a deffact, we need to check and make sure
        		// the slots with BoundParam value are compiled properly
        		if (vp.getValue() instanceof Deffact) {
        			((Deffact)vp.getValue()).compileBinding(util);
        		}
        	}
        }
        // in the case of Assert, we do further compilation
        if (this.faction instanceof AssertFunction) {
			Deftemplate tmpl = (Deftemplate) engine.getCurrentFocus()
			.getTemplate(this.parameters[0].getStringValue());
			Deffact	fact = (Deffact) tmpl.createFact(
					(Object[])this.parameters[1].getValue(),-1);
			fact.compileBinding(util);
			this.parameters = new ValueParam[1];
			this.parameters[0] = new ValueParam(Constants.OBJECT_TYPE,fact);
        }
    }
    
	/* (non-Javadoc)
	 * @see woolfel.engine.rule.Action#executeAction(woolfel.engine.rete.Rete, woolfel.engine.rete.Fact[])
	 */
	public void executeAction(Rete engine, Fact[] facts)    
			throws ExecuteException {
        // first we iterate over the parameters and pass the facts
        // to the BoundParams.
        for (int idx=0; idx < this.parameters.length; idx++) {
            if (this.parameters[idx] instanceof BoundParam) {
                ((BoundParam)this.parameters[idx]).setFact(facts);
            } else if (this.parameters[idx] instanceof SlotParam) {
            	// we have to check if the slot is an object
            	SlotParam sp = (SlotParam)this.parameters[idx];
            	Slot slot = sp.getSlotValue();
            	if (slot.getValue() instanceof BoundParam) {
            		BoundParam sbp = (BoundParam)slot.getValue();
            		sbp.setFact(facts);
            		// the binding could be from a query or function check the value isn't null
            		if (sbp.getValue() == null) {
            			BoundParam newbp = new BoundParam(-1, Constants.ARRAY_TYPE, false);
            			newbp.setVariableName(sbp.getVariableName());
            			slot.setValue(newbp);
            		}
            	}
            } else if (this.parameters[idx] instanceof FunctionParam) {
                ((FunctionParam)this.parameters[idx]).setFacts(facts);
            } else if (this.parameters[idx] instanceof FunctionParam2) {
            	((FunctionParam2)this.parameters[idx]).setEngine(engine);
            	((FunctionParam2)this.parameters[idx]).setFacts(facts);
            }
        }
        // If the function is a RuleFunction, we set the trigger facts
        if (this.faction instanceof RuleFunction) {
        	((RuleFunction)this.faction).setTriggerFacts(facts);
        }
        // now we find the function
        this.faction.executeFunction(engine,this.parameters);
	}

	/**
	 * method implements the necessary logic to print out the action
	 */
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		buf.append("  " + faction.toPPString(this.parameters,1) + Constants.LINEBREAK);
		return buf.toString();
	}
}
