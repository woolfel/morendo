/*
 * Copyright 2006-2008 Nikolaus Koemm
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

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * @author Nikolaus Koemm
 * 
 */
public class NeqFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String NEQUAL = "neq";

	/**
	 * 
	 */
	public NeqFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector ret = new DefaultReturnVector();
		Boolean eq = Boolean.TRUE;
		if (params != null && params.length > 1) {
			Object first = params[0].getValue(engine, Constants.OBJECT_TYPE);
			for (int idx = 1; idx < params.length; idx++) {
				Object other = params[idx].getValue(engine, Constants.OBJECT_TYPE);
				if ( (  (first == null && other == null)     ||  
						(first != null && first.equals(other))  ) ) {
					eq = Boolean.FALSE;
					break;
				}
			}
		}
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, eq);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return NEQUAL;
	}

	public Class<?>[] getParameter() {
		return new Class[] { ValueParam[].class };
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(neq (<literal> | <binding>)+)\n" +
			"Function description:\n" +
			"\tCompares a literal value against one or more" +
			"bindings. \n\tIf all of the bindings are equal to the constant value," +
			"\n\tthe function returns true.";
	}

}
