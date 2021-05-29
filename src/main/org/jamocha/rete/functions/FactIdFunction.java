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
package org.jamocha.rete.functions;

import java.io.Serializable;
import java.math.BigDecimal;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

/**
 * @author Peter Lin
 * 
 * FactIdFunction will lookup a fact by the id and try to print out the
 * string form of the given fact.
 */
public class FactIdFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String FACT_ID = "fact-id";

	/**
	 * 
	 */
	public FactIdFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector returnVector = new DefaultReturnVector();
        if (params != null && params.length > 0) {
        	for (int idx=0; idx < params.length; idx++) {
        		BigDecimal id = (BigDecimal)params[idx].getValue(engine, Constants.LONG_OBJECT);
        		Fact f = engine.getFactById(id.longValue());
        		if (f != null) {
        			engine.writeMessage(f.toFactString() + Constants.LINEBREAK);
            		DefaultReturnValue rv = new DefaultReturnValue(
            				Constants.FACT_TYPE, f);
            		returnVector.addReturnValue(rv);
        		}
        	}
        }
		return returnVector;
	}

	public String getName() {
		return FACT_ID;
	}

	public Class<?>[] getParameter() {
		return new Class[]{long.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(fact-id)\n" +
			"Function description:\n" +
			"\tPrints the string for the fact with the given id.";
	}
}
