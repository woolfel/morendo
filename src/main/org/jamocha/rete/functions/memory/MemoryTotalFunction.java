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
package org.jamocha.rete.functions.memory;

import java.io.Serializable;

import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;


public class MemoryTotalFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String MEMORY_TOTAL = "mem-total";
	
	public MemoryTotalFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Runtime rt = Runtime.getRuntime();
		long total = rt.totalMemory();
		engine.writeMessage("Total memory " +
				String.valueOf(total/1024) + "Kb | " +
				String.valueOf(total/1024/1024) + "Mb" +
				Constants.LINEBREAK,"t");
		DefaultReturnVector ret = new DefaultReturnVector();
		return ret;
	}

	public String getName() {
		return MEMORY_TOTAL;
	}

	public Class<?>[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(mem-total)";
	}

}
