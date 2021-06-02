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
import org.jamocha.rete.Function;
import org.jamocha.rete.Module;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;


/**
 * @author Peter Lin
 *
 */
public class GetCurrentModuleFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String GET_CURRENT_MODULE = "get-current-module";

	/**
	 * 
	 */
	public GetCurrentModuleFunction() {
		super();
	}

	public String getName() {
		return GET_CURRENT_MODULE;
	}

	public int getReturnType() {
		return Constants.STRING_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		DefaultReturnVector rvector = new DefaultReturnVector();
		Module module = engine.getCurrentFocus();
		DefaultReturnValue rval = new DefaultReturnValue(Constants.STRING_TYPE,module.getModuleName());
		rvector.addReturnValue(rval);
		return rvector;
	}

	public Class<?>[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(get-current-module)";
	}

}
