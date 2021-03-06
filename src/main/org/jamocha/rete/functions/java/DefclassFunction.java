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
package org.jamocha.rete.functions.java;

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
 * @author Peter Lin
 * 
 */
public class DefclassFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DEFCLASS = "defclass";

	public DefclassFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean def = Boolean.TRUE;
		if (params.length >= 0) {
			String clazz = params[0].getStringValue();
			String template = null;
            if (params.length >= 2 && params[1] != null) {
                template = params[1].getStringValue();
            }
			String parent = null;
			if (params.length == 3) {
				parent = params[2].getStringValue();
			}
            try {
                engine.declareObject(clazz, template, parent);
            } catch (ClassNotFoundException e) {
                def = Boolean.FALSE;
            }
		} else {
			def = Boolean.FALSE;
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, def);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return DEFCLASS;
	}

	/**
	 * defclass function expects 3 parameters. (defclass classname,
	 * templatename, parenttemplate) parent template name is optional.
	 */
	public Class<?>[] getParameter() {
		return new Class[] { ValueParam.class, ValueParam.class,
				ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			buf.append("(defclass");
			for (int idx = 0; idx < params.length; idx++) {
				buf.append(" " + params[idx].getStringValue());
			}
			buf.append(")");
			return buf.toString();
		} else {
			return "(defclass [new classname] [template] [parent template])";
		}
	}
}
