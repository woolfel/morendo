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
 *
 */
public class StringUpperFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String STRING_UPPER = "str-upper";
	

	public StringUpperFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.STRING_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		String txt = null;
		if (params != null && params.length == 1) {
			if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[0];
				bp.resolveBinding(engine);
			}
			txt = params[0].getStringValue();
			txt = txt.toUpperCase();
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.STRING_TYPE, txt);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return STRING_UPPER;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(str-upper [string])";
	}

}
