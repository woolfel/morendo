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
import java.util.Collection;
import java.util.Iterator;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Module;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * @author Sebastian Reinartz
 * 
 */
public class ModulesFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String MODULES = "modules";

	public ModulesFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.STRING_TYPE;
	}


	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Collection<?> modules = engine.getWorkingMemory().getModules();
		int count = modules.size();
		Iterator<?> itr = modules.iterator();
		while (itr.hasNext()) {
			Module r = (Module) itr.next();
			engine.writeMessage(r.getModuleName() + Constants.LINEBREAK, "t");
		}
		engine.writeMessage("for a total of " + count + Constants.LINEBREAK,
				"t");
		DefaultReturnVector rv = new DefaultReturnVector();
		return rv;
	}

	public String getName() {
		return MODULES;
	}

	public Class<?>[] getParameter() {
		return new Class[] { ValueParam.class };
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(set-focus)";
	}

}
