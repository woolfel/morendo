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
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class ProfileCubeIndexFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String PROFILE_CUBE_INDEX = "profile-cube-index";

	public ProfileCubeIndexFunction() {
		super();
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Boolean profile = Boolean.FALSE;
		if (params != null && params.length > 0) {
			for (int idx=0; idx < params.length; idx++) {
				String cubename = params[idx].getStringValue();
				Cube c = engine.getCube(cubename);
				if (c != null) {
					c.setProfileIndex(true);
					profile = Boolean.TRUE;
				}
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.BOOLEAN_OBJECT, profile);
		ret.addReturnValue(rv);
		return ret;
	}

	public String getName() {
		return PROFILE_CUBE_INDEX;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{String.class};
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(profile-cube-index <cube>)";
	}

}
