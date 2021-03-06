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
 * Modified 22/5/21 Dave Woodman- make 1 based as per CLIPS
 */
public class SubStringFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String SUBSTRING = "sub-string";
	

	public SubStringFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.STRING_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		String sub = null;
		if (params != null && params.length == 3) {
			if (params[0] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[0];
				bp.resolveBinding(engine);
			}
			if (params[1] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[1];
				bp.resolveBinding(engine);
			}
			if (params[2] instanceof BoundParam) {
				BoundParam bp = (BoundParam)params[2];
				bp.resolveBinding(engine);
			}
			int begin = params[0].getIntValue() - 1;
			int end = params[1].getIntValue() - 1;
			String txt = params[2].getStringValue();
			int length = txt.length();
			if ((length != 0) && (begin < end ) && (end <= length)) {
			sub = txt.substring(begin,end);
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.STRING_TYPE, sub);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return SUBSTRING;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class,ValueParam.class,ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(sub-string [begin] [end] [string])";
	}

}
