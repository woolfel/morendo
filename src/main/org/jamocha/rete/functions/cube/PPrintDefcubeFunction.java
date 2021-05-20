/*
 * Copyright 2002-2009 Jamocha
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
package org.jamocha.rete.functions.cube;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Cube;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class PPrintDefcubeFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PPDEFCUBE = "ppdefcube";
	
	public PPrintDefcubeFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				String name = params[idx].getStringValue();
				Cube c = engine.getCube(name);
				if (c != null) {
					engine.writeMessage(c.toPPString(), "t");
				}
			}
		}
		DefaultReturnVector rv = new DefaultReturnVector();
		return rv;
	}

	public String getName() {
		return PPDEFCUBE;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{String.class};
	}

	public int getReturnType() {
		return Constants.STRING_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length == 1) {
			StringBuffer buf = new StringBuffer();
			buf.append("(ppdefcube " + params[0].getStringValue());
			buf.append(")");
			return buf.toString();
		} else {
			return "(ppdefcube <name>)";
		}
	}
}
