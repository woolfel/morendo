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
package org.jamocha.rete.functions.string;

import java.io.Serializable;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;


/**
 * @author Peter Lin
 * Modified 22/5/21 - DAve Woodman. Returns 1-based index or false if not found (as CLIPS)
 *   args now [string to find] [string to search]
 *
 */
public class StringIndexFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String STRING_INDEX = "str-index";

	public StringIndexFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.INTEGER_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		int index = -1;
		if (params != null && params.length == 2) {
			if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[0];
				bp.resolveBinding(engine);
			}
			if (params[1] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[1];
				bp.resolveBinding(engine);
			}
			String pt = params[0].getStringValue();
			String val = params[1].getStringValue();
			index = val.indexOf(pt);
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		if (index == -1) {
			DefaultReturnValue rv = new DefaultReturnValue(
					Constants.BOOLEAN_OBJECT, Boolean.FALSE);
			ret.addReturnValue(rv);
		} else {
			DefaultReturnValue rv = new DefaultReturnValue(
					Constants.INTEGER_OBJECT, Integer.valueOf(++index));
			ret.addReturnValue(rv);
		}
		return ret;
	}

	public String getName() {
		return STRING_INDEX;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class,ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(str-index [string to find] [string to search]])";
	}

}
