/*
 * Copyright 2002-2010 Peter Lin
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
package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.rete.BaseSlot;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Defclass;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.RuleFunction;
import org.jamocha.rete.Slot;
import org.jamocha.rete.SlotParam;
import org.jamocha.rete.Template;
import org.jamocha.rete.exception.AssertException;

/**
 * @author Peter Lin
 *
 * ModifyFunction is equivalent to CLIPS modify function.
 */
public class DuplicateFunction implements RuleFunction, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String DUPLICATE = "duplicate";
    
    protected Fact[] triggerFacts = null;

    /**
	 * 
	 */
	public DuplicateFunction() {
		super();
	}

	public void setTriggerFacts(Fact[] facts) {
		this.triggerFacts = facts;
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean exec = Boolean.FALSE;
        if (params != null && params.length >= 2 &&
                params[0].isObjectBinding()) {
            BoundParam bp = (BoundParam)params[0];
            Deffact fact = (Deffact)bp.getFact();
            try {
            	if (fact.getObjectInstance() == null) {
                    // first retract the fact
                    Deffact clone = fact.cloneFact();
                    // now modify the fact
                    SlotParam[] sp = new SlotParam[params.length - 1];
                    for (int idx=0; idx < sp.length; idx++) {
                    	Parameter p = params[idx + 1];
                    	if (p instanceof SlotParam) {
                    		sp[idx] = (SlotParam)p;
                    	}
                    }
                    clone.updateSlots(engine, 
                    		convertToSlots(sp,fact.getDeftemplate()));
        			if (clone.hasBinding()) {
        				clone.resolveValues(engine,this.triggerFacts);
        			}
                    // now assert the fact using the same fact-id
                    engine.assertFact(clone);
                    exec = Boolean.TRUE;
            	}
            } catch (AssertException e) {
                engine.writeMessage(e.getMessage());
            }
        }
        
		DefaultReturnVector rv = new DefaultReturnVector();
		DefaultReturnValue rval = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, exec);
		rv.addReturnValue(rval);
		return rv;
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.Function#getName()
	 */
	public String getName() {
		return DUPLICATE;
	}

	/**
     * The current implementation expects 3 parameters in the following
     * sequence:<br/>
     * BoundParam
     * SlotParam[]
	 * <br/>
     * Example: (modify ?boundVariable (slotName value)* )
	 */
	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[] {BoundParam.class,SlotParam[].class};
	}

    /**
     * convert the SlotParam to Slot objects
     * @param params
     * @return
     */
    public BaseSlot[] convertToSlots(Parameter[] params, Template templ) {
        BaseSlot[] slts = new BaseSlot[params.length];
        for (int idx=0; idx < params.length; idx++) {
            slts[idx] = ((SlotParam)params[idx]).getSlotValue();
            int col = templ.getColumnIndex(slts[idx].getName());
            if (col != -1) {
                slts[idx].setId(col);
            }
        }
        return slts;
    }

    protected void updateObject(Rete engine, Deffact fact, Defclass defclass, Object instance, Parameter[] parameters) {
		for (int idx = 1; idx < parameters.length; idx++) {
			Parameter p = parameters[idx];
			if (p instanceof SlotParam) {
				SlotParam sp = (SlotParam)p;
				String field = sp.getSlotValue().getName();
				Object value = sp.getSlotValue().getValue();
				int col = fact.getDeftemplate().getSlot(field).getId();
				defclass.setFieldValue(col, instance, value);
			}
		}
    }
    
	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(" + DUPLICATE + " ");
			buf.append("?" + ((BoundParam)params[0]).getVariableName() + " ");
			for (int idx=1; idx < params.length; idx++) {
				// the parameter should be a deffact
				SlotParam sp = (SlotParam)params[idx];
				Slot s = sp.getSlotValue();
				if (s.getValue() instanceof BoundParam) {
					buf.append("(" + s.getName() + " ?" + 
							((BoundParam)s.getValue()).getVariableName() + ")");
				} else {
					buf.append("(" + s.getName() + " " + s.getValue() + ")");
				}
			}
			buf.append(" )");
			return buf.toString();
		} else {
			return "(duplicate [binding] [deffact])\n" +
					"Function description:\n" +
					"\tAllows the user to duplicate a deffacts.";
		}
	}
}
