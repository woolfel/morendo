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

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.Template;
import org.jamocha.rete.ValueParam;

/**
 * @author Peter Lin
 *
 */
public class UnDeftemplateFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String UNDEFTEMPLATE = "undeftemplate";

	public UnDeftemplateFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean removed = Boolean.TRUE;
		if (params.length == 1) {
			String template = params[0].getStringValue();
			Template t = engine.getCurrentFocus().getTemplate(template);
			if (!t.inUse()) {
				engine.getCurrentFocus().removeTemplate(t,engine,engine.getWorkingMemory());
			} else {
				removed = Boolean.FALSE;
			}
		} else {
			removed = Boolean.FALSE;
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, removed);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return UNDEFTEMPLATE;
	}

	/**
	 * The expected parameter is a single ValueParam containing a deftemplate
	 * instance. The function gets the deftemplate using Parameter.getValue().
	 */
	public Class<?>[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(undeftemplate name)";
		}
	}
}
