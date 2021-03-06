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
package org.jamocha.rete.functions;

import java.io.Serializable;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Function;
import org.jamocha.rete.Module;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * @author Peter Lin
 *
 */
public class DeftemplateFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DEFTEMPLATE = "deftemplate";

	public DeftemplateFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean add = Boolean.TRUE;
		if (params.length == 1 && params[0].getValue() instanceof Deftemplate) {
			Deftemplate tpl = (Deftemplate) params[0].getValue();
			Module mod = tpl.checkName(engine);
			if (mod == null) {
				mod = engine.getCurrentFocus();
			}
			mod.addTemplate(tpl,engine,engine.getWorkingMemory());
		} else {
			add = Boolean.FALSE;
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT,add);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return DEFTEMPLATE;
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
			return "(deftemplate name (slot name (type [type]))+?)";
		}
	}
}
