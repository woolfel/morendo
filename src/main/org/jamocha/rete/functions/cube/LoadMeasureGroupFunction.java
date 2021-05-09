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
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.measures.MeasureGroup;

public class LoadMeasureGroupFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String LOAD_MEASURE_GROUP = "load-measure-group";
	
	public LoadMeasureGroupFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		boolean load = false;
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				String className = params[idx].getStringValue();
				try {
					Class clzz = Class.forName(className);
					MeasureGroup mGroup = (MeasureGroup)clzz.newInstance();
					engine.declareMeasureGroup(mGroup);
					load = true;
				} catch (ClassNotFoundException e) {
					engine.writeMessage("Could not find the class. Please double check and make sure it is the fully qualified class name.");
				} catch (InstantiationException e) {
					engine.writeMessage("Could not create new instance of the class.");
				} catch (IllegalAccessException e) {
					engine.writeMessage("Could not create new instance of the class.");
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(
				Constants.BOOLEAN_OBJECT, new Boolean(load));
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return LOAD_MEASURE_GROUP;
	}

	public Class[] getParameter() {
		return new Class[]{String[].class};
	}

	public int getReturnType() {
		return Constants.BOOLEAN_OBJECT;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(load-measure-group <class>+)";
	}

}
