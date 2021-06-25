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
 * Modified by Dave Woodman - 21/6/21 to permit 
 * (bind ?fred (assert ....
 * (retract ?Fred)
 * 	
 * 
 */
package org.jamocha.rete.functions;

import java.io.Serializable;
import java.math.BigDecimal;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Deffact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.exception.RetractException;

public class RetractFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String RETRACT = "retract";

	public RetractFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector rv = new DefaultReturnVector();
		if (params != null && params.length >= 1) {
			for (int idx = 0; idx < params.length; idx++) {
				if (params[idx] instanceof BoundParam) {
					BoundParam bp = (BoundParam) params[idx];
					Deffact fact = (Deffact) bp.getFact();
					try {
						if (fact == null)
							fact = (Deffact) engine
									.getFactById(Long.parseLong(engine.getBinding(bp.getVariableName()).toString()));
						if (fact.getObjectInstance() != null) 
							engine.retractObject(fact.getObjectInstance());
						else engine.retractFact(fact);
						DefaultReturnValue rval = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, Boolean.TRUE);
						rv.addReturnValue(rval);
					} catch (RetractException | NumberFormatException e) {
						DefaultReturnValue rval = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, Boolean.FALSE);
						rv.addReturnValue(rval);
					}
				} else if (params[idx] instanceof ValueParam) {
					BigDecimal bi = params[idx].getBigDecimalValue();
					try {
						engine.retractById(bi.longValue());
						DefaultReturnValue rval = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, Boolean.TRUE);
						rv.addReturnValue(rval);
					} catch (RetractException e) {
						DefaultReturnValue rval = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, Boolean.FALSE);
						rv.addReturnValue(rval);
					}
				}
			}
		}
		return rv;
	}

	public String getName() {
		return RETRACT;
	}

	public Class<?>[] getParameter() {
		return new Class[] { BoundParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(retract [?binding|fact-id])\n" + "Function description:\n"
				+ "\tAllows the user to remove facts from the fact-list.";
	}

}
