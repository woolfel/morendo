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
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.util.ProfileStats;

public class PrintProfileCubeIndexFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PRINT_PROFILE_CUBE_INDEX = "print-profile-cube-index";

	public PrintProfileCubeIndexFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		engine.writeMessage("index Cube ET=" + ProfileStats.indexTime + " ms" + Constants.LINEBREAK, "t");
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return PRINT_PROFILE_CUBE_INDEX;
	}

	public Class<?>[] getParameter() {
		return new Class[0];
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(print-profile-cube-index)";
	}

}
